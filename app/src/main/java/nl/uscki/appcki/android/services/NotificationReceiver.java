package nl.uscki.appcki.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.VibrationPatternPreferenceHelper;

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

        NotificationCompat.Builder n = getIntentlessBaseNotification(type, title, content);

        Intent intent = null;

        switch (type) {
            case meeting_filledin:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_STATUS);
                }
            case meeting_planned:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                intent = new Intent(App.getContext(), MeetingActivity.class);

                if(PermissionHelper.canExportMeetingAuto()) {
                    // Start a service to export this meeting to calendar
                    EventExportService.startExportMeetingToCalendarAction(App.getContext(), id);
                } else {
                    // Add a button to export meeting
                    Intent exportMeetingIntent =
                            new Intent(App.getContext(), EventExportService.class);

                    exportMeetingIntent.setAction(EventExportService.ACTION_MEETING_EXPORT);
                    exportMeetingIntent.putExtra(EventExportService.PARAM_MEETING_ID, id);

                    PendingIntent exportMeetingpIntent =
                            PendingIntent.getService(
                                    App.getContext(),
                                    0,
                                    exportMeetingIntent,
                                    0);

                    n.addAction(
                            R.drawable.calendar,
                            App.getContext().getResources().getString(R.string.action_meeting_save),
                            exportMeetingpIntent
                    );
                }
                break;
            case meeting_new:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                intent = new Intent(App.getContext(), MeetingActivity.class);
                break;
            case forum_reply:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_SOCIAL);
                }
                break;
            case forum_new_topic:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_SOCIAL);
                }
                Intent forumIntent = new Intent(Intent.ACTION_VIEW);

                forumIntent.setData(Uri.parse(String.format(
                        Locale.ENGLISH,
                        "https://www.uscki.nl/?pagina=Forum/ViewTopic&topicId=%d&newest=true#newest",
                        id)
                        .trim()));

                PendingIntent pIntent = PendingIntent.getActivity(App.getContext(), 0, forumIntent, 0);
                n.setContentIntent(pIntent);
                break;
            case agenda_announcement:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                break;
            case agenda_new:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                addAgendaActions(n, id,true, true, title, content, id);
                intent = new Intent(App.getContext(), AgendaActivity.class);
                addReproducabilityExtras(intent, title, content, id, id);
                break;
            case agenda_reply:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_SOCIAL);
                }
                intent = new Intent(App.getContext(), AgendaActivity.class);
                break;
            case news:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_RECOMMENDATION);
                }
                intent = new Intent(App.getContext(), MainActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("screen", MainActivity.Screen.NEWS.toString());
                break;
            case achievement: // what do?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_PROMO);
                }
                break;
            case other: // what we do?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_SOCIAL);
                }
                break;
        }

        if(intent != null) {
            addIntentionsToNotification(n, intent, id);
        }

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(App.getContext());
        notificationManager.notify(id, n.build());

        NotificationUtil nUtil = new NotificationUtil(App.getContext());
        nUtil.vibrateIfEnabled(type);
    }

    /**
     * Build a default notification without an intent.
     * This function handles markup
     * @param title         Notification title
     * @param content       Notification body
     * @return              NotificatioNCompat.Builder
     */
    public NotificationCompat.Builder getIntentlessBaseNotification(NotificationType notificationType, String title, String content) {
        // Because androids default BitmapFactory doesn't work with vector drawables
        Bitmap bm = Utils.getBitmapFromVectorDrawable(App.getContext(), R.drawable.ic_wilson);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        NotificationUtil notificationUtil = new NotificationUtil(App.getContext());

        // Since Android Oreo we use notification channels
        NotificationCompat.Builder n  = new NotificationCompat.Builder(App.getContext(),
                notificationUtil.getChannel(notificationType));

        // On older devices, we mock-up our own channels, based on the preferences
        notificationUtil.addNotificationPropertiesBySettings(n, notificationType);

        n.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);

        n.setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            n.setSmallIcon(R.drawable.cki_logo_white)
                    .setLargeIcon(bm);
        } else {
            n.setSmallIcon(R.drawable.cki_logo_white);
        }

        return n;
    }

    /**
     * Put extra's to a notification intent that help reproduce or overwrite a notification
     * @param intent        The intent to put the extras to
     * @param title         The notification title
     * @param content       The notification body
     * @param id            The ID of the notification (for overwriting purposes)
     * @param itemId        The ID of the item the action view should open (-1 for empty)
     */
    public void addReproducabilityExtras(Intent intent, String title, String content, int id, int itemId) {
        intent.putExtra(PARAM_NOTIFICATION_ID, id);
        intent.putExtra(PARAM_NOTIFICATION_TITLE, title);
        intent.putExtra(PARAM_NOTIFICATION_CONTENT, content);
        intent.putExtra(PARAM_VIEW_ITEM_ID, itemId);
        // TODO extend with notification group and tag
    }

    /**
     * Add properties to an intention to open a detail view
     * @param notification      The notification this intent is intended for
     * @param intent            The base intention of the notification
     * @param id                The ID of the item to open
     */
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

    /**
     * Add export and subscribe action buttons to a new agenda event notification
     * @param notification          New Agenda Event base notification
     * @param agendaId              ID of the new agenda event
     * @param allowExport           True if export button should be added
     * @param allowSubscribe        True if subscribe button should be added
     * @param title                 Title of the event
     * @param content               Notification body, i.e. event extra information
     * @param notificationid        The notification object to add these buttons to
     */
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

    /**
     * Reproduce a notification for a new agenda event based on parameters in the intention, and
     * overwrite the previous notification
     * @param intent            The intent of an action of the existing notification
     * @param allowExport       True iff export buttons should be added
     * @param allowSubscribe    True iff subscribe buttons should be added
     */
    public void buildNewAgendaItemNotificationFromIntent(Intent intent, boolean allowExport, boolean allowSubscribe) {
        Log.e("BuildAgendaNotification", "Building new agenda notification from existing intent");
        String title = intent.getStringExtra(PARAM_NOTIFICATION_TITLE);
        String content = intent.getStringExtra(PARAM_NOTIFICATION_CONTENT);
        int notification_id = intent.getIntExtra(PARAM_NOTIFICATION_ID, -1);
        int agenda_id = intent.getIntExtra(PARAM_VIEW_ITEM_ID, -1);

        NotificationCompat.Builder notification = getIntentlessBaseNotification(NotificationType.agenda_new, title, content);
        addAgendaActions(notification, agenda_id, allowExport, allowSubscribe, title, content, notification_id);

        // Create a new intention, because the last was already final
        Intent newIntention = new Intent(App.getContext(), AgendaActivity.class);
        addIntentionsToNotification(notification, newIntention, agenda_id);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());

        // TODO: Disable making a sound or vibrating, below does not work
        notification.setDefaults(Notification.DEFAULT_ALL);
        notificationManager.notify(notification_id, notification.build());

        // TODO: Check if the follow *does* work
        notification.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);

        // TODO: This might work as well:
        notification.setOnlyAlertOnce(true);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
