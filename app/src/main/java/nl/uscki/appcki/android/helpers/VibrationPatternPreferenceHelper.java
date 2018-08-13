package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

/**
 * A helper class for converting resources to vibration patterns based on the user's preferences
 */
public class VibrationPatternPreferenceHelper {

    private Context context;
    private SharedPreferences preferenceManager;
    private Resources res;

    public VibrationPatternPreferenceHelper() {
        context = App.getContext();
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        res = context.getResources();
    }

    /**
     * Convert a resource array describing a vibration pattern to a long array that can be used
     * by the vibration manager
     *
     * @param resourceIdentifier Resource identifier of the array containing the vibration pattern
     * @return long array for the vibration pattern
     */
    public long[] getVibrationPattern(int resourceIdentifier) {
        if(resourceIdentifier < 0)
            return null; // Nothing to do here

        Resources res = App.getContext().getResources();

        int[] patternArray = res.getIntArray(resourceIdentifier);
        int l = patternArray.length;
        long[] pattern = new long[l];

        for(int i = 0; i < l; i++) {
            pattern[i] = patternArray[i];
        }

        return pattern;
    }

    /**
     * Find the index of the selected vibration pattern in a preference list
     *
     * @param preferenceIdentifier Preference identifier of the specific vibration pattern preference
     *
     * @return Index of the selected preference in the given preference list
     */
    public int getIndexOfVibrationPatternPreference(String preferenceIdentifier) {
        String vibrationPatternName = preferenceManager.getString(preferenceIdentifier, "");
        if(vibrationPatternName.equals(""))
            return -1;

        String[] vibrationPatternNames = res.getStringArray(R.array.vibration_pattern_names);

        int index = 0;

        // O(n), but converting to Array does more harm
        for(String name : vibrationPatternNames) {
            if(name.equals(vibrationPatternName))
                return index;
            index++;
        }

        return -1;
    }

    /**
     * Get the resource identifier of the vibration pattern at the given index in any vibration
     * pattern preference
     *
     * @param index Index of the selected vibration pattern
     * @return Resource identifier of the integer array describing that vibration patter
     */
    public int getVibrationPatternResourceIdAtIndex(int index) {
        // The vibration pattern values array corresponds to the vibration pattern names array
        TypedArray vibrationPatternResourceArrays =
                res.obtainTypedArray(R.array.vibration_pattern_values);

        // The resource identifier is at the found index
        int resourceIdentifier = vibrationPatternResourceArrays.getResourceId(index, -1);
        vibrationPatternResourceArrays.recycle();

        return resourceIdentifier;
    }
}
