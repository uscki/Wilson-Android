package nl.uscki.appcki.android.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

public class PermissionHelper {

    public static final String USE_CALENDAR_EXPORT_KEY = "calendar_use_export";
    public static final String CALENDAR_EXPORT_EVENT_AUTO_KEY = "event_export_auto";
    public static final String CALENDAR_EXPORT_MEETING_AUTO_KEY = "meeting_export_auto";
    public static final String USE_PEOPLE_EXPORT_KEY = "people_use_export";

    // Privacy and data policies
    public static final String AGREE_GENERAL_POLICY_KEY = "nl.uscki.appcki.android.wilson.policy.key.GENERAL";
    public static final String AGREE_APP_POLICY_KEY = "nl.uscki.appcki.android.wilson.policy.key.APP_SPECIFIC";
    public static final String AGREE_NOTIFICATION_POLICY_KEY = "nl.uscki.appcki.android.wilson.policy.key.NOTIFICATIONS";

    private static PermissionHelper instance;

    private PermissionHelper(){
        instance = this;
    }

    public static PermissionHelper getInstance() {
        return instance;
    }

    public static boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(App.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean canExportCalendar(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean hasPermission = hasPermission(Manifest.permission.WRITE_CALENDAR);
        boolean hasRequestedExport = prefs.getBoolean(USE_CALENDAR_EXPORT_KEY, false);
        return hasPermission && hasRequestedExport;
    }

    public static boolean canDeleteCalendar() {
        return canExportCalendar() && hasPermission(Manifest.permission.READ_CALENDAR);
    }

    public static boolean canExportCalendarAuto() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean hasRequestedAutoExport = prefs.getBoolean(CALENDAR_EXPORT_EVENT_AUTO_KEY, false);
        return canExportCalendar() && hasRequestedAutoExport;
    }

    public static boolean canExportMeetingAuto() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean hasRequestedAutoExport = prefs.getBoolean(CALENDAR_EXPORT_MEETING_AUTO_KEY, false);
        return canExportCalendar() && hasRequestedAutoExport;
    }

    public static boolean canExportContact() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return hasPermission(Manifest.permission.READ_CONTACTS) &&
                hasPermission(Manifest.permission.WRITE_CONTACTS) &&
                prefs.getBoolean(USE_PEOPLE_EXPORT_KEY, false);
    }

    /**
     * Check if a certain policy has been agreed to according to the stored preferences of the user
     * @param policyKey     Key to the policy
     * @return              Boolean value, defaults to false
     */
    public static boolean getPreferenceBoolean(Context context, String policyKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(policyKey, false);
    }

    /**
     * Check if the user has agreed to both the general USCKI policy and the app-specific privacy
     * policy
     * @param context   A context reference
     * @return          Boolean
     */
    public static boolean hasAgreedToBasicPolicy(Context context) {
        return getAgreeToPolicyLatest(context, AGREE_GENERAL_POLICY_KEY) &&
                getAgreeToPolicyLatest(context, AGREE_APP_POLICY_KEY);
    }

    /**
     * Check if the user has explicitly agreed to the privacy policy regarding the collecting of
     * a personalized device token in favour of receiving notifications
     *
     * @param context       Active context
     * @return              Boolean indicating user agreement with notification policy
     */
    public static boolean hasAgreedToNotificationPolicy(Context context) {
        return getAgreeToPolicyLatest(context, PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY);
    }

    /**
     * Updat ethe users agreement with a specific policy key governed by a specific policy text
     * version.
     *
     * @param context       Application context
     * @param policyKey     Static policy preference key
     * @param policyVersion Version code of a policy text version
     * @param value         New value: True if agreed, false if disagreed
     */
    @SuppressLint("ApplySharedPref")
    public static void setAgreeToPolicy(Context context, String policyKey, int policyVersion, boolean value) {
        // Acquire a shared preference editor
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        // Also store on the default key, so we can verify if the user did not agree yet at all,
        // or just not to the current policy
        editor.putBoolean(policyKey, value);

        // Store the preference for the given version
        editor.putBoolean(getPolicyKeyForVersion(policyKey, policyVersion), value);

        // Commit changes. Use commit, because app may not be open long enough for an apply
        editor.commit();
    }

    /**
     * Get the state of the user agreement with a specific policy governed by a specific policy
     * text version
     * @param context       Application context
     * @param policyKey     Static policy preference key
     * @param policyVersion Version code of a policy text version
     * @return  Boolean, true if agreed, false if disagreed or not yet known
     */
    public static boolean getAgreeToPolicy(Context context, String policyKey, int policyVersion) {
        return getPreferenceBoolean(context, getPolicyKeyForVersion(policyKey, policyVersion));
    }

    /**
     * Get the state of the user agreement with a specific policy governed by the latest policy
     * text version
     * @param context       Application context
     * @param policyKey     Static policy preference key
     * @return              Boolean, true if agreed, false if disagreed or not yet known
     */
    public static boolean getAgreeToPolicyLatest(Context context, String policyKey) {
        Resources resources;
        try {
            resources = context.getResources();
        } catch(NullPointerException e) {
            Log.e(PermissionHelper.class.getSimpleName(), "Trying to get resources from context, but we have a problem");
            Log.e(PermissionHelper.class.getSimpleName(), e.getMessage());
            for(StackTraceElement ste : e.getStackTrace()) {
                Log.e(PermissionHelper.class.getSimpleName(), ste.toString());
            }
            return false;
        }
        int currentPrivacyPolicyIndex = resources.getInteger(R.integer.privacy_policy_current_version_index);
        int[] privacyPolicyVersionNumbers = resources.getIntArray(R.array.privacy_policy_version_numbers);
        int currentVersion = privacyPolicyVersionNumbers[currentPrivacyPolicyIndex];
        return getAgreeToPolicy(context, policyKey, currentVersion);
    }

    /**
     * Method to append a policy version code to a policy preference key. This method performs a
     * simple concat, but is intended to keep policy versioning consistent
     *
     * @param policyKey         Static key of the policy
     * @param policyVersion     Current policy version code
     * @return                  Policy key annotated with version code
     */
    private static String getPolicyKeyForVersion(String policyKey, int policyVersion) {
        return policyKey + "." + policyVersion;
    }

    /**
     * If a user has not agreed to the latest privacy policy, this method can be used to verify
     * if the user has never agreed to the privacy policy (or rejected it), or if the user already
     * did agree to a previous version of the privacy policy
     *
     * @param context   Application context
     * @return  Boolean, true if user has explicitly agreed with at least one version of the
     *          privacy policy for this key. False otherwise
     */
    public static boolean getAgreeToOverallPolicy(Context context, String policyKey) {
        return getPreferenceBoolean(context, policyKey);
    }
}
