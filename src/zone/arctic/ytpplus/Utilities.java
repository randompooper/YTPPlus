package zone.arctic.ytpplus;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.stream.IntStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * FFMPEG utilities toolbox for YTP+
 *
 * @author benb
 */
public class Utilities {

    private String FFPROBE;
    private String FFMPEG;
    private String MAGICK;

    private String TEMP = "";
    private String SOURCES = "";
    private String SOUNDS = "";
    private String MUSIC = "";
    private String RESOURCES = "";

    public class Pair<T, E> {
        public Pair() {}

        public Pair(T first, E second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public void setFirst(T first) {
            this.first = first;
        }

        public E getSecond() {
            return second;
        }

        public void setSecond(E second) {
            this.second = second;
        }

        private T first;
        private E second;
    }

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
        TEMP = path + "/";
    }

    public String getTemp() {
        return TEMP;
    }

    public void setSources(String path) {
        SOURCES = path + "/";
    }

    public String getSources() {
        return SOURCES;
    }

    public void setSounds(String path) {
        SOUNDS = path + "/";
    }

    public String getSounds() {
        return SOUNDS;
    }

    public void setMusic(String path) {
        MUSIC = path + "/";
    }

    public String getMusic() {
        return MUSIC;
    }

    public void setResources(String path) {
        RESOURCES = path + "/";
    }

    public String getResources() {
        return RESOURCES;
    }

    public double getLength(String file) {
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
                return Double.parseDouble(s);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return -1.0;
    }

    /**
     * Snip a video file between the start and end time, and save it to an output file.
     *
     * @param video input video filename to work with
     * @param startTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param endTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param output output video filename to save the snipped clip to
     */
    public void snipVideo(String video, TimeStamp startTime, TimeStamp endTime, String output) {
        try {
            int exitValue = execFFmpeg(
                "-ss", startTime.getTimeStamp(),
                "-i", video,
                "-to", endTime.getTimeStamp(),
                "-copyts",
                "-ac", "1",
                "-ar", "44100",
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-y", output
            );
            if (exitValue==1) {
                System.err.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.err.println(ex);}
    }

    /**
     * Copies a video and encodes it in the proper format without changes.
     *
     * @param video input video filename to work with
     * @param output output video filename to save the snipped clip to
     */
    public void copyVideo(String video, String output) {
        try {
            int exitValue = execFFmpeg(
                "-i", video,
                "-ac", "1",
                "-ar", "44100",
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-y", output
            );
            if (exitValue==1) {
                System.err.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.err.println(ex);}
    }

    /**
     * Concatenate videos by count
     *
     * @param count number of input videos to concatenate
     * @param out output video filename
     */
    public void concatenateVideo(int count, String out, boolean quality) {
        try {
            if (quality) {
                IntStream.range(0, count).parallel().forEach(i -> {
                    try {
                        File vid = new File(getTemp() + "video" + i + "." + getVideoExtension());
                        if (vid.exists() && isVideoAudioPresent(vid.getPath())) {
                            File temp = getTempVideoFile();
                            /* Simply force encoding and concat using demuxer */
                            vid.renameTo(temp);
                            copyVideo(temp.getPath(), vid.getPath());
                        } else
                            vid.delete();
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                });
                PrintWriter writer = new PrintWriter(getTemp() + "concat.txt", "UTF-8");
                for (int i = 0; i < count; i++) {
                    File vid = new File(getTemp() + "video" + i + ".mp4");
                    if (vid.exists())
                        writer.write("file 'video" + i + ".mp4'\n");

                }
                writer.close();
                execFFmpeg("-f", "concat", "-i", getTemp() + "/concat.txt", "-c", "copy", out);
            } else {
                /* 1. Loselessly convert mp4 to MPEG-2 transport streams */
                IntStream.range(0, count).parallel().forEach(i -> {
                    try {
                        File vid = new File(getTemp() + "video" + i + "." + getVideoExtension());
                        if (vid.exists() && isVideoAudioPresent(vid.getPath())) {
                            //File temp = getTempVideoFile();
                            // Reencode :(
                            //copyVideo(vid.getPath(), temp.getPath());
                            //vid.renameTo(temp);
                            // And losslessly transcode
                            execFFmpeg("-i", vid.getPath(), "-c", "copy",
                                "-bsf:v", "h264_mp4toannexb", "-f", "mpegts",
                                getTemp() + "video" + i + ".ts");

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
                    File vid = new File(getTemp() + "video" + i + ".ts");
                    if (vid.exists())
                        concatLine += vid.getPath() + (i == count - 1 ? "" : "|");
                }
                execFFmpeg("-i", concatLine, "-c", "copy", "-bsf:a",
                    "aac_adtstoasc", out);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public static Process execProc(String ...args) throws Exception {
        return Runtime.getRuntime().exec(args);
    }

    public static int exec(String ...args) throws Exception {
        Process proc = execProc(args);
        /*BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String s;
        while ((s = stdInput.readLine()) != null)
            System.err.println(s);*/

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
            file = new File(getTemp() + randomInt() + "-temp.mp4");
        while (file.exists());

        return file;
    }

    public boolean probability(int prob) {
        if (prob >= 100)
            return true;

        if (prob <= 0)
            return false;

        return random().nextInt(99) < prob;
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

        return new Pair<String, Double>(file.getPath(), length);
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
}
