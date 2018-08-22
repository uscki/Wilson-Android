package nl.uscki.appcki.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.util.Locale;

import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.fragments.LoginFragment;
import nl.uscki.appcki.android.fragments.agenda.AgendaDetailTabsFragment;
import nl.uscki.appcki.android.fragments.home.HomeAgendaTab;
import nl.uscki.appcki.android.fragments.home.HomeNewsTab;
import nl.uscki.appcki.android.fragments.home.HomeRoephoekTab;
import nl.uscki.appcki.android.fragments.meeting.MeetingDetailTabsFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingOverviewFragment;
import nl.uscki.appcki.android.fragments.meeting.MeetingPlannerFragment;
import nl.uscki.appcki.android.fragments.poll.PollOverviewFragment;
import nl.uscki.appcki.android.fragments.poll.PollResultFragment;
import nl.uscki.appcki.android.fragments.poll.PollVoteFragment;
import nl.uscki.appcki.android.fragments.quotes.QuoteFragment;
import nl.uscki.appcki.android.fragments.search.SmoboSearch;

/**
 * Created by peter on 11/23/16.
 */

public class Utils {
    public static String timestampConversion(Long time) {
        return timestampConversion(time, true);
    }

    public static String timestampConversion(Long time, Boolean useBrackets) {
        DateTime now = DateTime.now();
        DateTime other = new DateTime(time);
        int minutes = Minutes.minutesBetween(other, now).getMinutes();
        String timeIndication;
        if (minutes == 1) {
            timeIndication = "1 minuut geleden";
        } else if (minutes > 60) {
            int hours = Hours.hoursBetween(other, now).getHours();
            if (hours == 1) {
                timeIndication = "1 uur geleden";
            } else if(hours > 24) {
                int days = Days.daysBetween(other, now).getDays();
                if (days == 1) {
                    timeIndication = "1 dag geleden";
                } else if(days > 7) {
                    int weeks = Weeks.weeksBetween(other, now).getWeeks();
                    if (weeks == 1) {
                        timeIndication = "± 1 week geleden";
                    } else if(weeks > 4) {
                        int months = Months.monthsBetween(other, now).getMonths();
                        if (months == 1) {
                            timeIndication = "± 1 maand geleden";
                        } else if(months > 12) {
                            int years = Years.yearsBetween(other, now).getYears();
                            if (years == 1) {
                                timeIndication = "± 1 jaar geleden";
                            } else {
                                timeIndication = String.format(
                                        Locale.getDefault(),
                                        "± %d jaar geleden",
                                        years
                                );
                            }
                        } else {
                            timeIndication = String.format(
                                    Locale.getDefault(),
                                    "± %d maanden geleden",
                                    months
                            );
                        }
                    } else {
                        timeIndication = String.format(
                                Locale.getDefault(),
                                "± %d weken geleden",
                                weeks
                        );
                    }
                } else {
                    timeIndication = String.format(
                            Locale.getDefault(),
                            "± %d dagen geleden",
                            days
                    );
                }
            } else {
                timeIndication = String.format(
                        Locale.getDefault(),
                        "± %d uur geleden",
                        hours
                );
            }
        } else {
            timeIndication = String.format(
                    Locale.getDefault(),
                    "± %d minuten geleden",
                    minutes
            );
        }

        return String.format(
                Locale.getDefault(),
                useBrackets ? "(%s)" : "%s",
                timeIndication
        );
    }

    /**
     * Find the class of the fragment corresponding to a Screen
     * @param screen    Screen to find fragment class for
     * @return          Fragment class for screen
     */
    public static Class<? extends Fragment> getClassForScreen(MainActivity.Screen screen) {
        switch (screen) {
            case LOGIN:
                return LoginFragment.class;
            case NEWS:
                return HomeNewsTab.class;
            case AGENDA:
                return HomeAgendaTab.class;
            case POLL_OVERVIEW:
                return PollOverviewFragment.class;
            case ROEPHOEK:
                return HomeRoephoekTab.class;
            case AGENDA_DETAIL:
                return AgendaDetailTabsFragment.class;
            case MEETING_OVERVIEW:
                return MeetingOverviewFragment.class;
            case MEETING_PLANNER:
                return MeetingPlannerFragment.class;
            case MEETING_DETAIL:
                return MeetingDetailTabsFragment.class;
            case QUOTE_OVERVIEW:
                return QuoteFragment.class;
            case POLL_VOTE:
                return PollVoteFragment.class;
            case POLL_RESULT:
                return PollResultFragment.class;
            case SMOBO_SEARCH:
                return SmoboSearch.class;
            default:
                return null;
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
