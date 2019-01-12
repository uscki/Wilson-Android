package nl.uscki.appcki.android.helpers.notification.meeting;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.NotificationType;

public class MeetingNewNotification extends BadWolfNotification {

    public MeetingNewNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected NotificationType getNotificationType() {
        return null;
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

    }
}
