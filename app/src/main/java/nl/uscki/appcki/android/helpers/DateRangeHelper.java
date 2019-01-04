package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.Period;
import java.util.ArrayList;
import java.util.List;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.organisation.Person;

public class DateRangeHelper {

    /**
     * Expressing a year as a UNIX time stamp is difficult, as the length of a year changes from
     * year to year
     */
    private static final double SOLAR_YEAR = 365.2425 * 24 * 60 * 60 * 1000;
    private static final double FOURTEEN_SOLAR_YEARS = SOLAR_YEAR * 14;

    private Person compareTo;
    private Person me;

    private DateTime countdownDate;
    private DateRange loveStatus;

    private boolean isSuccess = false;

    private Context context;

    public DateRangeHelper(Context context, Person compareTo) {
        this.compareTo = compareTo;
        this.context = context;
        try {
            this.me = UserHelper.getInstance().getFullPersonInfo();
            getCountdownDate();
            isSuccess = true;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "No full user info loaded");
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Get the ethical legality of a relation between these two love birds, one of three options
     *
     * @return Love status
     */
    public DateRange getLoveStatus() {
        return this.loveStatus;
    }

    public Period getPeriodUntilCountdown() {
        return new Period(DateTime.now(), countdownDate);
    }

    public Period getPeriodSinceCrossover() {
        return new Period(countdownDate, DateTime.now());
    }

    /**
     * Calculate the moment where both me and them are within eachothers dateable range according
     * to the half-your-age-plus-seven rule
     */
    private void getCountdownDate() {
        DateTime meOldEnoughForThem = untilQOldEnoughForP(compareTo, me);
        DateTime themOldEnoughForMe = untilQOldEnoughForP(me, compareTo);

        if (meOldEnoughForThem.isAfter(themOldEnoughForMe)) {
            this.countdownDate = meOldEnoughForThem;
            this.loveStatus = DateRange.ME_TOO_YOUNG;
        } else {
            this.countdownDate = themOldEnoughForMe;
            this.loveStatus = DateRange.OTHER_TOO_YOUNG;
        }

        if (countdownDate.isBeforeNow()) {
            this.loveStatus = DateRange.IN_RANGE;
        }
    }

    /**
     * Calculate the exact moment that person Q is old enough for person P according to
     * the half-your-age-plus-seven rule. If this moment lies in the past, Q is already old enough
     * for P (although even then the reverse is not necessarily true)
     *
     * @param p Person p
     * @param q Person q
     * @return DateTime specifying the exact moment q is old enough for p
     */
    private DateTime untilQOldEnoughForP(Person p, Person q) {
        long birthP = p.getBirthdate().getMillis();
        long birthQ = q.getBirthdate().getMillis();

        long qOldEnoughForP = (long) ((2 * birthQ) - birthP + FOURTEEN_SOLAR_YEARS);

        return new DateTime(qOldEnoughForP);
    }

    public String getFullCountdownString() {
        if(loveStatus.equals(DateRange.IN_RANGE)) {
            return this.context.getString(R.string.hyap7_time_since_in_range, constructStringFromMillis(getPeriodSinceCrossover()));
        } else {
            return this.context.getString(R.string.hyap7_time_until_in_range, constructStringFromMillis(getPeriodUntilCountdown()));
        }
    }

    /**
     * Construct a countdown string from a time period
     *
     * @param period Period denoting remaining time
     * @return String
     */
    public String constructStringFromMillis(Period period) {
        List<String> timeElements = new ArrayList<>();
        boolean fromHereOnOut = false;
        if (period.getYears() > 0) {
            timeElements.add(
                    this.context.getString(
                            period.getYears() == 1 ? R.string.timestrings_x_year : R.string.timestring_x_years,
                            period.getYears())
            );
            fromHereOnOut = true;
        }
        if (fromHereOnOut || period.getMonths() > 0) {
            timeElements.add(
                    this.context.getString(
                            period.getMonths() == 1 ? R.string.timestrings_x_month : R.string.timestring_x_months,
                            period.getMonths())
            );
        }
        if (fromHereOnOut || period.getDays() > 0) {
            timeElements.add(
                    this.context.getString(
                            period.getDays() == 1 ? R.string.timestrings_x_day : R.string.timestring_x_days,
                            period.getDays())
            );
        }
        if (fromHereOnOut || period.getHours() > 0) {
            timeElements.add(
                    this.context.getString(
                            period.getHours() == 1 ? R.string.timestrings_x_hour : R.string.timestring_x_hours,
                            period.getHours())
            );
        }
        if (fromHereOnOut || period.getMinutes() > 0) {
            timeElements.add(
                    this.context.getString(
                            period.getMinutes() == 1 ? R.string.timestrings_x_minute : R.string.timestring_x_minutes,
                            period.getMinutes())
            );
        }
        if (fromHereOnOut || period.getSeconds() > 0) {
            timeElements.add(
                    this.context.getString(
                            period.getSeconds() == 1 ? R.string.timestrings_x_second : R.string.timestring_x_seconds,
                            period.getSeconds())
            );
        }

        int size = timeElements.size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return timeElements.get(0);
        } else if (size == 2) {
            return this.context.getString(R.string.list_concat_and, timeElements.get(0), timeElements.get(1));
        } else {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < size - 2; i++) {
                b.append(this.context.getString(R.string.list_concat_seperator, timeElements.get(i)));
            }
            b.append(this.context.getString(R.string.list_concat_and, timeElements.get(size - 2), timeElements.get(size - 1)));
            return b.toString();
        }
    }

    public enum DateRange {
        OTHER_TOO_YOUNG,
        ME_TOO_YOUNG,
        IN_RANGE
    }
}
