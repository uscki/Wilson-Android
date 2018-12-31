package nl.uscki.appcki.android.helpers;

import android.util.Log;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.organisation.Person;

public class DateRangeHelper {

    private Person compareTo;
    private Person me;

    public DateRangeHelper(Person compareTo) {
        this.compareTo = compareTo;
        try {
            this.me = UserHelper.getInstance().getFullPersonInfo();
        } catch(Exception e) {
            Log.e(getClass().getSimpleName(), "No full user info loaded");
        }
    }

    private void getCountdownDate() {
        DateTime meOldEnoughForThem = null;
        DateTime themOldEnoughForMe = null;
    }

    private DateTime untilPOldEnoughForQ(Person p, Person q) {

        return null; 
    }

    /**
     * Calculate the time until a person with birthdate p is old enough for a person
     * with birthdate q. The return value may be negative, indicating person P already
     * is old enough for person q, although this relation is not symmetric
     * @param p                 Unix time stamp, birthdate of person P
     * @param q                 Unix time stamp, birthdate of person Q
     * @param OneYearAsUnit     Unix time stamp units in a single year
     * @return                  Units used by unix time stamp until p is old enough for q
     */
    private long untilPOldEnoughForQ(long p, long q, long OneYearAsUnit) {
        return q + (14 * OneYearAsUnit) - (2*p);
    }
}
