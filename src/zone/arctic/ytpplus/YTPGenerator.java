/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.io.File;
import java.io.PrintWriter;
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

    private Utilities toolBox = new Utilities();

    private Map<String, Integer> effects = new HashMap<String, Integer>();
    private int transitionClipChance = 0;
    private int effectChance = 50;

    private EffectsFactory effectsFactory = new EffectsFactory(toolBox);
    private ArrayList<String> sourceList = new ArrayList<String>();

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
        OUTPUT_FILE = new String(out);
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
        sourceList.add(new String(sourceName));
        /* TO DO: Validate source before adding */
        return true;
    }

    public void setFFmpeg(String path) {
        toolBox.setFFmpeg(path);
    }

    public String getFFmpeg() {
        return toolBox.getFFmpeg();
    }

    public void setFFprobe(String path) {
        toolBox.setFFprobe(path);
    }

    public String getFFprobe() {
        return toolBox.getFFprobe();
    }

    public void setMagick(String path) {
        toolBox.setMagick(path);
    }

    public String getMagick() {
        return toolBox.getMagick();
    }

    public void setTemp(String path) {
        toolBox.setTemp(path);
    }

    public String getTemp() {
        return toolBox.getTemp();
    }

    public void setSources(String path) {
        toolBox.setSources(path);
    }

    public String getSources() {
        return toolBox.getSources();
    }

    public void setSounds(String path) {
        toolBox.setSounds(path);
    }

    public String getSounds() {
        return toolBox.getSounds();
    }

    public void setMusic(String path) {
        toolBox.setMusic(path);
    }

    public String getMusic() {
        return toolBox.getMusic();
    }

    public void setResources(String path) {
        toolBox.setResources(path);
    }

    public String getResources() {
        return toolBox.getResources();
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
        setFFmpeg("ffmpeg");
        setFFprobe("ffprobe");
        setMagick("magick");
        setTemp("temp/job_" + System.currentTimeMillis() + "/");
        new File(toolBox.getTemp()).mkdir();
        setSources("sources/");
        setSounds("sounds/");
        setMusic("music/");
        setResources("resources/");
        setupDefaultEffects();

        setTransitionClipChance(6);
    }

    public void go() {
        System.out.println("My FFMPEG is: " + toolBox.getFFmpeg());
        System.out.println("My FFPROBE is: " + toolBox.getFFprobe());
        System.out.println("My MAGICK is: " + toolBox.getMagick());
        System.out.println("My TEMP is: " + toolBox.getTemp());
        System.out.println("My SOUNDS is: " + toolBox.getSounds());
        System.out.println("My SOURCES is: " + toolBox.getSources());
        System.out.println("My MUSIC is: " + toolBox.getMusic());
        System.out.println("My RESOURCES is: " + toolBox.getResources());
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
                        String sourceToPick = sourceList.get(toolBox.randomInt(sourceList.size() - 1));
                        System.out.println(sourceToPick);
                        double clipLength = Double.parseDouble(toolBox.getLength(sourceToPick));
                        TimeStamp boy = new TimeStamp(clipLength);
                        System.out.println("STARTING CLIP " + "video" + i + " of length " + clipLength);
                        double start = 0.0, end = clipLength;
                        if (end > getMinDuration()) {
                            start = toolBox.randomDouble(0.0, end - getMinDuration());
                            end = start + toolBox.randomDouble(getMinDuration(), Math.min(end - start, getMaxDuration()));
                        }
                        TimeStamp startOfClip = new TimeStamp(start);
                        TimeStamp endOfClip = new TimeStamp(end);
                        System.out.println("Beginning of clip " + i + ": " + startOfClip.getTimeStamp());
                        System.out.println("Ending of clip " + i + ": " + endOfClip.getTimeStamp() + ", in seconds: ");

                        String clipToWorkWith = toolBox.getTemp() + "video" + i + ".mp4";
                        if (getTransitionClipChance() > 0 && toolBox.randomInt(99) < getTransitionClipChance()) {
                            System.out.println("Tryina use a diff source");
                            toolBox.copyVideo(effectsFactory.pickSource(), clipToWorkWith);
                        } else {
                            toolBox.snipVideo(sourceToPick, startOfClip, endOfClip, clipToWorkWith);
                        }
                        if (toolBox.randomInt(99) < getEffectChance())
                            effectsFactory.applyRandomEffect(clipToWorkWith);

                        doneCount += 1.0 / getMaxClips();
                    });
                    toolBox.concatenateVideo(getMaxClips(), getOutputFile());
                } catch (Exception ex) { ex.printStackTrace(); }
                Utilities.rmDir(new File(toolBox.getTemp()));
                done = true;
            }
        };
        vidThread.start();

    }

    public boolean isDone() {
        return done;
    }

    public void cleanUp() {
        //Create concatenation text file
        File text = new File(toolBox.getTemp() + "concat.txt");
        if (text.exists())
            text.delete();
        File mp4 = new File(toolBox.getTemp() + "temp.mp4");
        if (mp4.exists())
            mp4.delete();

        for (int i=0; i < getMaxClips(); i++) {
            File del = new File(toolBox.getTemp() + "video" + i + ".mp4");
            if (del.exists()) {
                System.out.println(i + " Exists");
                del.delete();
            }
        }
    }
}
