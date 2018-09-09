package nl.uscki.appcki.android.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
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

import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.fragments.comments.CommentsFragment;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.calendar.AgendaSubscribeServiceHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarServiceHelper;

/**
 * Created by peter on 3/21/17.
 */

public class NotificationReceiver extends FirebaseMessagingService {
    private final String TAG = getClass().getSimpleName();

    public static final String PARAM_NOTIFICATION_TITLE = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TITLE";
    public static final String PARAM_NOTIFICATION_CONTENT = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_CONTENT";
    public static final String PARAM_NOTIFICATION_TAG = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_TAG";
    public static final String PARAM_NOTIFICATION_ID = "nl.uscki.appcki.android.services.extra.PARAM_NOTIFICATION_ID";
    public static final String PARAM_VIEW_ITEM_ID = "nl.uscki.appcki.android.services.extra.PARAM_VIEW_ITEM_ID";

    // Groups should be appended with the ID of the item in that group
    private static final String GROUP_KEY_AGENDA = "nl.uscki.appcki.android.groupkey.AGENDA.";
    private static final String GROUP_KEY_MEETING = "nl.uscki.appcki.android.groupkey.MEETING.";
    private static final String GROUP_KEY_FORUM = "nl.uscki.appcki.android.groupkey.FORUM.";

    private Context context;

    public NotificationReceiver() {
        // Zero argument constructor required by the system to create services the way they are intended
        this.context = this;
    }

    public NotificationReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If this service is used as a service, use the service context
        this.context = this;

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

        Log.e(TAG, title + "\tContent: " + content + "\tid: " + id);

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
        String notificationTag = type.toString();

        String mainBackstackAction = null;

