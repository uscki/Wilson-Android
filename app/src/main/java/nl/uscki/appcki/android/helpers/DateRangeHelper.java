package nl.uscki.appcki.android.helpers;

import android.util.Log;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.organisation.Person;

public class DateRangeHelper {

    /**
     * Expressing a year as a UNIX time stamp is difficult, as the length of a year changes from
     * year to year
     */
    private static final double FOURTEEN_SOLAR_YEARS = 365.2425 * 24 * 60 * 60 * 1000 * 14;

    private Person compareTo;
    private Person me;

    private DateTime countdownDate;
    private DateRange loveStatus;

    public DateRangeHelper(Person compareTo) {
        this.compareTo = compareTo;
        try {
            this.me = UserHelper.getInstance().getFullPersonInfo();
        } catch(Exception e) {
            Log.e(getClass().getSimpleName(), "No full user info loaded");
        }

        getCountdownDate();
    }

    /**
     * Get the ethical legality of a relation between these two love birds, one of three options
     * @return  Love status
     */
    public DateRange getLoveStatus() {
        return loveStatus;
    }

    /**
     * Get the number of milliseconds until both people are within each other's datable range. If
     * this value is negative, both are in each others datable range already and the sign of that
     * number indicates the time passed since this epic event occurred.
     * @return Long, milliseconds until both are in each others datable range
     */
    public long millisUntilInRange() {
        return countdownDate.getMillis() - DateTime.now().getMillis();
    }

    /**
     * Calculate the moment where both me and them are within eachothers dateable range according
     * to the half-your-age-plus-seven rule
     */
    private void getCountdownDate() {
        DateTime meOldEnoughForThem = untilQOldEnoughForP(compareTo, me);
        DateTime themOldEnoughForMe = untilQOldEnoughForP(me, compareTo);

       if(meOldEnoughForThem.isAfter(themOldEnoughForMe)) {
           countdownDate = meOldEnoughForThem;
           loveStatus = DateRange.OTHER_TO_YOUNG
       } else {
           countdownDate = themOldEnoughForMe;
           loveStatus = DateRange.ME_TO_YOUNG;
       }

       if(countdownDate.isBeforeNow()) {
           loveStatus = DateRange.IN_RANGE;
       }
    }

    /**
     * Calculate the exact moment that person Q is old enough for person P according to
     * the half-your-age-plus-seven rule. If this moment lies in the past, Q is already old enough
     * for P (although even then the reverse is not necessarily true)
     *
     * @param p     Person p
     * @param q     Person q
     * @return      DateTime specifying the exact moment q is old enough for p
     */
    private DateTime untilQOldEnoughForP(Person p, Person q) {
        DateTime now = DateTime.now();
        long birthP = p.getBirthdate().getMillis() - now.getMillis();
        long birthQ = q.getBirthdate().getMillis() - now.getMillis();

        long qOldEnoughForP = (long) ((2 * birthQ) - birthP + FOURTEEN_SOLAR_YEARS);

        return new DateTime(qOldEnoughForP);
    }

    public enum DateRange {
        OTHER_TO_YOUNG,
        ME_TO_YOUNG,
        IN_RANGE
    }
}
