package nl.uscki.appcki.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;

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
        if (minutes == 1) {
            return "(± 1 minuut geleden)";
        } else if (minutes > 60) {
            int hours = Hours.hoursBetween(other, now).getHours();
            if (hours == 1) {
                return "(± 1 uur geleden)";
            } else if(hours > 24) {
                int days = Days.daysBetween(other, now).getDays();
                if (days == 1) {
                    return "(± 1 dag geleden)";
                } else if(days > 7) {
                    int weeks = Weeks.weeksBetween(other, now).getWeeks();
                    if (weeks == 1) {
                        return "(± 1 week geleden)";
                    } else if(weeks > 4) {
                        int months = Months.monthsBetween(other, now).getMonths();
                        if (months == 1) {
                            return "(± 1 maand geleden)";
                        } else if(months > 12) {
                            int years = Years.yearsBetween(other, now).getYears();
                            if (years == 1) {
                                return "(± 1 jaar geleden)";
                            } else {
                                return "(± " + years + " jaren geleden)";
                            }
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

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String timestampConversion(String timestamp) {
        Long time = Long.valueOf(timestamp);
        return timestampConversion(time);
    }
}
