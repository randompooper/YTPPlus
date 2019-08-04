/* Copyright 2019 randompooper
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
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.lang.reflect.Method;
import ytpplus.Pair;

public class YTPGenerator {
    public YTPGenerator() {}

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
        maxClips = clips;
    }

    public int getMaxClips() {
        return maxClips;
    }

    public void setMinDuration(double min) {
        minDuration = min;
    }

    public double getMinDuration() {
        return minDuration;
    }

    public void setMaxDuration(double max) {
        maxDuration = max;
    }

    public double getMaxDuration() {
        return maxDuration;
    }

    public void setOutputFile(String out) {
        outputFile = new String(out);
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setEffectChance(double chance) {
        effectChance = Utilities.clamp(chance, 0.0, 100.0);
    }

    public double getEffectChance() {
        return effectChance;
    }

    public void setTransitionClipChance(double chance) {
        transitionClipChance = Utilities.clamp(chance, 0.0, 100.0);
    }

    public double getTransitionClipChance() {
        return transitionClipChance;
    }

    public void setLazySwitch(boolean state) {
        lazySwitch = state;
    }

    public boolean getLazySwitch() {
        return lazySwitch;
    }

    public void setLazySwitchChance(double chance) {
        lazySwitchChance = Utilities.clamp(chance, 0.0, 100.0);
    }

    public double getLazySwitchChance() {
        return lazySwitchChance;
    }

    public void setLazySwitchInterrupt(double chance) {
        lazySwitchInterrupt = Utilities.clamp(chance, 0.0, 100.0);
    }

    public double getLazySwitchInterrupt() {
        return lazySwitchInterrupt;
    }

    public void setLazySwitchMaxClips(int count) {
        lazySwitchMaxClips = count;
    }

    public int getLazySwitchMaxClips() {
        return lazySwitchMaxClips;
    }

    private String mediaFileCheck(String path) {
        if (path == null || path.equals(""))
            return "";

        File file = new File(path);
        if (!file.isFile())
            return null;

        if (!tools.isVideoAudioPresent(path)) {
            System.err.println("Source " + path + " was rejected by ffprobe");
            return null;
        }
        return new String(path);
    }

    public boolean setLazySwitchStartingSource(String path) {
        if ((path = mediaFileCheck(path)) == null)
            return false;

        lazySwitchStartingSource = path;
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

    public void setLazySeekChance(double chance) {
        lazySeekChance = Utilities.clamp(chance, 0.0, 100.0);
    }

    public double getLazySeekChance() {
        return lazySeekChance;
    }

    public void setLazySeekInterrupt(double chance) {
        lazySeekInterrupt = Utilities.clamp(chance, 0.0, 100.0);
    }

    public double getLazySeekInterrupt() {
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

    public void setLazySeekSameChance(double chance) {
        lazySeekSameChance = Utilities.clamp(chance, 0.0, 100.0);
    }

    public double getLazySeekSameChance() {
        return lazySeekSameChance;
    }

    public boolean setIntroVideo(String path) {
        if ((path = mediaFileCheck(path)) == null)
            return false;

        introVideo = path;
        return true;
    }

    public String getIntroVideo() {
        return introVideo;
    }

    public void setFFmpeg(String path) {
        tools.setFFmpeg(path);
    }

    public String getFFmpeg() {
        return tools.getFFmpeg();
    }

    public void setFFprobe(String path) {
        tools.setFFprobe(path);
    }

    public String getFFprobe() {
        return tools.getFFprobe();
    }

    public void setMagick(String path) {
        tools.setMagick(path);
    }

    public String getMagick() {
        return tools.getMagick();
    }

    public void setTemp(String path) {
        tools.setTemp(path);
    }

    public String getTemp() {
        return tools.getTemp();
    }

    public void setSources(String path) {
        tools.setSources(path);
    }

    public String getSources() {
        return tools.getSources();
    }

    public void setSounds(String path) {
        tools.setSounds(path);
    }

    public String getSounds() {
        return tools.getSounds();
    }

    public void setMusic(String path) {
        tools.setMusic(path);
    }

    public String getMusic() {
        return tools.getMusic();
    }

    public void setResources(String path) {
        tools.setResources(path);
    }

    public String getResources() {
        return tools.getResources();
    }

    public void setReconvertEffected(boolean state) {
        reconvertEffected = state;
    }

    public boolean getReconvertEffected() {
        return reconvertEffected;
    }

    public void setConcatMethod(int idx) {
        switch (idx) {
        case 0:
            concatMethod = ConcatMethod.DEMUXER;
            break;
        case 1:
            concatMethod = ConcatMethod.CONCAT_PROTO;
            break;
        case 2:
            concatMethod = ConcatMethod.CONCAT_FILTER;
            break;
        default:
            System.err.println("Invalid concat method index provided: " + idx);
            System.err.println("Index must be in [0; 2]");
        }
    }

    public int getConcatMethod() {
        switch (concatMethod) {
        case DEMUXER: return 0;
        case CONCAT_PROTO: return 1;
        case CONCAT_FILTER: return 2;
        }
        return 2;
    }

    public void setProgressCallback(ProgressCallback clbk) {
        report = clbk;
    }

    public boolean addSource(String sourceName) {
        File file = new File(sourceName);
        if (!file.isFile())
            return false;

        if (!tools.isVideoAudioPresent(sourceName)) {
            System.err.println("Source " + sourceName + " was rejected by ffprobe");
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

    public boolean setEffect(String name, double likelyness) {
        try {
            /* This method will throw exception if effect doesn't exist */
            EffectsFactory.class.getMethod("effect_" + name, String.class);
            if (likelyness > 0.0)
                effects.put(name, likelyness);
            else
                effects.remove(name);

            return true;
        } catch (Exception ex) {
            System.err.println("Failed to set effect: " + ex);
        }
        return false;
    }

    public double getEffect(String name) {
        Double v = effects.get(name);
        if (v == null) {
            System.err.println("Effect " + name + " doesn't exist!");
            v = 0.0;
        }
        return v;
    }

    private String randomSource() {
        return sourceList.get(tools.randomInt(sourceList.size() - 1));
    }

    private Pair<TimeStamp, TimeStamp> randomInterval(String video, double start) {
        double end = tools.getLength(video);
        if (end > getMinDuration()) {
            if (start < 0.0 || start >= end)
                start = tools.randomDouble(0.0, end - getMinDuration());

            if (end - start > getMinDuration()) {
                /* Special case: clips with fixed length */
                if (getMinDuration() == getMaxDuration())
                    end = start + getMinDuration();
                else
                    end = start + tools.randomDouble(getMinDuration(), Math.min(end - start, getMaxDuration()));
            }
        }
        return new Pair<>(new TimeStamp(start), new TimeStamp(end));
    }

    private Pair<TimeStamp, TimeStamp> randomInterval(String video) {
        return randomInterval(video, -1.0);
    }

    public Script[] randomScript() {
        String oldPick, pick = null;
        Pair<TimeStamp, TimeStamp> interval, prev = null;

        if (getLazySwitch() && (pick = getLazySwitchStartingSource()).equals(""))
            pick = randomSource();

        Script[] rnd = new Script[getMaxClips()];
        Script step;

        if (getLazySeek() && getLazySeekFromStart())
            prev = new Pair<>(null, new TimeStamp(0.0));

        int i = 0, count = 0, count2 = 0;
        if (!getIntroVideo().equals("")) {
            step = (rnd[i++] = new Script());
            step.file = getIntroVideo();
        }
        for (; i < getMaxClips(); ++i) {
            step = (rnd[i] = new Script());
            step.applyEffect = tools.probability(getEffectChance());

            if (tools.probability(getTransitionClipChance()))
                step.file = tools.pickSource().getFirst();
            else if (getLazySwitch()) {
                if (sourceList.size() > 1 && (tools.probability(getLazySwitchChance()) || count++ == getLazySwitchMaxClips())) {
                    oldPick = pick;
                    /* Intentional reference compare */
                    while ((pick = randomSource()) == oldPick);
                    count = 0;
                    prev = null;
                }
                if (sourceList.size() > 1 && tools.probability(getLazySwitchInterrupt())) {
                    /* Intentional reference compare */
                    while ((step.file = randomSource()) == pick);
                    step.interval = randomInterval(step.file);
                } else {
                    step.file = pick;
                    if (getLazySeek()) {
                        if (prev == null || tools.probability(getLazySeekChance()) || count2++ == getLazySeekMaxClips()) {
                            step.interval = (prev = randomInterval(step.file));
                            count2 = 0;
                        } else {
                            if (tools.probability(getLazySeekSameChance()))
                                step.interval = prev;
                            else if (tools.probability(getLazySeekInterrupt())) {
                                if (getLazySeekNearby()) {
                                    double clipLen = tools.getLength(step.file), seekLen = 0.0, delta = 0.0;
                                    if (clipLen > getLazySeekNearbyMin()) {
                                        seekLen = tools.randomDouble(getLazySeekNearbyMin(), getLazySeekNearbyMax()) / 2.0;
                                        delta = tools.randomDouble(-seekLen, seekLen);
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
        tools.resetLengthCache();
        _go(randomScript());
    }

    public void go(Script[] scr) {
        tools.resetLengthCache();
        _go(scr);
    }

    private void _go(Script[] scr) {
        if (scr.length == 0) {
            report.done("No sources added ...");
            return;
        }
        try {
            tools.configureEffects(effects);
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
            /* TO DO: Find a way to stop parallelization when requested to stop */
            IntStream.range(0, getMaxClips()).parallel().forEach(i -> {
                String clipToWorkWith = tools.getTemp() + "/video" + i + ".mp4";
                Script pick = scr[i];
                if (pick.whole()) {
                    System.err.println("Transition clip: " + pick.file);
                    tools.copyVideo(pick.file, clipToWorkWith);
                } else {
                    System.err.println(
                        "Clip (" + tools.getLength(pick.file) + ") "
                        + i + " " + (pick.interval.getFirst() != null ? pick.interval.getFirst().getTimeStamp() : "")
                        + " - " + pick.interval.getSecond().getTimeStamp()
                    );
                    tools.snipVideo(pick.file, pick.interval.getFirst(), pick.interval.getSecond(), clipToWorkWith);
                }
                if (pick.applyEffect) {
                    tools.applyRandomEffect(clipToWorkWith);
                    /* TO DO: Only reconvert after certain effect applied
                     * (I am looking at you, Squidward!)
                     */
                    if (getReconvertEffected() && concatMethod != ConcatMethod.CONCAT_FILTER) {
                        File temp = tools.getTempVideoFile();
                        tools.copyVideo(clipToWorkWith, temp.getPath());
                        temp.renameTo(new File(clipToWorkWith));
                    }
                }
                report.progress(doneProgress += 1.0 / scr.length);
            });
            switch (concatMethod) {
                case DEMUXER:
                    tools.concatenateVideoDemuxer(scr.length, getOutputFile());
                    break;
                case CONCAT_PROTO:
                    tools.concatenateVideoConcatProto(scr.length, getOutputFile());
                    break;
                case CONCAT_FILTER:
                    tools.concatenateVideoConcatFilter(scr.length, getOutputFile());
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            report.done(ex.toString());
            /* Don't remove temp folder; let user investigate matter or
             * manually concat what was generated
             */
            return;
        }
        Utilities.rmDir(new File(tools.getTemp()));
        report.done(null);
    }

    public void cleanUp() {
        //Create concatenation text file
        File text = new File(tools.getTemp() + "/concat.txt");
        if (text.exists())
            text.delete();

        for (int i=0; i < getMaxClips(); i++) {
            File del = new File(tools.getTemp() + "/video" + i + ".mp4");
            if (del.exists()) {
                System.err.println(i + " Exists");
                del.delete();
            }
        }
    }

    private double maxDuration = 0.6;
    private double minDuration = 0.3;
    private int maxClips = 20;

    private double transitionClipChance = 0.0;
    private double effectChance = 50.0;

    private boolean lazySwitch = false;
    private String lazySwitchStartingSource = "";
    private double lazySwitchChance = 3.0;
    private double lazySwitchInterrupt = 20.0;
    private int lazySwitchMaxClips = 60;

    private boolean lazySeek = false;
    private boolean lazySeekFromStart = false;
    private double lazySeekChance = 5.0;
    private double lazySeekInterrupt = 40.0;
    private int lazySeekMaxClips = -1;
    private double lazySeekSameChance = 10.0;
    private boolean lazySeekNearby = false;
    private double lazySeekNearbyMin = 0.2;
    private double lazySeekNearbyMax = 5.0;

    private String introVideo = "";

    private boolean reconvertEffected = true;

    public enum ConcatMethod {
        /* It takes all mp4 clips (with same PTS and audio) and
         * concatenates them like it is one big mp4 file without
         * reencoding
         *
         * Very fast, shouldn't reduce quality but final video might not
         * play correctly in all media players (at the moment only MPV
         * plays them correctly) (Squidward and pitch effects break)
         * Introduces barely noticable A/V desync
         *
         * Recommended when tight on memory and time
         */
        DEMUXER,
        /* It losslessly transcodes all mp4 clips into MPEG2 transport
         * streams, then concatenates them using ffmpeg's concat protocol
         *
         * Like first one but makes some videos with broken effects play
         * normally in some players at the cost of higher A/V desync
         *
         * Use if DEMUXER produces result poorly playable in your player
         */
        CONCAT_PROTO,
        /* It uses ffmpeg's concat filter which assumes clips have
         * different format and/or different video and audio characteristics
         *
         * Makes video quality worse, requires a lot of memory and time
         *
         * Recommended default, final video produced shouldn't have any issues
         */
        CONCAT_FILTER
    }
    /* TO DO: Fix all PTS related issues and use DEMUXER as default
     * concat method
     *
     * Problematic effects:
     * - Pitch effects: produces audio with different PTS.
     *    Clip reconvertion doesn't help. Also pitch effects might
     *    sometimes drop video or audio channel. These clips usually
     *    have length of < 0.2 seconds
     * - Some random sounds with mute: inserts audio with 2 channels and
     *    thus with different audio PTS (worked around by reconverting)
     * - Some random music in dance effect with same issues as random
     *    sounds
     * - Squidward due to concatenation with screenshots changing video
     *    PTS + same audio issues as with random music.
     *    Clip reconvertion works it around only for MPV, other players
     *    might play final video not correctly. CONCAT_PROTO might work
     *    around this problem for these players but makes matter worse
     *    for MPV
     *    TO DO: check for outdated ffmpeg on Windows
     */
    private ConcatMethod concatMethod = ConcatMethod.CONCAT_FILTER;

    private String outputFile = "temp/tempoutput.mp4";

    private Map<String, Double> effects = new HashMap<>();
    {
        effects.put("RandomSound", 10.0);
        effects.put("RandomSoundMute", 10.0);
        effects.put("Reverse", 10.0);
        effects.put("SlowDown", 10.0);
        effects.put("SpeedUp", 10.0);
        effects.put("Squidward", 10.0);
        effects.put("Vibrato", 10.0);
        effects.put("Chorus", 10.0);
        effects.put("Dance", 10.0);
        effects.put("HighPitch", 10.0);
        effects.put("LowPitch", 10.0);
        effects.put("Mirror", 10.0);
    }

    private EffectsFactory tools = new EffectsFactory();
    private ArrayList<String> sourceList = new ArrayList<String>();

    public class ProgressCallback {
        public void progress(double v) {}
        public void done(String errors) {}
    }

    public class Script {
        public final boolean whole() {
            return interval == null;
        }

        public String file;
        public Pair<TimeStamp, TimeStamp> interval;
        public boolean applyEffect;
    }

    private ProgressCallback report = new ProgressCallback();
    private volatile double doneProgress;
}
