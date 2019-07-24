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

    private int transitionClipChance = 0;
    private int effectChance = 50;

    private boolean lazySwitch = false;
    private String lazySwitchStartingSource = null;
    private int lazySwitchChance = 3;
    private int lazySwitchInterrupt = 20;
    private int lazySwitchMaxClips = 60;

    private String OUTPUT_FILE;

    private Utilities toolBox = new Utilities();

    private Map<String, Integer> effects = new HashMap<String, Integer>();

    private EffectsFactory effectsFactory = new EffectsFactory(toolBox);
    private ArrayList<String> sourceList = new ArrayList<String>();

    public class ProgressCallback {
        public void progress(double v) {}
        public void done(String errors) {}
    }

    private ProgressCallback report = new ProgressCallback();
    private volatile double doneProgress;

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
        effectChance = Math.max(Math.min(100, chance), 0);
    }

    public int getEffectChance() {
        return effectChance;
    }

    public void setTransitionClipChance(int chance) {
        transitionClipChance = Math.max(Math.min(100, chance), 0);
    }

    public int getTransitionClipChance() {
        return transitionClipChance;
    }

    public void setLazySwitch(boolean state) {
        lazySwitch = state;
    }

    public boolean getLazySwitch() {
        return lazySwitch;
    }

    public void setLazySwitchChance(int chance) {
        lazySwitchChance = chance;
    }

    public int getLazySwitchChance() {
        return lazySwitchChance;
    }

    public void setLazySwitchInterrupt(int chance) {
        lazySwitchInterrupt = chance;
    }

    public int getLazySwitchInterrupt() {
        return lazySwitchInterrupt;
    }

    public void setLazySwitchMaxClips(int count) {
        lazySwitchMaxClips = count;
    }

    public int getLazySwitchMaxClips() {
        return lazySwitchMaxClips;
    }

    public boolean setLazySwitchStartingSource(String path) {
        if (toolBox.getLength(path) < 0.0) {
            System.err.println("Source " + path + " was rejected by ffprobe");
            return false;
        }
        lazySwitchStartingSource = new String(path);
        return true;
    }

    public String getLazySwitchStartingSource() {
        return lazySwitchStartingSource;
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

    public void setProgressCallback(ProgressCallback clbk) {
        report = clbk;
    }

    public boolean addSource(String sourceName) {
        if (toolBox.getLength(sourceName) < 0.0) {
            System.err.println("Source " + sourceName + " was rejected by ffprobe");
            return false;
        }
        sourceList.add(new String(sourceName));
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
            System.err.println("Failed to set effect: " + ex);
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

    private String randomSource() {
        return sourceList.get(toolBox.randomInt(sourceList.size() - 1));
    }

    public void go() {
        if (sourceList.isEmpty()) {
            report.done("No sources added ...");
            return;
        }
        try {
            effectsFactory.configureEffects(effects);
        } catch (Exception ex) {
            report.done("Failed to configure effects: " + ex);
            return;
        }
        final String sources[];
        if (getLazySwitch() && sourceList.size() > 1) {
            String pick = lazySwitchStartingSource != null ? lazySwitchStartingSource : randomSource();
            sources = new String[getMaxClips()];
            for (int i = 0, count = 0; i < getMaxClips(); ++i) {
                if (toolBox.probability(getLazySwitchChance()) || count++ == getLazySwitchMaxClips()) {
                    String oldPick = pick;
                    while ((pick = randomSource()) == oldPick);
                    count = 0;
                }
                if (toolBox.probability(getLazySwitchInterrupt()))
                    while ((sources[i] = randomSource()) == pick);
                else
                    sources[i] = pick;
            }
        } else
            sources = null;

        File out = new File(getOutputFile());
        if (out.exists())
            out.delete();

        doneProgress = 0.0;
        cleanUp();
        try {
            IntStream.range(0, getMaxClips()).parallel().forEach(i -> {
                String clipToWorkWith = toolBox.getTemp() + "video" + i + ".mp4";
                if (toolBox.probability(getTransitionClipChance())) {
                    String clip = effectsFactory.pickSource().getFirst();
                    System.err.println("Transition clip: " + clip);
                    toolBox.copyVideo(clip, clipToWorkWith);
                } else {
                    String sourceToPick;

                    if (sources != null)
                        sourceToPick = sources[i];
                    else
                        sourceToPick = randomSource();

                    System.err.println(sourceToPick);
                    double clipLength = toolBox.getLength(sourceToPick);
                    TimeStamp boy = new TimeStamp(clipLength);

                    double start = 0.0, end = clipLength;
                    if (end > getMinDuration()) {
                        start = toolBox.randomDouble(0.0, end - getMinDuration());
                        end = start + toolBox.randomDouble(getMinDuration(), Math.min(end - start, getMaxDuration()));
                    }

                    TimeStamp startOfClip = new TimeStamp(start);
                    TimeStamp endOfClip = new TimeStamp(end);
                    System.err.println("Clip (" + clipLength + ") " + i + " " + startOfClip.getTimeStamp() + " - " + endOfClip.getTimeStamp());

                    toolBox.snipVideo(sourceToPick, startOfClip, endOfClip, clipToWorkWith);
                }
                if (toolBox.probability(getEffectChance()))
                    effectsFactory.applyRandomEffect(clipToWorkWith);

                doneProgress += 1.0 / getMaxClips();
                report.progress(doneProgress);
            });
            toolBox.concatenateVideo(getMaxClips(), getOutputFile());
        } catch (Exception ex) {
            ex.printStackTrace();
            report.done(ex.toString());
            /* Don't remove temp folder; let user investigate matter or
             * manually concat what was generated
             */
            return;
        }
        Utilities.rmDir(new File(toolBox.getTemp()));
        report.done(null);
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
                System.err.println(i + " Exists");
                del.delete();
            }
        }
    }
}
