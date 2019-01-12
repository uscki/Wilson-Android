package nl.uscki.appcki.android.helpers.notification.news;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.NotificationType;

public class NewsNotification extends BadWolfNotification {

    public NewsNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.news;
    }

    @Override
    protected Intent getIntent() {
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_VIEW_NEWSITEM);
        intent.putExtra(MainActivity.PARAM_NEWS_ID, id);

        return intent;
    }

    @Override
    protected String getBackstackAction() {
        return null;
    }

    @Override
    protected String getNotificationCategory() {
        return Notification.CATEGORY_RECOMMENDATION;
    }

    @Override
    protected void addActions() {

    }
}
