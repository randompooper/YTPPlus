/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.lang.reflect.Method;

public class YTPGenerator {
    private double MAX_STREAM_DURATION = 0.4;
    private double MIN_STREAM_DURATION = 0.2;
    private int MAX_CLIPS = 20;
    private String OUTPUT_FILE;

    public Utilities toolBox = new Utilities();

    private Map<String, Integer> effects = new HashMap<String, Integer>();
    private int transitionClipChance = 0;
    private int effectChance = 50;

    EffectsFactory effectsFactory = new EffectsFactory(toolBox);
    ArrayList<String> sourceList = new ArrayList<String>();
    public volatile boolean done = false;
    public volatile double doneCount = 0;

    public YTPGenerator(String output) {
        setOutputFile(output);
    }

    public YTPGenerator(String output, double min, double max) {
        setOutputFile(output);
        setMinDuration(min);
        setMaxDuration(max);
    }

    public YTPGenerator(String output, double min, double max, int maxclips) {
        setOutputFile(output);
        setMinDuration(min);
        setMaxDuration(max);
        setMaxClips(maxclips);
    }

    public void setMaxClips(int clips) {
        MAX_CLIPS = clips;
    }

    public int getMaxClips() {
        return MAX_CLIPS;
    }

    public void setMinDuration(double min) {
        MIN_STREAM_DURATION = min;
    }

    public double getMinDuration() {
        return MIN_STREAM_DURATION;
    }

    public void setMaxDuration(double max) {
        MAX_STREAM_DURATION = max;
    }

    public double getMaxDuration() {
        return MAX_STREAM_DURATION;
    }

    public void setOutputFile(String out) {
        OUTPUT_FILE = out;
    }

    public String getOutputFile() {
        return OUTPUT_FILE;
    }

    public void setEffectChance(int chance) {
        effectChance = Math.min(100, chance);
    }

    public int getEffectChance() {
        return effectChance;
    }

    public void setTransitionClipChance(int chance) {
        transitionClipChance = Math.min(100, chance);
    }

    public int getTransitionClipChance() {
        return transitionClipChance;
    }

    public boolean addSource(String sourceName) {
        sourceList.add(sourceName);
        /* TO DO: Validate source before adding */
        return true;
    }
    public boolean setEffect(String name, int likelyness) {
        try {
            /* This method will throw exception if effect doesn't exist */
            EffectsFactory.class.getMethod("effect_" + name, String.class);
            if (likelyness > 0)
                effects.put(name, likelyness);
            else
                effects.remove(name);

            return true;
        } catch (Exception ex) {
            System.out.println("Failed to set effect: " + ex);
        }
        return false;
    }

    public void setupDefaultEffects() {
        effects.put("RandomSound", 10);
        effects.put("RandomSoundMute", 10);
        effects.put("Reverse", 10);
        effects.put("SlowDown", 10);
        effects.put("SpeedUp", 10);
        effects.put("Squidward", 10);
        effects.put("Vibrato", 10);
        effects.put("Chorus", 10);
        effects.put("Dance", 10);
        effects.put("HighPitch", 10);
        effects.put("LowPitch", 10);
        effects.put("Mirror", 10);
    }

    public void configurate() {
        //add some code to load this from a .cfg file later
        toolBox.FFMPEG = "ffmpeg";
        toolBox.FFPROBE = "ffprobe";
        toolBox.MAGICK = "magick";
        toolBox.TEMP = "temp/job_" + System.currentTimeMillis() + "/";
        new File(toolBox.TEMP).mkdir();
        toolBox.SOURCES = "sources/";
        toolBox.SOUNDS = "sounds/";
        toolBox.MUSIC = "music/";
        toolBox.RESOURCES = "resources/";
        setupDefaultEffects();

        setTransitionClipChance(6);
    }

    public void go() {
        System.out.println("My FFMPEG is: " + toolBox.FFMPEG);
        System.out.println("My FFPROBE is: " + toolBox.FFPROBE);
        System.out.println("My MAGICK is: " + toolBox.MAGICK);
        System.out.println("My TEMP is: " + toolBox.TEMP);
        System.out.println("My SOUNDS is: " + toolBox.SOUNDS);
        System.out.println("My SOURCES is: " + toolBox.SOURCES);
        System.out.println("My MUSIC is: " + toolBox.MUSIC);
        System.out.println("My RESOURCES is: " + toolBox.RESOURCES);
        if (sourceList.isEmpty()) {
            System.out.println("No sources added...");
            return;
        }
        try {
            effectsFactory.configureEffects(effects);
        } catch (Exception ex) {
            System.out.println("Failed to configure effects: " + ex);
            return;
        }
        Thread vidThread = new Thread() {
            public void run() {
                System.out.println("poop_1");
                File out = new File(getOutputFile());
                if (out.exists()) {
                    out.delete();
                }
                cleanUp();
                try {
                    IntStream.range(0, getMaxClips()).parallel().forEach(i -> {
                        String sourceToPick = sourceList.get(toolBox.randomInt(0, sourceList.size() - 1));
                        System.out.println(sourceToPick);
                        TimeStamp boy = new TimeStamp(Double.parseDouble(toolBox.getLength(sourceToPick)));
                        System.out.println("STARTING CLIP " + "video" + i + " of length " + boy.getLengthSec());
                        double start = 0.0, end = boy.getLengthSec();
                        if (end > getMinDuration()) {
                            start = toolBox.randomDouble(0.0, end - getMinDuration());
                            end = start + toolBox.randomDouble(getMinDuration(), Math.min(end - start, getMaxDuration()));
                        }
                        TimeStamp startOfClip = new TimeStamp(start);
                        TimeStamp endOfClip = new TimeStamp(end);
                        System.out.println("Beginning of clip " + i + ": " + startOfClip.getTimeStamp());
                        System.out.println("Ending of clip " + i + ": " + endOfClip.getTimeStamp() + ", in seconds: ");

                        String clipToWorkWith = toolBox.TEMP + "video" + i + ".mp4";
                        if (getTransitionClipChance() > 0 && toolBox.randomInt(0, 99) < getTransitionClipChance()) {
                            System.out.println("Tryina use a diff source");
                            toolBox.copyVideo(effectsFactory.pickSource(), clipToWorkWith);
                        } else {
                            toolBox.snipVideo(sourceToPick, startOfClip, endOfClip, clipToWorkWith);
                        }
                        if (toolBox.randomInt(0, 99) < getEffectChance())
                            effectsFactory.applyRandomEffect(clipToWorkWith);

                        doneCount += 1.0 / getMaxClips();
                    });
                    toolBox.concatenateVideo(getMaxClips(), getOutputFile());
                } catch (Exception ex) { ex.printStackTrace(); }
                rmDir(new File(toolBox.TEMP));
                done = true;
            }
        };
        vidThread.start();

    }


    public boolean isDone() {
        return done;
    }

    public void cleanUp() {
        File mp4 = new File(toolBox.TEMP + "temp.mp4");
        if (mp4.exists())
            mp4.delete();

        for (int i=0; i < getMaxClips(); i++) {
            File del = new File(toolBox.TEMP + "video"+i+".mp4");
            if (del.exists()) {
                System.out.println(i + " Exists");
                del.delete();
            }
        }

    }
    public void rmDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    rmDir(f);
                }
            }
        }
        file.delete();
    }

}
