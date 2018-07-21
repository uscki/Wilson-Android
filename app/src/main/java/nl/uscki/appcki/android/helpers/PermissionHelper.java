package nl.uscki.appcki.android.helpers;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.util.prefs.Preferences;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.generated.meeting.Preference;

public class PermissionHelper {
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
        boolean hasRequestedExport = prefs.getBoolean("calendar_use_export", false);
        return hasPermission && hasRequestedExport;
    }

    public static boolean canDeleteCalendar() {
        return canExportCalendar() && hasPermission(Manifest.permission.READ_CALENDAR);
    }

    public static boolean canExportCalendarAuto() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean hasRequestedAutoExport = prefs.getBoolean("event_export_auto", false);

        return canExportCalendar() && hasRequestedAutoExport;
    }
}
