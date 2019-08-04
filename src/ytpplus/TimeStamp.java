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

import java.util.concurrent.TimeUnit;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class TimeStamp {
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

    @Override
    public String toString() {
        return getTimeStamp();
    }

    /* 8 digits will be enough
     * Output depends on system locale (on some locales '.' replaced with ',')
     * Neutralize locale with Locale.ROOT
     */
    private static DecimalFormat df = new DecimalFormat("#.########", new DecimalFormatSymbols(Locale.ROOT));
    private int HOURS;
    private int MINUTES;
    private double SECONDS;
}
