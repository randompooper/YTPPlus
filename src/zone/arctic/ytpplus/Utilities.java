package zone.arctic.ytpplus;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.io.InputStreamReader;
import java.util.Random;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

/**
 * FFMPEG utilities toolbox for YTP+
 *
 * @author benb
 */
public class Utilities {

    private String FFPROBE;
    private String FFMPEG;
    private String MAGICK;

    private Random random = new Random();
    private String TEMP = "";
    private String SOURCES = "";
    private String SOUNDS = "";
    private String MUSIC = "";
    private String RESOURCES = "";

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

    public String getLength(String file) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(new String[] {
                getFFprobe(),
                "-i", file,
                "-show_entries", "format=duration",
                "-v", "error",
                "-of", "csv=p=0"
            });
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s;
            proc.waitFor();
            while ((s = stdInput.readLine()) != null) {
                return s;
            }
        } catch (Exception ex) {System.out.println(ex); return "";}
        return "";
    }

    /**
     * Snip a video file between the start and end time, and save it to an output file.
     *
     * @param video input video filename to work with
     * @param startTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param endTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param output output video filename to save the snipped clip to
     */
    public void snipVideo(String video, TimeStamp startTime, TimeStamp endTime, String output){
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
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }

    /**
     * Copies a video and encodes it in the proper format without changes.
     *
     * @param video input video filename to work with
     * @param output output video filename to save the snipped clip to
     */
    public void copyVideo(String video, String output){
        try {
            int exitValue = execFFmpeg(
                "-i", video,
                "-ac", "1",
                "-ar", "44100",
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-y", output
            );
            if (exitValue==1) {
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }

    /**
     * Concatenate videos by count
     *
     * @param count number of input videos to concatenate
     * @param out output video filename
     */
    public void concatenateVideo(int count, String out) {
        try {
            File export = new File(out);

            if (export.exists())
                export.delete();

            int realcount = 0;
            CommandLine cmdLine = new CommandLine(getFFmpeg());
            for (int i = 0; i < count; i++) {
                File vid = new File(TEMP + "video" + i + ".mp4");
                if (vid.exists()) {
                    cmdLine.addArgument("-i", false);
                    cmdLine.addArgument(vid.getPath(), false);
                    ++realcount;
                }
            }
            String filter = new String();
            for (int i=0; i < realcount; i++)
                filter += "[" + i + ":v:0][" + i + ":a:0]";

            cmdLine.addArguments(new String[] {
                "-filter_complex", filter + "concat=n=" + realcount + ":v=1:a=1[outv][outa]",
                "-map", "[outv]",
                "-map", "[outa]",
                "-y", out
            }, false);
            System.out.println(cmdLine);
            new DefaultExecutor().execute(cmdLine);
        } catch (Exception ex) {System.out.println(ex);}
    }

    public static int exec(String what, String ...args) throws Exception {
        CommandLine cmdLine = new CommandLine(what);
        cmdLine.addArguments(args, false);
        System.out.println("Command: " + cmdLine);
        return new DefaultExecutor().execute(cmdLine);
    }

    public int execFFmpeg(String ...args) throws Exception {
        return exec(getFFmpeg(), args);
    }

    public int execMagick(String ...args) throws Exception {
        return exec(getMagick(), args);
    }

    public int randomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public int randomInt(int max) {
        return random.nextInt(max + 1);
    }

    public int randomInt() {
        return randomInt((1 << 30) - 1);
    }

    public double randomDouble() {
        return random.nextDouble();
    }

    public double randomDouble(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    public boolean randomBool() {
        return random.nextBoolean();
    }

    public File getTempVideoFile() {
        File file;
        do
            file = new File(getTemp() + randomInt() + "-temp.mp4");
        while (file.exists());

        return file;
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
