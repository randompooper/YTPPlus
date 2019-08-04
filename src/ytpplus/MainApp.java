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
import java.util.Map;

public class MainApp {
    public static void main(String[] args) {
        YTPGenerator ytp = new YTPGenerator("output/job_" + System.currentTimeMillis() + ".mp4");

        ytp.setMinDuration(0.34);
        ytp.setMaxDuration(0.92);
        ytp.setMaxClips(60);
        ytp.setEffectChance(50);
        ytp.setTransitionClipChance(2);
        ytp.setLazySwitch(true);
        ytp.setLazySwitchInterrupt(10);
        ytp.setLazySwitchChance(0);
        ytp.setLazySwitchMaxClips(-1);

        ytp.setLazySeek(true);
        //ytp.setLazySeekFromStart(true);
        //ytp.setLazySeekNearby(false);
        ytp.setLazySeekChance(1);
        ytp.setLazySeekNearby(true);
        ytp.setLazySeekInterrupt(35);
        ytp.setLazySeekSameChance(10);

        ytp.setEffect("RandomSoundMute", 10);
        ytp.setEffect("RandomSound", 10);
        ytp.setEffect("Reverse", 10);
        ytp.setEffect("SlowDown", 10);
        ytp.setEffect("SpeedUp", 10);
        ytp.setEffect("Squidward", 2);
        ytp.setEffect("Vibrato", 10);
        ytp.setEffect("Chorus", 10);
        ytp.setEffect("Dance", 6);
        ytp.setEffect("HighPitch", 10);
        ytp.setEffect("LowPitch", 10);
        ytp.setEffect("Mirror", 10);

        ytp.addSource("toys.mp4");

        ytp.setProgressCallback(ytp.new ProgressCallback() {
            @Override
            public void progress(double v) {
                System.out.println("Progress: " + v + "%");
            }
            @Override
            public void done(String errors) {
                if (errors != null)
                    System.out.println(errors);
            }
        });
        ytp.go();
    }
}
