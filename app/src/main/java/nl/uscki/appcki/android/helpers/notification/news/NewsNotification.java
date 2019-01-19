package nl.uscki.appcki.android.helpers.notification.news;

import android.content.Context;
import android.content.Intent;
import com.google.firebase.messaging.RemoteMessage;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.helpers.notification.AbstractNotification;

public class NewsNotification extends AbstractNotification {

    public NewsNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_VIEW_NEWSITEM);
        intent.putExtra(MainActivity.PARAM_NEWS_ID, id);

        return intent;
    }

    @Override
    protected void addActions() {

    }
}
