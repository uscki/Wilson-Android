package nl.uscki.appcki.android.helpers.notification.forum;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.fragments.forum.ForumTopicOverviewFragment;
import nl.uscki.appcki.android.helpers.notification.AbstractNotification;

public class ForumNewTopicNotification extends AbstractNotification {

    public ForumNewTopicNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_VIEW_FORUM_TOPIC);
        intent.putExtra(ForumTopicOverviewFragment.ARG_TOPIC_ID, this.id);
        return intent;
    }

    @Override
    protected void addActions() {

    }
}
