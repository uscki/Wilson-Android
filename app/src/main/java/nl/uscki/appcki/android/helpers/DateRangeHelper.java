package nl.uscki.appcki.android.helpers;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

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

    private boolean isSucces = false;

    public DateRangeHelper(Person compareTo) {
        this.compareTo = compareTo;
        try {
            this.me = UserHelper.getInstance().getFullPersonInfo();
            getCountdownDate();
            isSucces = true;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "No full user info loaded");
        }
    }

    public boolean isSucces() {
        return isSucces;
    }

    /**
     * Get the ethical legality of a relation between these two love birds, one of three options
     *
     * @return Love status
     */
    public DateRange getLoveStatus() {
        return this.loveStatus;
    }

    /**
     * Get the number of milliseconds until both people are within each other's datable range. If
     * this value is negative, both are in each others datable range already and the sign of that
     * number indicates the time passed since this epic event occurred.
     *
     * @return Long, milliseconds until both are in each others datable range
     */
    @Deprecated
    public long millisUntilInRange() {
        return this.countdownDate.getMillis() - DateTime.now().getMillis();
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

        Log.e(getClass().getSimpleName(), String.format(
                "Op %s is %s oud genoeg voor %s. Op %s is %s oud genoeg voor %s",
                meOldEnoughForThem.toString(), me.getFirstname(), compareTo.getFirstname(),
                themOldEnoughForMe, compareTo.getFirstname(), me.getFirstname()
        ));

        if (meOldEnoughForThem.isAfter(themOldEnoughForMe)) {
            this.countdownDate = meOldEnoughForThem;
            loveStatus = DateRange.OTHER_TO_YOUNG;
        } else {
            this.countdownDate = themOldEnoughForMe;
            this.loveStatus = DateRange.ME_TO_YOUNG;
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
        DateTime now = DateTime.now();
        long birthP = p.getBirthdate().getMillis();
        long birthQ = q.getBirthdate().getMillis();

        Period ageP = new Period(p.getBirthdate().getMillis(), now.getMillis());
        Log.e(getClass().getSimpleName(), String.format("%s is %s years old", p.getFirstname(), ageP.getYears()));

        long qOldEnoughForP = (long) ((2 * birthQ) - birthP + FOURTEEN_SOLAR_YEARS);

        Log.e(getClass().getSimpleName(), "" +
                String.format("%s is over %d milliseconden oud genoeg voor %s", q.getFirstname(), qOldEnoughForP, p.getFirstname()));

        return new DateTime(qOldEnoughForP);
    }

    public String getFullCountdownString() {
        if(loveStatus.equals(DateRange.IN_RANGE)) {
            return String.format(Locale.getDefault(), "Jullie zijn al %s oud genoeg voor elkaar", constructStringFromMillis(getPeriodSinceCrossover()));
        } else {
            return String.format(Locale.getDefault(), "Jullie moeten nog %s wachten", constructStringFromMillis(getPeriodUntilCountdown()));
        }
    }

    /**
     * Construct a countdown string from a time period
     *
     * @param period Period denoting remaining time
     * @return String
     */
    // TODO use resources for strings
    public String constructStringFromMillis(Period period) {
        List<String> timeElements = new ArrayList<>();
        boolean fromHereOnOut = false;
        if (period.getYears() > 0) {
            timeElements.add(String.format(Locale.getDefault(), "%d jaar", period.getYears()));
            fromHereOnOut = true;
        }
        if (fromHereOnOut || period.getMonths() > 0) {
            String formatter = period.getMonths() == 1 ? "%d maand" : "%d maanden";
            timeElements.add(String.format(Locale.getDefault(), formatter, period.getMonths()));
        }
        if (fromHereOnOut || period.getDays() > 0) {
            String formatter = period.getDays() == 1 ? "%d dag" : "%d dagen";
            timeElements.add(String.format(Locale.getDefault(), formatter, period.getDays()));
        }
        if (fromHereOnOut || period.getHours() > 0) {
            timeElements.add(String.format(Locale.getDefault(), "%d uur", period.getHours()));
        }
        if (fromHereOnOut || period.getMinutes() > 0) {
            String formatter = period.getMinutes() == 1 ? "%d minuut" : "%d minuten";
            timeElements.add(String.format(Locale.getDefault(), formatter, period.getMinutes()));
        }
        if (fromHereOnOut || period.getSeconds() > 0) {
            String formatter = period.getSeconds() == 1 ? "%d seconde" : "%d seconden";
            timeElements.add(String.format(Locale.getDefault(), formatter, period.getSeconds()));
        }

        int size = timeElements.size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return timeElements.get(0);
        } else if (size == 2) {
            return String.format(Locale.getDefault(), "%s en %s", timeElements.get(0), timeElements.get(1));
        } else {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < size - 2; i++) {
                b.append(String.format(Locale.getDefault(), "%s, ", timeElements.get(i)));
            }
            b.append(String.format(Locale.getDefault(), "%s en %s", timeElements.get(size - 2), timeElements.get(size - 1)));
            return b.toString();
        }
    }

    public enum DateRange {
        OTHER_TO_YOUNG,
        ME_TO_YOUNG,
        IN_RANGE
    }
}
