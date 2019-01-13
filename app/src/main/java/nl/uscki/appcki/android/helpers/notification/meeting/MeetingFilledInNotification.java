package nl.uscki.appcki.android.helpers.notification.meeting;

import android.content.Context;
import android.content.Intent;
import com.google.firebase.messaging.RemoteMessage;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;

public class MeetingFilledInNotification extends BadWolfNotification {

    public MeetingFilledInNotification(Context c, RemoteMessage m) {
        super(c, m);
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent i = new Intent(this.context, MeetingActivity.class);
        i.putExtra(MeetingActivity.PARAM_MEETING_ID, this.id);
        return i;
    }

    @Override
    protected void addActions() { }
}
