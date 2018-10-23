package nl.uscki.appcki.android.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.events.PrivacyPolicyPreferenceChangedEvent;
import nl.uscki.appcki.android.fragments.PrivacyPolicyModalFragment;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.VibrationPatternPreferenceHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;

import java.util.ArrayList;
import java.util.List;

import static butterknife.internal.Utils.arrayOf;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    final static int MY_PERMISSION_REQUEST_READ_WRITE_CALENDAR = 1;
    final static int MY_PERMISSION_REQUEST_WRITE_CONTACT = 2;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * When a vibrate pattern is selected, vibrate that pattern as feedback on
     * the chosen selection
     */
    private static Preference.OnPreferenceChangeListener sVibratePreferenceValueChangedListener =
            new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // Can't have two onPreferenceChange listeners, so execute the other one as well
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);

            ListPreference listPreference = (ListPreference) preference;

            // Get the vibrator system service
            Context context = App.getContext();
            Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            if(v == null || !v.hasVibrator()) {
                return false;
            }

            // Get the index of the value
            String stringValue = value.toString();
            int index = listPreference.findIndexOfValue(stringValue);

            // Create a vibration preference helper
            VibrationPatternPreferenceHelper vibrationPreferenceHelper =
                    new VibrationPatternPreferenceHelper();

            // Acquire the pattern corresponding to the found index
            int patternArrayId =
                    vibrationPreferenceHelper.getVibrationPatternResourceIdAtIndex(index);
            long[] pattern = vibrationPreferenceHelper.getVibrationPattern(patternArrayId);

            // Vibrate using the selected pattern (method depends on API version)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect vbe = VibrationEffect.createWaveform(pattern, -1);
                v.vibrate(vbe);
            } else {
                v.vibrate(pattern, -1);
            }

            // We made it!
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || PrivacyPolicyPreferenceFragment.class.getName().equals(fragmentName)
                || exportOptionsPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    public void requestCalendarPermissions() {
        if(
                ContextCompat.checkSelfPermission(
                        App.getContext(),
                        Manifest.permission.READ_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                                App.getContext(),
                                Manifest.permission.WRITE_CALENDAR
                        ) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                    MY_PERMISSION_REQUEST_READ_WRITE_CALENDAR
            );
        } else {
            exportOptionsPreferenceFragment sourceFragment =
                    (exportOptionsPreferenceFragment) getFragmentManager().findFragmentById(exportOptionsPreferenceFragment.id);
            sourceFragment.updateCalendarItemOptions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        exportOptionsPreferenceFragment sourceFragment =
                (exportOptionsPreferenceFragment) getFragmentManager().findFragmentById(exportOptionsPreferenceFragment.id);
        if(sourceFragment == null) {
            Log.e("SettingsActivityUpdater", "Could not find export options fragment");
            return;
        }

        switch(requestCode) {
            case MY_PERMISSION_REQUEST_READ_WRITE_CALENDAR: {

                if (
                        grantResults.length > 0 &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED
                        ) {
                    sourceFragment.updateCalendarItemOptions();
                } else {
                    Log.e("RequestPermission", "Callback on requestPermission unsuccessful");
                    SwitchPreference useExport =
                            (SwitchPreference) sourceFragment.findPreference("calendar_use_export");
                    if(useExport != null) {
                        useExport.setChecked(false);
                    }
                }
            }
        }
    }



    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrivacyPolicyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_privacy_policy);
            setHasOptionsMenu(true);

            updatePolicySummaries();
            registerOpenPolicyListener();
            EventBus.getDefault().register(this);
        }

        /**
         * Register an OnClickListener to open the privacy policy dialog when the user taps on
         * the corresponding preference
         */
        private void registerOpenPolicyListener() {

            Preference pref = findPreference("privacy_policy_show_modal");
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    PrivacyPolicyModalFragment policyModalFragment = new PrivacyPolicyModalFragment();
                    policyModalFragment.show(getFragmentManager(), "privacyPolicyDialog");
                    return false;
                }
            });
        }

        /**
         * Update all policy summaries to reflect the user's choice
         */
        private void updatePolicySummaries() {
            updateHasAgreedPolicySummary(
                    "privacy_policy_general_agreement",
                    PermissionHelper.AGREE_GENERAL_POLICY_KEY);
            updateHasAgreedPolicySummary(
                    "privacy_policy_app_agreement",
                    PermissionHelper.AGREE_APP_POLICY_KEY);
            updateHasAgreedPolicySummary(
                    "privacy_policy_notifications_agreement",
                    PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY);
        }

        /**
         * Update a single policy summary to reflect the user's choice
         * @param preferenceKey         Key of the preference on the screen
         * @param storedPreferenceKey   Key of the preference in the stored preferences
         */
        private void updateHasAgreedPolicySummary(String preferenceKey, String storedPreferenceKey) {
            Preference agreedPreference = findPreference(preferenceKey);
            boolean agreed = PermissionHelper.getPreferenceBoolean(getActivity(), storedPreferenceKey);
            agreedPreference.setSummary(agreed ? R.string.privacy_policy_agreed : R.string.privacy_policy_disagreed);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        /**
         * Listen to changes in the policy agreement of the user, so the summaries on this
         * page can be updated accordingly
         *
         * @param event Event signaling a possible change in status of user agreement with policies
         */
        public void onEventMainThread(PrivacyPolicyPreferenceChangedEvent event) {
            updatePolicySummaries();
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        String[] catPrefix = new String[] {
                "notifications_interactive_",
                "notifications_general_",
                "notifications_personal_",
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
               prepareForAndroidOreo();
            } else {
                prepareForOlderThanOreo();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void prepareForAndroidOreo() {
            addPreferencesFromResource(R.xml.pref_notifications_min_oreo);

            Preference systemSettings = findPreference("notifications_system_preferences");
            systemSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, App.getContext().getPackageName());
                    startActivity(intent);
                    return true;
                }
            });

            Preference vibration = findPreference("notifications_oreo_vibrate");
            Preference vibrationPattern = findPreference("notifications_oreo_vibration_pattern");
            Preference notificationsDisabled = findPreference("privacy_policy_notifications_disabled");

            if(PermissionHelper.getPreferenceBoolean(getContext(), PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY)) {
                getPreferenceScreen().removePreference(notificationsDisabled);
                vibration.setEnabled(true);
                vibrationPattern.setEnabled(true);
            }

            Vibrator v = (Vibrator) App.getContext().getSystemService(VIBRATOR_SERVICE);
            if(v == null || !v.hasVibrator()) {
                // Remove vibration options from menu
                PreferenceScreen screen = getPreferenceScreen();
                screen.removePreference(vibrationPattern);
            } else {
                // Bind and trigger summary changed
                bindPreferenceSummaryToValue(vibrationPattern);

                // Replace the listener with one that also vibrates on change
                vibrationPattern
                        .setOnPreferenceChangeListener(sVibratePreferenceValueChangedListener);
            }
        }

        private void prepareForOlderThanOreo() {
            addPreferencesFromResource(R.xml.pref_notification);
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            for(String prefKey : catPrefix) {
                bindPreferenceSummaryToValue(findPreference(prefKey + "new_message_ringtone"));
            }

            PreferenceScreen screen = getPreferenceScreen();
            Preference notificationsDisabled = findPreference("privacy_policy_notifications_disabled");

            if(PermissionHelper.getPreferenceBoolean(getActivity(), PermissionHelper.AGREE_NOTIFICATION_POLICY_KEY)) {
                screen.removePreference(notificationsDisabled);
                findPreference("notifications_cat_interactive").setEnabled(true);
                findPreference("notifications_cat_updates").setEnabled(true);
                findPreference("notifications_cat_personal").setEnabled(true);
            }

            Vibrator v = (Vibrator) App.getContext().getSystemService(VIBRATOR_SERVICE);
            if(v != null && v.hasVibrator()) {
                for(String prefKey : catPrefix) {
                    // Bind and trigger summary changed
                    bindPreferenceSummaryToValue(findPreference(prefKey + "vibration_pattern"));

                    // Replace the listener with one that also vibrates on change
                    findPreference(prefKey + "vibration_pattern")
                            .setOnPreferenceChangeListener(sVibratePreferenceValueChangedListener);
                }
            } else {
                for(String prefKey : catPrefix) {
                    screen.removePreference(findPreference(prefKey + "new_message_vibrate"));
                    screen.removePreference(findPreference(prefKey + "vibration_pattern"));
                }
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class exportOptionsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public static int id;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            id = getId();

            addPreferencesFromResource(R.xml.pref_export_options);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            bindPreferenceSummaryToValue(findPreference("calendar_selected_id"));
            updateCalendarItemOptions();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            Preference preference = findPreference(s);
            Preference pref = findPreference(s);
            if(preference instanceof SwitchPreference) {
                SwitchPreference switchPreference = (SwitchPreference) preference;
                if(pref != null && switchPreference.isChecked()) {
                    SettingsActivity settingsActivity = ((SettingsActivity) getActivity());
                    switch(s) {
                        case "calendar_use_export":
                            settingsActivity.requestCalendarPermissions();
                            break;
                    }
                }
            }
        }

        public void updateCalendarItemOptions() {
            ListPreference calendarValues = (ListPreference) findPreference("calendar_selected_id");

            if(calendarValues == null) return;

            ArrayList<Pair<String,String>> calendarList =
                    CalendarHelper.getInstance().getCalendarList();

            if(calendarList.isEmpty()) {
                SwitchPreference use_cal_export = (SwitchPreference) findPreference("calendar_use_export");
                use_cal_export.setChecked(false);
                String errorMessage = getResources().getString(R.string.pref_save_in_calendar_empty_calendar_list_error);
                Context context = getActivity();
                if(context != null) {
                    Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
                }

                return;
            }

            CharSequence[] calendarIds = new CharSequence[calendarList.size()];
            CharSequence[] calendarNames = new CharSequence[calendarList.size()];

            for(int i = 0; i < calendarList.size(); i++) {
                Pair<String,String> calendarItem = calendarList.get(i);
                calendarIds[i] = calendarItem.first;
                calendarNames[i] = calendarItem.second;
            }

            calendarValues.setEntries(calendarNames);
            calendarValues.setEntryValues(calendarIds);

            // No value selected yet. Don't do anything
            if(calendarValues.getValue() == null) return;

            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    calendarValues,
                    calendarValues.getValue()
            );
        }
    }
}