        switch (type) {
            case meeting_filledin:
                intent = new Intent(this, MeetingActivity.class);
                intent.putExtra(MeetingActivity.PARAM_MEETING_ID, id);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_STATUS);
                }
                mainBackstackAction = MainActivity.ACTION_MEETING_OVERVIEW;
            case meeting_planned:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                intent = new Intent(this, MeetingActivity.class);
                intent.putExtra(MeetingActivity.PARAM_MEETING_ID, id);

                if(PermissionHelper.canExportMeetingAuto()) {
                    // Start a service to export this meeting to calendar
                    EventExportJobService.enqueueExportMeetingToCalendarAction(this, id);
                } else {
                    // Add a button to export meeting
                    Intent exportMeetingIntent =
                            new Intent(this, EventExportIntentService.class);

                    exportMeetingIntent.setAction(CalendarServiceHelper.ACTION_MEETING_EXPORT);
                    exportMeetingIntent.putExtra(CalendarServiceHelper.PARAM_MEETING_ID, id);

                    PendingIntent exportMeetingpIntent =
                            PendingIntent.getService(
                                    this,
                                    0,
                                    exportMeetingIntent,
                                    0);

                    n.addAction(
                            R.drawable.calendar,
                            getResources().getString(R.string.action_meeting_save),
                            exportMeetingpIntent
                    );
                }
                mainBackstackAction = MainActivity.ACTION_MEETING_OVERVIEW;
                break;
            case meeting_new:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                intent = new Intent(this, MeetingActivity.class);
                intent.putExtra(MeetingActivity.PARAM_MEETING_ID, id);
                mainBackstackAction = MainActivity.ACTION_MEETING_OVERVIEW;
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

                // TODO: Isn't this handled at the end of this function? And overwritten by that stuff?
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, forumIntent, 0);
                n.setContentIntent(pIntent);
                break;
            case agenda_announcement:
                intent = new Intent(this, AgendaActivity.class);
                intent.setAction(CommentsFragment.ACTION_VIEW_COMMENTS);
                intent.putExtra(AgendaActivity.PARAM_AGENDA_ID, id);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                mainBackstackAction = MainActivity.ACTION_AGENDA_OVERVIEW;
                break;
            case agenda_new:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_EVENT);
                }
                addAgendaActions(n, id,true, true, title, content, id, notificationTag);
                intent = new Intent(this, AgendaActivity.class);
                intent.putExtra(AgendaActivity.PARAM_AGENDA_ID, id);
                addReproducabilityExtras(intent, title, content, notificationTag, id, id);
                mainBackstackAction = MainActivity.ACTION_AGENDA_OVERVIEW;
                break;
            case agenda_reply:
                intent = new Intent(this, AgendaActivity.class);
                intent.setAction(CommentsFragment.ACTION_VIEW_COMMENTS);
                intent.putExtra(AgendaActivity.PARAM_AGENDA_ID, id);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_SOCIAL);
                }
                mainBackstackAction = MainActivity.ACTION_AGENDA_OVERVIEW;
                break;
            case news:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    n.setCategory(Notification.CATEGORY_RECOMMENDATION);
                }
                intent = new Intent(this, MainActivity.class);
                intent.setAction(MainActivity.ACTION_VIEW_NEWSITEM);
                intent.putExtra(MainActivity.PARAM_NEWS_ID, id);
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
            addIntentionsToNotification(n, intent, mainBackstackAction);
        }

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(notificationTag, id, n.build());

        NotificationUtil nUtil = new NotificationUtil(this);
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
        Bitmap bm = Utils.getBitmapFromVectorDrawable(context, R.drawable.ic_wilson);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        NotificationUtil notificationUtil = new NotificationUtil(context);

        // Since Android Oreo we use notification channels
        NotificationCompat.Builder n  = new NotificationCompat.Builder(context,
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
     * @param tag           Tag with which the user is notified of this notification
     * @param id            The ID of the notification (for overwriting purposes)
     * @param itemId        The ID of the item the action view should open (-1 for empty)
     */
    public void addReproducabilityExtras(Intent intent, String title, String content, String tag, int id, int itemId) {
        intent.putExtra(PARAM_NOTIFICATION_ID, id);
        intent.putExtra(PARAM_NOTIFICATION_TAG, tag);
        intent.putExtra(PARAM_NOTIFICATION_TITLE, title);
        intent.putExtra(PARAM_NOTIFICATION_CONTENT, content);
        intent.putExtra(PARAM_VIEW_ITEM_ID, itemId);
        // TODO extend with notification group
    }

    /**
     * Add properties to an intention to open a detail view
     * @param notification      The notification this intent is intended for
     * @param intent            The base intention of the notification
     * @param mainBackstackAction (Optional) The action name of the MAIN ACTIVITY to call when
     *                            pressing back from the activity this notification opens
     */
    public void addIntentionsToNotification(
            NotificationCompat.Builder notification,
            Intent intent,
            String mainBackstackAction
    ) {
        if(intent.getAction() == null)
            intent.setAction(Intent.ACTION_VIEW);

        PendingIntent pIntent;

        if(mainBackstackAction == null) {
            pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        } else {
            // NOTE: This solution is going to fail horribly if a backpress no longer always
            // should go to the MainActivity
            Intent backPressedIntent = new Intent(context, MainActivity.class);
            backPressedIntent.setAction(mainBackstackAction);

            pIntent =
                    TaskStackBuilder.create(context)
                            .addNextIntent(backPressedIntent)
                            .addNextIntent(intent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        notification.setContentIntent(pIntent);
    }

    /**
     * Add export and subscribe action buttons to a new agenda event notification
     * @param notification          New Agenda Event base notification to add these buttons to
     * @param agendaId              ID of the new agenda event
     * @param allowExport           True if export button should be added
     * @param allowSubscribe        True if subscribe button should be added
     * @param title                 Title of the event
     * @param content               Notification body, i.e. event extra information
     * @param notificationid        ID with which the user will be notified of this event
     * @param notificationTag       Tag with which the user will be notified of this event
     *
     */
    public void addAgendaActions(
            NotificationCompat.Builder notification,
            int agendaId,
            boolean allowExport,
            boolean allowSubscribe,
            String title,
            String content,
            int notificationid,
            String notificationTag
            )
    {
        if(allowExport) {
            // Build an export action
            Intent exportAgendaIntent = new Intent(context, EventExportIntentService.class);
            addReproducabilityExtras(exportAgendaIntent, title, content, notificationTag, notificationid, agendaId);
            exportAgendaIntent.setAction(CalendarServiceHelper.ACTION_AGENDA_EXPORT);
            exportAgendaIntent.putExtra(CalendarServiceHelper.PARAM_AGENDA_ID, agendaId);

            PendingIntent exportAgendapIntent =
                    PendingIntent.getService(context, 0, exportAgendaIntent, 0);

            String agenda_export_label = context.getResources().getString(R.string.action_agenda_export);
            notification.addAction(R.drawable.calendar, agenda_export_label, exportAgendapIntent);
        }

        if (allowSubscribe && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            // Building remote input object
            String subscribeLabel = context.getResources().getString(R.string.action_agenda_subscribe);
            RemoteInput remoteInput = new RemoteInput.Builder(AgendaSubscribeServiceHelper.PARAM_SUBSCRIBE_COMMENT)
                    .setLabel(subscribeLabel)
                    .setAllowFreeFormInput(true)
                    .build();

            // Creating a pending intent for this action
            Intent subscribeIntent = new Intent(context, AgendaSubscriberIntentService.class);
            addReproducabilityExtras(subscribeIntent, title, content, notificationTag, notificationid, agendaId);
            subscribeIntent.setAction(AgendaSubscribeServiceHelper.ACTION_SUBSCRIBE_AGENDA);
            subscribeIntent.putExtra(AgendaSubscribeServiceHelper.PARAM_AGENDA_ID, agendaId);
            PendingIntent agendaSubscribepIntent = PendingIntent.getService(
                    context,
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
        // Extract information from existing intent
        String title = intent.getStringExtra(PARAM_NOTIFICATION_TITLE);
        String content = intent.getStringExtra(PARAM_NOTIFICATION_CONTENT);
        int notification_id = intent.getIntExtra(PARAM_NOTIFICATION_ID, -1);
        int agenda_id = intent.getIntExtra(PARAM_VIEW_ITEM_ID, -1);
        String notification_tag = intent.getStringExtra(PARAM_NOTIFICATION_TAG);

        // Build similar notification
        NotificationCompat.Builder notification = getIntentlessBaseNotification(NotificationType.agenda_new, title, content);
        addAgendaActions(notification, agenda_id, allowExport, allowSubscribe, title, content, notification_id, notification_tag);

        // Create a new intention, because the last was already final
        Intent newIntention = new Intent(context, AgendaActivity.class);
        addIntentionsToNotification(notification, newIntention, null);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Don't make a sound this time (update quietly)
        notification.setOnlyAlertOnce(true);

        // Update
        notificationManager.notify(notification_tag, notification_id, notification.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
