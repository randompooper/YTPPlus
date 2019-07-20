/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.util.concurrent.TimeUnit;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author bebn
 */
public class TimeStamp {
    /* 8 digits will be enough
     * Output depends on system locale (on some locales '.' replaced with ',')
     * Neutralize locale with Locale.ROOT
     */
    private static DecimalFormat df = new DecimalFormat("#.########", new DecimalFormatSymbols(Locale.ROOT));
    private int HOURS;
    private int MINUTES;
    private double SECONDS;

    public TimeStamp(String time) {
        String[] parts = time.split(":");
        this.HOURS = Integer.parseInt(parts[0]);
        this.MINUTES = Integer.parseInt(parts[1]);
        this.SECONDS = Double.parseDouble(parts[2]);
    }

    public TimeStamp(double l) {

        double ms = l*1000;
        double millis = ms % 1000;
        double x = ms / 1000;
        double seconds = x % 60;
        x /= 60;
        double minutes = x % 60;
        x /= 60;
        double hours = x % 24;

        this.HOURS = (int)hours;
        this.MINUTES = (int)minutes;
        this.SECONDS = (int)seconds + (millis/1000);

    }

    public double getLengthSec() {
        return SECONDS + (MINUTES*60) + (HOURS*60*60);
    }

    public int getHours() {
        return HOURS;
    }
    public int getMinutes() {
        return MINUTES;
    }
    public double getSeconds() {
        return SECONDS;
    }

    public String getTimeStamp() {
        return this.HOURS + ":" + this.MINUTES + ":" + df.format(this.SECONDS);
    }
}
