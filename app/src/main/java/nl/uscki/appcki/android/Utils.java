package nl.uscki.appcki.android;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

/**
 * Created by peter on 11/23/16.
 */

public class Utils {
    public static String timestampConversion(Long time) {
        DateTime now = DateTime.now();
        DateTime other = new DateTime(time);
        int minutes = Minutes.minutesBetween(other, now).getMinutes();
        if (minutes > 60) {
            int hours = Hours.hoursBetween(other, now).getHours();
            if(hours > 24) {
                int days = Days.daysBetween(other, now).getDays();
                if(days > 7) {
                    int weeks = Weeks.weeksBetween(other, now).getWeeks();
                    if(weeks > 4) {
                        int months = Months.monthsBetween(other, now).getMonths();
                        if(months > 12) {
                            int years = Years.yearsBetween(other, now).getYears();
                            return "(± " + years + " jaren geleden)";
                        } else {
                            return "(± " + months + " maanden geleden)";
                        }
                    } else {
                        return "(± " + weeks + " weken geleden)";
                    }
                } else {
                    return "(± " + days + " dagen geleden)";
                }
            } else {
                return "(± " + hours + " uur geleden)";
            }
        } else {
            return "(± " + minutes + " minuten geleden)";
        }
    }

    public static String timestampConversion(String timestamp) {
        Long time = Long.valueOf(timestamp);
        return timestampConversion(time);
    }
}
