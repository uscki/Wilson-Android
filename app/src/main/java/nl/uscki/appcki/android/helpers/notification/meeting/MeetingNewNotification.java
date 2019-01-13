package nl.uscki.appcki.android.helpers.notification.meeting;

import android.content.Context;
import android.content.Intent;
import com.google.firebase.messaging.RemoteMessage;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;

public class MeetingNewNotification extends BadWolfNotification {

    public MeetingNewNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent intent = new Intent(this.context, MeetingActivity.class);
        intent.putExtra(MeetingActivity.PARAM_MEETING_ID, id);
        return intent;
    }

    @Override
    protected void addActions() {

    }
}
