package nl.uscki.appcki.android.helpers.notification.bugtracker;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

public class BugTrackerCommentNotification extends BugTrackerNotification {

    public BugTrackerCommentNotification(Context c, RemoteMessage message) {
        super(c, message);
    }
}
