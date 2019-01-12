package nl.uscki.appcki.android.helpers.notification.meeting;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.NotificationType;

public class MeetingFilledInNotification extends BadWolfNotification {

    public MeetingFilledInNotification(Context c, RemoteMessage m) {
        super(c, m);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.meeting_filledin;
    }

    @Override
    protected Intent getIntent() {
        Intent i = new Intent(this.context, MeetingActivity.class);
        i.putExtra(MeetingActivity.PARAM_MEETING_ID, this.id);
        return i;
    }

    @Override
    protected String getBackstackAction() {
        return MainActivity.ACTION_MEETING_OVERVIEW;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected String getNotificationCategory() {
        return Notification.CATEGORY_STATUS;
    }

    @Override
    protected void addActions() { }
}
