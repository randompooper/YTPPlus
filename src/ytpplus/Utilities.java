/* Copyright 2019 randompooper, philosophofee (Ben Brown)
 * This file is part of YTPPlus.
 *
 * YTPPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * YTPPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with YTPPlus.  If not, see <https://www.gnu.org/licenses/>.
 */
package ytpplus;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.stream.IntStream;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import ytpplus.Pair;

public class Utilities {
    private ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    public void setFFmpeg(String path) {
        FFMPEG = new String(path);
    }

    public String getFFmpeg() {
        return FFMPEG;
    }

    public void setFFprobe(String path) {
        FFPROBE = new String(path);
    }

    public String getFFprobe() {
        return FFPROBE;
    }

    public void setMagick(String path) {
        MAGICK = new String(path);
    }

    public String getMagick() {
        return MAGICK;
    }

    public void setTemp(String path) {
        TEMP = path;
    }

    public String getTemp() {
        return TEMP;
    }

    public void setSources(String path) {
        SOURCES = path;
    }

    public String getSources() {
        return SOURCES;
    }

    public void setSounds(String path) {
        SOUNDS = path;
    }

    public String getSounds() {
        return SOUNDS;
    }

    public void setMusic(String path) {
        MUSIC = path;
    }

    public String getMusic() {
        return MUSIC;
    }

    public void setResources(String path) {
        RESOURCES = path;
    }

    public String getResources() {
        return RESOURCES;
    }

