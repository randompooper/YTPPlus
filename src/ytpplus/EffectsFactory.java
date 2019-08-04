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
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.lang.reflect.Method;
import ytpplus.Utilities;
import ytpplus.Pair;

public class EffectsFactory extends Utilities {
    public Pair<String, Double> pickSound() {
        return pickRandomMediaFile(getSounds());
    }

    public Pair<String, Double> pickSource() {
        return pickRandomMediaFile(getSources());
    }

    public Pair<String, Double> pickMusic() {
        return pickRandomMediaFile(getMusic());
    }

    public void effect_RandomSound(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-i", pickSound().getFirst(),
                "-c:v", "copy",
                "-filter_complex", "[1:a]volume=1,apad[A];[0:a][A]amerge[out]",
                "-ac", "2",
                "-ar", "44100",
                "-map", "0:v",
                "-map", "[out]",
                "-y", video);
            temp.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }

    public void effect_RandomSoundMute(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            Pair<String, Double> randomSound = pickSound();
            System.err.println("Doing a mute now. " + randomSound.getFirst() + " length: " + randomSound.getSecond() + ".");
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
               "-i", randomSound.getFirst(),
               "-to", randomSound.getSecond().toString(),
               "-filter_complex", "[1:a]volume=1,apad[A]",
               "-map", "0:v",
               "-map", "[A]",
               "-shortest", "-y", video);

            temp.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }
    public void effect_Reverse(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-af", "areverse",
                "-vf", "reverse",
                "-y", video);

            temp.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }


    public void effect_SpeedUp(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-vf", "setpts=0.5*PTS",
                "-af", "atempo=2.0",
                "-y", video);

            temp.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }

    public void effect_SlowDown(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-vf", "setpts=2*PTS",
                "-af", "atempo=0.5",
                "-y", video);
            temp.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);
        }
    }

    public void effect_Chorus(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-c:v", "copy",
                "-af", "chorus=0.5:0.9:50|60|40:0.4|0.32|0.3:0.25|0.4|0.3:2|2.3|1.3",
                "-y", video);
            temp.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }

    public void effect_Vibrato(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-c:v", "copy",
                "-af", "vibrato=f=7.0:d=0.5",
                "-y", video);
            temp.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }

    public void effect_LowPitch(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            //File temp2 = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-vf", "setpts=2*PTS",
                "-af", "asetrate=44100*0.5,aresample=44100",
                "-y", video);

            temp.delete();
            /*execFFmpeg("-i", temp2.getPath(),
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-ac", "1", "-ar", "44100",
                "-y", video);

            temp2.delete();*/
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }

    public void effect_HighPitch(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            //File temp2 = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-vf", "setpts=0.5*PTS",
                "-af", "asetrate=44100*2,aresample=44100",
                "-y", video);

            temp.delete();
            /*execFFmpeg("-i", temp2.getPath(),
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-ac", "1", "-ar", "44100",
                "-y", video);

            temp2.delete();*/
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" + ex);
        }
    }

    public void effect_Dance(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);

            File temp = getTempVideoFile(); //og file
            File temp2 = getTempVideoFile(); //1st cut
            File temp3 = getTempVideoFile(); //backwards (silent
            File temp4 = getTempVideoFile(); //forwards (silent
            File temp5 = getTempVideoFile(); //backwards & forwards concatenated
            File temp6 = getTempVideoFile(); //backwards & forwards concatenated

            // final result is backwards & forwards concatenated with music

            if (in.exists())
                in.renameTo(temp);

            Pair<String, Double> randomSound = pickMusic();
            double soundLength = randomSound.getSecond() / 8.0;
            if (soundLength > 0.3)
                soundLength = randomDouble(0.3, Math.min(soundLength, 1.9));

            TimeStamp ts = new TimeStamp(soundLength);
            execFFmpeg("-i", temp.getPath(),
                "-map", "0", // "-c:v", "copy",
                "-to", ts.getTimeStamp(),
                "-an", "-y", temp2.getPath());
            execFFmpeg("-i", temp2.getPath(),
                "-map", "0", // "-c:v", "copy",
                "-vf", "reverse",
                "-y", temp3.getPath());
            execFFmpeg("-i", temp3.getPath(),
                "-vf", "reverse",
                "-y", temp4.getPath());
            execFFmpeg("-i", temp3.getPath(),
                "-i", temp4.getPath(),
                "-filter_complex", "[0:v:0][1:v:0][0:v:0][1:v:0][0:v:0][1:v:0][0:v:0][1:v:0]concat=n=8:v=1[outv]",
                "-map", "[outv]",
                "-c:v", "libx264",
                "-shortest", "-y", temp5.getPath());
            execFFmpeg("-i", temp5.getPath(),
                "-map", "0",
                "-vf", "setpts=0.5*PTS",
                "-af", "atempo=2.0",
                "-shortest", "-y", temp6.getPath());
            execFFmpeg("-i", temp6.getPath(),
                "-i", randomSound.getFirst(),
                "-map", "0:v:0",
                "-map", "1:a:0",
                "-shortest", "-y", video);

            temp.delete();
            temp2.delete();
            temp3.delete();
            temp4.delete();
            temp5.delete();
            temp6.delete();
        } catch (Exception ex) {
            System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);
        }
    }

    public void effect_Squidward(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile(); //og file
            int pictureNum = randomInt();
            String picturePrefix = getTemp() + "/" + pictureNum + "-";

            // final result is backwards & forwards concatenated with music

            if (in.exists())
                in.renameTo(temp);

            execFFmpeg("-i", temp.getPath(),
                "-vf", "select=gte(n\\,1)",
                "-vframes", "1",
                "-y", picturePrefix + "squidward0.png");

            int squidPrev = -1, squidSwitch;
            for (int i = 1; i < 6; i++) {
                ArrayList<String> cmdLine = new ArrayList<String>();
                cmdLine.add("convert");
                cmdLine.add(picturePrefix + "squidward0.png");

                while ((squidSwitch = randomInt(6)) == squidPrev);
                switch ((squidPrev = squidSwitch)) {
                    case 0:
                        cmdLine.add("-flop");
                        break;
                    case 1:
                        cmdLine.add("-flip");
                        break;
                    case 2:
                    case 3:
                        cmdLine.add("-implode");
                        cmdLine.add((squidPrev == 3 ? "-" : "") + randomInt(1, 3));
                        break;
                    case 4:
                    case 5:
                        cmdLine.add("-swirl");
                        cmdLine.add((squidPrev == 5 ? "-" : "") + randomInt(1, 180));
                        break;
                    case 6:
                        cmdLine.add("-channel");
                        cmdLine.add("RGB");
                        cmdLine.add("-negate");
                        break;
                    //case 7:
                    //    effect = " -virtual-pixel Black +distort Cylinder2Plane " + randomInt(1,90);
                    //    break;
                }
                cmdLine.add(picturePrefix + "squidward" + i + ".png");
                execMagick(cmdLine.toArray(new String[cmdLine.size()]));
            }
            execMagick("convert", "-size", "640x480", "canvas:black",
                picturePrefix + "black.png");

            File squidwardScript = new File(picturePrefix + "concatsquidward.txt");
            if (squidwardScript.exists())
                squidwardScript.delete();
            PrintWriter writer = new PrintWriter(squidwardScript, "UTF-8");
            writer.write("file " + pictureNum + "-squidward0.png\n" +
                        "duration 0.467\n" +
                        "file " + pictureNum + "-squidward1.png\n" +
                        "duration 0.434\n" +
                        "file " + pictureNum + "-squidward2.png\n" +
                        "duration 0.4\n" +
                        "file " + pictureNum + "-black.png'\n" +
                        "duration 0.834\n" +
                        "file " + pictureNum + "-squidward3.png\n" +
                        "duration 0.467\n" +
                        "file " + pictureNum + "-squidward4.png\n" +
                        "duration 0.4\n" +
                        "file " + pictureNum + "-squidward5.png\n" +
                        "duration 0.467");
            writer.close();
            /* Using "\\" slashes breaks concat (and thus squidward) on
             * Windows breaks
             */
            execFFmpeg("-f", "concat",
                "-i", picturePrefix + "concatsquidward.txt",
                "-i", getResources() + "/squidward/music.wav",
                "-map", "0:v:0", "-map", "1:a:0", "-pix_fmt", "yuv420p",
                "-y", video);

            temp.delete();
            for (int i = 0; i < 6; i++)
                new File(picturePrefix + "squidward" + i + ".png").delete();

            new File(picturePrefix + "black.png").delete();
            squidwardScript.delete();
        } catch (Exception ex) {System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }

    public void effect_Mirror(String video) {
        System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + " initiated");
        try {
            File in = new File(video);
            File temp = getTempVideoFile();
            if (in.exists())
                in.renameTo(temp);

            boolean flip = randomBool();
            execFFmpeg("-i", temp.getPath(),
                "-c:a", "copy",
                "-vf", "crop=iw/2:ih:" + (flip ? "0:0" : "iw/2:ih") + ",split[part0][tmp];"
                + "[tmp]hflip[part1];" + (flip ? "[part0][part1]" : "[part1][part0]") + "hstack",
                "-y", video);
            temp.delete();
        } catch (Exception ex) {System.err.println(new Object() {}.getClass().getEnclosingMethod().getName() + "\n" +ex);}
    }

    public void configureEffects(Map<String, Double> options) throws NoSuchMethodException {
        double l = 0.0, totalLikelyness = 0.0;
        Set<Map.Entry<String, Double>> optionSet = options.entrySet();

        map = new TreeMap<>();
        for (Map.Entry<String, Double> opt: optionSet)
            if (opt.getValue() > 0.0)
                totalLikelyness += opt.getValue();

        for (Map.Entry<String, Double> opt : optionSet)
            if (opt.getValue() > 0.0) {
                l += (double)opt.getValue() / totalLikelyness;
                map.put(l, EffectsFactory.class.getMethod("effect_" + opt.getKey(), String.class));
            }
        for (Map.Entry<Double, Method> o : map.entrySet())
            System.err.println("" + o.getKey() + " = " + o.getValue().getName());
    }

    public void applyRandomEffect(String video) {
        if (map == null || map.size() == 0)
            return;

        double randFloat = randomDouble();

        Map.Entry<Double, Method> effect = map.higherEntry(randFloat);
        System.err.println("Random float " + randFloat + "; Found " + effect);

        Method method = effect.getValue();
        System.err.println("Starting effect " + method.getName());
        try {
            method.invoke(this, video);
        } catch (Exception ex) {
            System.err.println("Failed to apply random effect: " + ex);
        }
    }

    private TreeMap<Double, Method> map;
}
