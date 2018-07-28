package nl.uscki.appcki.android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;

/**
 * Created by peter on 3/21/17.
 */

public class NotificationReceiver extends FirebaseMessagingService {
    private final String TAG = getClass().getSimpleName();

    private final String PARAM_NOTIFICATION_TITLE = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TITLE";
    private final String PARAM_NOTIFICATION_CONTENT = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_CONTENT";
    private final String PARAM_NOTIFICATION_ID = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_ID";
    private final String PARAM_VIEW_ITEM_ID = "nl.uscki.appcki.android.services.extra.PARAM_VIEW_ITEM_ID";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.

        // TODO maybe handle building in NotificationUtil? Allows sending of notifications from other places
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom() + "\n");

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        } else {
            Log.e(TAG, "No message data payload found");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification: " + remoteMessage.getNotification());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        } else {
            Log.e(TAG, "No Message Notification Body found");
        }

        String title = remoteMessage.getData().get("title");
        String content = remoteMessage.getData().get("content");
        int id = Integer.parseInt(remoteMessage.getData().get("id"));

        Log.e(TAG, title + "\tContent: " + content);

        NotificationType type;
        try {
            String sentType = remoteMessage.getData().get("type");
            Log.e(TAG, "Getting type of " + sentType);
            type = NotificationType.valueOf(sentType);
        } catch(NullPointerException e) {
            Log.e(TAG, e.toString());
            return;
        }

        NotificationUtil notificationUtil = new NotificationUtil(App.getContext());
        NotificationCompat.Builder n = getIntentlessBaseNotification(title, content);

        Intent intent = null;

        switch (type) {
            case meeting_filledin:
            case meeting_planned:
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_ACTIVITIES_ID));
                intent = new Intent(App.getContext(), MeetingActivity.class);

                Intent exportMeetingIntent = new Intent(App.getContext(), EventExportService.class);

                exportMeetingIntent.setAction(EventExportService.ACTION_MEETING_EXPORT);
                exportMeetingIntent.putExtra(EventExportService.PARAM_MEETING_ID, id);

                PendingIntent exportMeetingpIntent =
                        PendingIntent.getService(App.getContext(), 0, exportMeetingIntent, 0);

                n.addAction(
                        R.drawable.calendar,
                        App.getContext().getResources().getString(R.string.action_meeting_save),
                        exportMeetingpIntent
                );
                break;
            case meeting_new:
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_ACTIVITIES_ID));
                intent = new Intent(App.getContext(), MeetingActivity.class);
                break;
            case forum_reply:
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_PERSONAL_ID));
                break;
            case forum_new_topic:
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_PERSONAL_ID));
                Intent forumIntent = new Intent(Intent.ACTION_VIEW);
                forumIntent.setData(Uri.parse(String.format(Locale.ENGLISH, "https://www.uscki.nl/?pagina=Forum/ViewTopic&topicId=%d&newest=true#newest",
                        id).trim()));
                PendingIntent pIntent = PendingIntent.getActivity(App.getContext(), 0, forumIntent, 0);
                n.setContentIntent(pIntent);
                break;
            case agenda_announcement:
                break;
            case agenda_new:
                addAgendaActions(n, id,true, true, title, content, id);
                intent = new Intent(App.getContext(), AgendaActivity.class);
                addReproducabilityExtras(intent, title, content, id, id);
                break;
            case agenda_reply:
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_PERSONAL_ID));
                intent = new Intent(App.getContext(), AgendaActivity.class);
                break;
            case news:
                intent = new Intent(App.getContext(), MainActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
//                intent.putExtra("target", "news_item");
//                intent.setFlags()
                intent.putExtra("screen", MainActivity.Screen.NEWS.toString());
                break;
            case achievement: // what do?
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_PERSONAL_ID));
                break;
            case other: // what we do?
                break;
        }

        if(intent != null) {
            addIntentionsToNotification(n, intent, id);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());
        notificationManager.notify(id, n.build());
    }

    public NotificationCompat.Builder getIntentlessBaseNotification(String title, String content) {
        // Because androids default BitmapFactory doesn't work with vector drawables
        Bitmap bm = Utils.getBitmapFromVectorDrawable(App.getContext(), R.drawable.ic_wilson);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        NotificationUtil notificationUtil = new NotificationUtil(App.getContext());

        NotificationCompat.Builder n  = new NotificationCompat.Builder(App.getContext(),
                notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_GENERAL_ID));
        n.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);

        n.setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        Log.e(TAG, "Logging to " + NotificationCompat.getChannelId(n.build()).toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            n.setSmallIcon(R.drawable.cki_logo_white)
                    .setLargeIcon(bm);
        } else {
            n.setSmallIcon(R.drawable.cki_logo_white);
        }

        return n;
    }

    public void addReproducabilityExtras(Intent intent, String title, String content, int id, int itemId) {
        intent.putExtra(PARAM_NOTIFICATION_ID, id);
        intent.putExtra(PARAM_NOTIFICATION_TITLE, title);
        intent.putExtra(PARAM_NOTIFICATION_CONTENT, content);
        intent.putExtra(PARAM_VIEW_ITEM_ID, itemId);
        // TODO extend with notification group and tag
    }

    public void addIntentionsToNotification(NotificationCompat.Builder notification, Intent intent, int id) {
        intent.putExtra("id", id);
        intent.setAction(Intent.ACTION_VIEW);
        // TODO: Figure out which flags to use to create a proper back stack:
        // https://developer.android.com/training/notify-user/build-notification#click
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(App.getContext(), 0, intent, 0);
        notification.setContentIntent(pIntent);
        Log.d(TAG, "Notification intent updated, should now go to item id " + id);
    }

    public void addAgendaActions(
            NotificationCompat.Builder notification,
            int agendaId,
            boolean allowExport,
            boolean allowSubscribe,
            String title,
            String content,
            int notificationid
            )
    {
        if(allowExport) {
            // Build an export action
            Intent exportAgendaIntent = new Intent(App.getContext(), EventExportService.class);
            addReproducabilityExtras(exportAgendaIntent, title, content, notificationid, agendaId);
            exportAgendaIntent.setAction(EventExportService.ACTION_AGENDA_EXPORT);
            exportAgendaIntent.putExtra(EventExportService.PARAM_AGENDA_ID, agendaId);

            PendingIntent exportAgendapIntent =
                    PendingIntent.getService(App.getContext(), 0, exportAgendaIntent, 0);

            String agenda_export_label = App.getContext().getResources().getString(R.string.action_agenda_export);
            notification.addAction(R.drawable.calendar, agenda_export_label, exportAgendapIntent);
        }

        if (allowSubscribe && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            // Building remote input object
            String subscribeLabel = App.getContext().getResources().getString(R.string.action_agenda_subscribe);
            RemoteInput remoteInput = new RemoteInput.Builder(AgendaSubscriberService.PARAM_SUBSCRIBE_COMMENT)
                    .setLabel(subscribeLabel)
                    .setAllowFreeFormInput(true)
                    .build();

            // Creating a pending intent for this action
            Intent subscribeIntent = new Intent(App.getContext(), AgendaSubscriberService.class);
            addReproducabilityExtras(subscribeIntent, title, content, notificationid, agendaId);
            subscribeIntent.setAction(AgendaSubscriberService.ACTION_SUBSCRIBE_AGENDA);
            subscribeIntent.putExtra(AgendaSubscriberService.PARAM_AGENDA_ID, agendaId);
            PendingIntent agendaSubscribepIntent = PendingIntent.getService(
                    App.getContext(),
                    0,
                    subscribeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action subscribeAction =
                    new NotificationCompat.Action.Builder(R.drawable.plus,
                            subscribeLabel,
                            agendaSubscribepIntent)
                            .addRemoteInput(remoteInput)
                            .build();

            notification.addAction(subscribeAction);
        }
    }

    public void buildNewAgendaItemNotificationFromIntent(Intent intent, boolean allowExport, boolean allowSubscribe) {
        Log.e("BuildAgendaNotification", "Building new agenda notification from existing intent");
        String title = intent.getStringExtra(PARAM_NOTIFICATION_TITLE);
        String content = intent.getStringExtra(PARAM_NOTIFICATION_CONTENT);
        int notification_id = intent.getIntExtra(PARAM_NOTIFICATION_ID, -1);
        int agenda_id = intent.getIntExtra(PARAM_VIEW_ITEM_ID, -1);

        Log.e("BuildAgendaNotification", "Found values: Title: " + title + "\tagenda_id: " + agenda_id + "\t notification_id: " + notification_id + "\t content: " +content);

        NotificationCompat.Builder notification = getIntentlessBaseNotification(title, content);
        addAgendaActions(notification, agenda_id, allowExport, allowSubscribe, title, content, notification_id);

        // Create a new intention, because the last was alreadd final
        Intent newIntention = new Intent(App.getContext(), AgendaActivity.class);
        addIntentionsToNotification(notification, newIntention, agenda_id);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());
        // Disable making a sound or vibrating
        notification.setDefaults(Notification.DEFAULT_ALL);
        notificationManager.notify(notification_id, notification.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