    public double getLength(String file) {
        Double v;

        if ((v = lenCache.get(file)) != null)
            return v;

        try {
            Process proc = execProc(getFFprobe(),
                "-i", file,
                "-show_entries", "format=duration",
                "-v", "error",
                "-of", "csv=p=0");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            int exitValue = proc.waitFor();
            if (exitValue != 0)
                return -1.0;

            String s;
            while ((s = stdInput.readLine()) != null) {
                lenCache.put(file, (v = Double.parseDouble(s)));
                return v;
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return -1.0;
    }

    public void resetLengthCache() {
        lenCache = new HashMap<String, Double>();
    }

    public boolean isVideoAudioPresent(String file) {
        try {
            boolean videoPresent = false, audioPresent = false;

            Process proc = execProc(getFFprobe(),
                "-loglevel", "error",
                "-show_entries", "stream=codec_type",
                "-of", "csv=p=0",
                file);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            int exitValue = proc.waitFor();
            if (exitValue != 0)
                return false;

            String s;
            while ((s = stdInput.readLine()) != null) {
                if (s.equals("video"))
                    videoPresent = true;
                else if (s.equals("audio"))
                    audioPresent = true;
            }
            if (!audioPresent || !videoPresent) {
                System.err.println("File: " + file);
                System.err.println("Audio present: " + audioPresent);
                System.err.println("Video present: " + videoPresent);
                System.err.println("Length: " + getLength(file));
            }
            return videoPresent && audioPresent;
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return false;
    }

    public void snipVideo(String video, TimeStamp startTime, TimeStamp endTime, String output) {
        try {
            int exitValue = execFFmpeg(
                "-ss", startTime.getTimeStamp(),
                "-i", video,
                "-to", endTime.getTimeStamp(),
                "-copyts",
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-af", "aresample=async=1000",
                "-ac", "1",
                "-ar", "44100",
                "-avoid_negative_ts", "make_zero",
                "-y", output
            );
            if (exitValue == 1) {
                System.err.println("Failed to snip video!");
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public void copyVideo(String video, String output) {
        try {
            int exitValue = execFFmpeg(
                "-i", video,
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-af", "aresample=async=1000",
                "-ac", "1",
                "-ar", "44100",
                "-avoid_negative_ts", "make_zero",
                "-y", output
            );
            if (exitValue == 1) {
                System.err.println("Failed to copy video!");
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public void concatenateVideoDemuxer(int count, String out) throws Exception {
        PrintWriter writer = new PrintWriter(getTemp() + "/concat.txt", "UTF-8");
        for (int i = 0; i < count; i++) {
            File vid = new File(getTemp() + "/video" + i + ".mp4");
            if (vid.exists() && isVideoAudioPresent(vid.getPath()))
                writer.write("file 'video" + i + ".mp4'\n");
        }
        writer.close();
        execFFmpeg("-f", "concat", "-i", getTemp() + "/concat.txt", "-c", "copy", out);
    }

    public void concatenateVideoConcatProto(int count, String out) throws Exception {
        /* 1. Loselessly convert mp4 to MPEG-2 transport streams */
        IntStream.range(0, count).parallel().forEach(i -> {
            try {
                File vid = new File(getTemp() + "/video" + i + ".mp4");
                if (vid.exists() && isVideoAudioPresent(vid.getPath())) {
                    //File temp = getTempVideoFile();
                    // Reencode :(
                    //copyVideo(vid.getPath(), temp.getPath());
                    //vid.renameTo(temp);
                    // And losslessly transcode
                    execFFmpeg("-i", vid.getPath(), "-c", "copy",
                        "-bsf:v", "h264_mp4toannexb", "-f", "mpegts",
                        getTemp() + "/video" + i + ".ts");

                    vid.delete();
                } else
                    vid.delete();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        });
        /* 2. Make concat list */
        String concatLine = "concat:";
        for (int i = 0; i < count; ++i) {
            File vid = new File(getTemp() + "/video" + i + ".ts");
            if (vid.exists())
                concatLine += vid.getPath() + (i == count - 1 ? "" : "|");
        }
        /* 3. Finally concat */
        execFFmpeg("-i", concatLine, "-c", "copy", "-bsf:a",
            "aac_adtstoasc", out);
    }

    public void concatenateVideoConcatFilter(int count, String out) throws Exception {
        int realcount = 0;

        ArrayList<String> cmdLine = new ArrayList<String>();
        cmdLine.add(getFFmpeg());

        for (int i = 0; i < count; i++) {
            File vid = new File(TEMP + "video" + i + ".mp4");
            if (vid.exists() && isVideoAudioPresent(vid.getPath())) {
                cmdLine.add("-i");
                cmdLine.add(vid.getPath());
                ++realcount;
            }
        }
        String filter = new String();
        for (int i=0; i < realcount; i++)
            filter += "[" + i + ":v:0][" + i + ":a:0]";

        cmdLine.add("-filter_complex");
        cmdLine.add(filter + "concat=n=" + realcount + ":v=1:a=1[outv][outa]");
        cmdLine.add("-map");
        cmdLine.add("[outv]");
        cmdLine.add("-map");
        cmdLine.add("[outa]");
        cmdLine.add("-y");
        cmdLine.add(out);
        System.err.println("Command line: " + cmdLine);
        exec(cmdLine.toArray(new String[cmdLine.size()]));
    }

    public static Process execProc(String ...args) throws Exception {
        return Runtime.getRuntime().exec(args);
    }

    public static int exec(String ...args) throws Exception {
        Process proc = execProc(args);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String s;
        while ((s = stdInput.readLine()) != null)
            /* WINDOWS BUG: You have to eat all output to make waitFor() return */
            /*System.err.println(s)*/;

        return proc.waitFor();
    }

    public static int execWhat(String what, String ...args) throws Exception {
        String[] cmdLine = new String[args.length + 1];
        cmdLine[0] = what;
        System.arraycopy(args, 0, cmdLine, 1, args.length);

        return exec(cmdLine);
    }

    public int execFFmpeg(String ...args) throws Exception {
        return execWhat(getFFmpeg(), args);
    }

    public int execMagick(String ...args) throws Exception {
        return execWhat(getMagick(), args);
    }

    public int randomInt(int min, int max) {
        return random().nextInt(min, max + 1);
    }

    public int randomInt(int max) {
        return random().nextInt(max + 1);
    }

    public int randomInt() {
        return randomInt((1 << 30) - 1);
    }

    public double randomDouble() {
        return random().nextDouble();
    }

    public double randomDouble(double min, double max) {
        return random().nextDouble(min, max);
    }

    public boolean randomBool() {
        return random().nextBoolean();
    }

    public File getTempVideoFile() {
        File file;
        do
            file = new File(getTemp() + "/" + randomInt() + "-temp.mp4");
        while (file.exists());

        return file;
    }

    public boolean probability(double prob) {
        if (prob >= 100.0)
            return true;

        if (prob <= 0.0)
            return false;

        return random().nextDouble(0.0, 100.0) < prob;
    }

    public Pair<String, Double> pickRandomMediaFile(String path) {
        File file;
        double length;

        File[] files = new File(path).listFiles();
        do {
            file = files[randomInt(files.length - 1)];
            length = getLength(file.getPath());
            if (length < 0.0)
                System.err.println("WARNING! File " + file.getPath() + " was rejected by ffprobe");

        } while (length < 0.0);

        return new Pair<>(file.getPath(), length);
    }

    public static double clamp(double v, double min, double max) {
        return Math.min(max, Math.max(min, v));
    }

    public static void rmDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    rmDir(f);
                }
            }
        }
        file.delete();
    }

    private Map<String, Double> lenCache = new HashMap<String, Double>();

    private String FFPROBE = "ffprobe";
    private String FFMPEG = "ffmpeg";
    private String MAGICK = "magick";

    private String TEMP = "temp/job";
    private String SOURCES = "sources";
    private String SOUNDS = "sounds";
    private String MUSIC = "music";
    private String RESOURCES = "resources";
}
