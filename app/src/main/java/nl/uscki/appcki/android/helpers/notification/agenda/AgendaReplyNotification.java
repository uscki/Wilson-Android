package nl.uscki.appcki.android.helpers.notification.agenda;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.NotificationType;

public class AgendaReplyNotification extends BadWolfNotification {
    public AgendaReplyNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.agenda_reply;
    }

    @Override
    protected Intent getIntent() {
        Intent intent = new Intent(this.context, AgendaActivity.class);
        intent.setAction(CommentsFragment.ACTION_VIEW_COMMENTS);
        intent.putExtra(AgendaActivity.PARAM_AGENDA_ID, id);

        return intent;
    }

    @Override
    protected String getBackstackAction() {
        return MainActivity.ACTION_AGENDA_OVERVIEW;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected String getNotificationCategory() {
        return Notification.CATEGORY_SOCIAL;
    }

    @Override
    protected void addActions() {

    }
}
