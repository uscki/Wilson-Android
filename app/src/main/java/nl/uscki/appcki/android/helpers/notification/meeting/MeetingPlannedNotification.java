package nl.uscki.appcki.android.helpers.notification.meeting;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarServiceHelper;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.EventExportIntentService;
import nl.uscki.appcki.android.services.EventExportJobService;
import nl.uscki.appcki.android.services.NotificationType;

public class MeetingPlannedNotification extends BadWolfNotification {

    public MeetingPlannedNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.meeting_planned;
    }

    @Override
    protected Intent getIntent() {
        Intent intent = new Intent(this.context, MeetingActivity.class);
        intent.putExtra(MeetingActivity.PARAM_MEETING_ID, id);
        return intent;
    }

    @Override
    protected String getBackstackAction() {
        return MainActivity.ACTION_MEETING_OVERVIEW;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected String getNotificationCategory() {
        return Notification.CATEGORY_EVENT;
    }

    @Override
    protected void addActions() {
        if(PermissionHelper.canExportMeetingAuto()) {
            // Start a service to export this meeting to calendar
            EventExportJobService.enqueueExportMeetingToCalendarAction(this.context, id);
        } else {
            // Add a button to export meeting
            Intent exportMeetingIntent =
                    new Intent(this.context, EventExportIntentService.class);

            exportMeetingIntent.setAction(CalendarServiceHelper.ACTION_MEETING_EXPORT);
            exportMeetingIntent.putExtra(CalendarServiceHelper.PARAM_MEETING_ID, id);

            PendingIntent exportMeetingpIntent =
                    PendingIntent.getService(
                            this.context,
                            0,
                            exportMeetingIntent,
                            0);

            notificationBuilder.addAction(
                    R.drawable.calendar,
                    this.context.getResources().getString(R.string.action_meeting_save),
                    exportMeetingpIntent
            );
        }
    }
}
