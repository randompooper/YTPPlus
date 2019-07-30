/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.lang.reflect.Method;
import zone.arctic.ytpplus.Utilities.Pair;

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

    private boolean lazySeek = false;
    private boolean lazySeekFromStart = false;
    private int lazySeekChance = 5;
    private int lazySeekInterrupt = 40;
    private int lazySeekMaxClips = -1;
    private int lazySeekSameChance = 10;
    private boolean lazySeekNearby = true;
    private double lazySeekNearbyMin = 0.2;
    private double lazySeekNearbyMax = 5.0;

    private boolean reconvertEffected = true;

    private String OUTPUT_FILE;

    private Utilities toolBox = new Utilities();

    private Map<String, Integer> effects = new HashMap<String, Integer>();

    private EffectsFactory effectsFactory = new EffectsFactory(toolBox);
    private ArrayList<String> sourceList = new ArrayList<String>();

    public class ProgressCallback {
        public void progress(double v) {}
        public void done(String errors) {}
    }

    public class Script {
        public boolean whole() {
            return interval == null;
        }

        public String file;
        public Pair<TimeStamp, TimeStamp> interval;
        public boolean applyEffect;
    }

    private ProgressCallback report = new ProgressCallback();
    private volatile double doneProgress;

    public YTPGenerator() {
        setOutputFile("temp/tempoutput." + getVideoExtension());
    }

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

    public void setLazySeek(boolean state) {
        lazySeek = state;
    }

    public boolean getLazySeek() {
        return lazySeek;
    }

    public void setLazySeekFromStart(boolean state) {
        lazySeekFromStart = state;
    }

    public boolean getLazySeekFromStart() {
        return lazySeekFromStart;
    }

    public void setLazySeekChance(int chance) {
        lazySeekChance = chance;
    }

    public int getLazySeekChance() {
        return lazySeekChance;
    }

    public void setLazySeekInterrupt(int chance) {
        lazySeekInterrupt = chance;
    }

    public int getLazySeekInterrupt() {
        return lazySeekInterrupt;
    }

    public void setLazySeekMaxClips(int count) {
        lazySeekMaxClips = count;
    }

    public int getLazySeekMaxClips() {
        return lazySeekMaxClips;
    }

    public void setLazySeekNearby(boolean state) {
        lazySeekNearby = state;
    }

    public boolean getLazySeekNearby() {
        return lazySeekNearby;
    }

    public void setLazySeekNearbyMin(double time) {
        lazySeekNearbyMin = time;
    }

    public double getLazySeekNearbyMin() {
        return lazySeekNearbyMin;
    }

    public void setLazySeekNearbyMax(double time) {
        lazySeekNearbyMax = time;
    }

    public double getLazySeekNearbyMax() {
        return lazySeekNearbyMax;
    }

    public void setLazySeekSameChance(int chance) {
        lazySeekSameChance = chance;
    }

    public int getLazySeekSameChance() {
        return lazySeekSameChance;
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

    public void setReconvertEffected(boolean state) {
        reconvertEffected = state;
    }

    public boolean getReconvertEffected() {
        return reconvertEffected;
    }

    public void setVideoExtension(String ext) {
        toolBox.setVideoExtension(ext);
    }

    public String getVideoExtension() {
        return toolBox.getVideoExtension();
    }

    public void setProgressCallback(ProgressCallback clbk) {
        report = clbk;
    }

    public boolean addSource(String sourceName) {
        if (!toolBox.isVideoAudioPresent(sourceName)) {
            System.err.println("Source " + sourceName + " was rejected by ffprobe: " +
                "it might lack video or audio stream or might be not media file at all");
            return false;
        }
        sourceList.add(new String(sourceName));
        return true;
    }

    public void removeSource(String sourceName) {
        while (sourceList.remove(sourceName));
    }

    public void clearSources() {
        sourceList.clear();
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

    private Pair<TimeStamp, TimeStamp> randomInterval(String video, double start) {
        double end = toolBox.getLength(video);
        if (end > getMinDuration()) {
            if (start < 0.0 || start >= end)
                start = toolBox.randomDouble(0.0, end - getMinDuration());

            //System.err.println("start " + start + ";end " + end);
            //System.err.println("" + getMinDuration() + " " + Math.min(end - start, getMaxDuration()));
            if (end - start > getMinDuration())
                end = start + toolBox.randomDouble(getMinDuration(), Math.min(end - start, getMaxDuration()));
        }
        return toolBox.new Pair<TimeStamp, TimeStamp>(new TimeStamp(start), new TimeStamp(end));
    }

    private Pair<TimeStamp, TimeStamp> randomInterval(String video) {
        return randomInterval(video, -1.0);
    }

    public Script[] randomScript() {
        String oldPick, pick = null;
        Pair<TimeStamp, TimeStamp> interval, prev = null;

        if (getLazySwitch() && (pick = getLazySwitchStartingSource()) == null)
            pick = randomSource();

        Script[] rnd = new Script[getMaxClips()];
        Script step;

        if (getLazySeek() && getLazySeekFromStart())
            prev = toolBox.new Pair<TimeStamp, TimeStamp>(null, new TimeStamp(0.0));

        for (int i = 0, count = 0, count2 = 0; i < getMaxClips(); ++i) {
            step = (rnd[i] = new Script());
            step.applyEffect = toolBox.probability(getEffectChance());

            if (toolBox.probability(getTransitionClipChance()))
                step.file = effectsFactory.pickSource().getFirst();
            else if (getLazySwitch()) {
                if (toolBox.probability(getLazySwitchChance()) || count++ == getLazySwitchMaxClips()) {
                    oldPick = pick;
                    /* Intentional reference compare */
                    while ((pick = randomSource()) == oldPick);
                    count = 0;
                    prev = null;
                }
                if (toolBox.probability(getLazySwitchInterrupt())) {
                    /* Intentional reference compare */
                    while ((step.file = randomSource()) == pick);
                    step.interval = randomInterval(step.file);
                } else {
                    step.file = pick;
                    if (getLazySeek()) {
                        if (prev == null || toolBox.probability(getLazySeekChance()) || count2++ == getLazySeekMaxClips()) {
                            step.interval = (prev = randomInterval(step.file));
                            count2 = 0;
                        } else {
                            if (toolBox.probability(getLazySeekSameChance()))
                                step.interval = prev;
                            else if (toolBox.probability(getLazySeekInterrupt())) {
                                if (getLazySeekNearby()) {
                                    double clipLen = toolBox.getLength(step.file), seekLen = 0.0, delta = 0.0;
                                    if (clipLen > getLazySeekNearbyMin()) {
                                        seekLen = toolBox.randomDouble(getLazySeekNearbyMin(), getLazySeekNearbyMax()) / 2.0;
                                        delta = toolBox.randomDouble(-seekLen, seekLen);
                                    }
                                    step.interval = randomInterval(step.file, Math.min(Math.max(prev.getSecond().getLengthSec() + delta, 0.0), clipLen));
                                } else
                                    step.interval = randomInterval(step.file);
                            } else {
                                step.interval = randomInterval(step.file, prev.getSecond().getLengthSec());
                                prev = step.interval;
                            }
                        }
                    } else
                        step.interval = randomInterval(step.file);
                }
            } else {
                step.file = randomSource();
                step.interval = randomInterval(step.file);
            }
        }
        return rnd;
    }

    public void go() {
        toolBox.resetLengthCache();
        _go(randomScript());
    }

    public void go(Script[] scr) {
        toolBox.resetLengthCache();
        _go(scr);
    }

    private void _go(Script[] scr) {
        if (scr.length == 0) {
            report.done("No sources added ...");
            return;
        }
        try {
            effectsFactory.configureEffects(effects);
        } catch (Exception ex) {
            report.done("Failed to configure effects: " + ex);
            return;
        }
        File out = new File(getOutputFile());
        if (out.exists())
            out.delete();

        doneProgress = 0.0;
        cleanUp();
        try {
            IntStream.range(0, getMaxClips()).parallel().forEach(i -> {
                String clipToWorkWith = toolBox.getTemp() + "video" + i + "." + toolBox.getVideoExtension();
                Script pick = scr[i];
                if (pick.whole()) {
                    System.err.println("Transition clip: " + pick.file);
                    toolBox.copyVideo(pick.file, clipToWorkWith);
                } else {
                    System.err.println(
                        "Clip (" + toolBox.getLength(pick.file) + ") "
                        + i + " " + (pick.interval.getFirst() != null ? pick.interval.getFirst().getTimeStamp() : "")
                        + " - " + pick.interval.getSecond().getTimeStamp()
                    );
                    toolBox.snipVideo(pick.file, pick.interval.getFirst(), pick.interval.getSecond(), clipToWorkWith);
                }
                if (pick.applyEffect) {
                    effectsFactory.applyRandomEffect(clipToWorkWith);
                    /* TO DO: Only reconvert after certain effect applied */
                    if (getReconvertEffected()) {
                        File temp = toolBox.getTempVideoFile();
                        toolBox.copyVideo(clipToWorkWith, temp.getPath());
                        temp.renameTo(new File(clipToWorkWith));
                    }
                }
                doneProgress += 1.0 / scr.length;
                report.progress(doneProgress);
            });
            toolBox.concatenateVideo(scr.length, getOutputFile(), true);
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

        for (int i=0; i < getMaxClips(); i++) {
            File del = new File(toolBox.getTemp() + "video" + i + "." + toolBox.getVideoExtension());
            if (del.exists()) {
                System.err.println(i + " Exists");
                del.delete();
            }
        }
    }
}
