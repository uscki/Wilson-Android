package nl.uscki.appcki.android.helpers.notification.achievement;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.NotificationType;

public class AchievementNotification extends BadWolfNotification {
    public AchievementNotification(Context c, RemoteMessage message) {
        super(c, message);
    }


    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.achievement;
    }

    @Override
    protected Intent getIntent() {
        return null;
    }

    @Override
    protected String getBackstackAction() {
        return null;
    }

    @Override
    protected String getNotificationCategory() {
        return Notification.CATEGORY_PROMO;
    }

    @Override
    protected void addActions() {

    }
}
