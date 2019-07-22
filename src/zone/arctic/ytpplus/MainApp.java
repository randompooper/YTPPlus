package zone.arctic.ytpplus;
import java.util.Map;

public class MainApp {
    public static void main(String[] args) {
        YTPGenerator generatorMain = new YTPGenerator("output/job_" + System.currentTimeMillis() + ".mp4");
        generatorMain.configurate();

        generatorMain.setMinDuration(0.1);
        generatorMain.setMaxDuration(1.2);
        generatorMain.setMaxClips(100);
        generatorMain.setEffectChance(66);
        generatorMain.setTransitionClipChance(5);
        generatorMain.setLazySwitch(true);
        /*generatorMain.setLazySwitchChance(0);
        generatorMain.setLazySwitchMaxClips(-1);*/

        generatorMain.setEffect("RandomSoundMute", 10);
        generatorMain.setEffect("Reverse", 10);
        generatorMain.setEffect("SlowDown", 10);
        generatorMain.setEffect("SpeedUp", 10);
        generatorMain.setEffect("Squidward", 2);
        generatorMain.setEffect("Vibrato", 10);
        generatorMain.setEffect("Chorus", 10);
        generatorMain.setEffect("Dance", 6);
        generatorMain.setEffect("HighPitch", 10);
        generatorMain.setEffect("LowPitch", 10);
        generatorMain.setEffect("Mirror", 10);
        generatorMain.setEffect("RandomSound", 10);

        generatorMain.addSource("toys.mp4");

        generatorMain.setProgressCallback(generatorMain.new ProgressCallback() {
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
        generatorMain.go();
    }
}
