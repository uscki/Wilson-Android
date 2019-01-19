package nl.uscki.appcki.android.helpers.notification.agenda;

import android.content.Context;
import android.content.Intent;
import com.google.firebase.messaging.RemoteMessage;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.helpers.notification.AbstractNotification;

public class AgendaReplyNotification extends AbstractNotification {
    public AgendaReplyNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent intent = new Intent(this.context, AgendaActivity.class);
        intent.setAction(CommentsFragment.ACTION_VIEW_COMMENTS);
        intent.putExtra(AgendaActivity.PARAM_AGENDA_ID, id);

        return intent;
    }

    @Override
    protected void addActions() {

    }
}
