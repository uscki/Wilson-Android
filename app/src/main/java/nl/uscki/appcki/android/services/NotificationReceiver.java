package nl.uscki.appcki.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.NotificationUtil;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.activities.MeetingActivity;
import nl.uscki.appcki.android.activities.NewsActivity;

/**
 * Created by peter on 3/21/17.
 */

public class NotificationReceiver extends FirebaseMessagingService {
    private final String TAG = getClass().getSimpleName();

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

        // Because androids default BitmapFactory doesn't work with vector drawables
        Bitmap bm = Utils.getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_wilson);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        NotificationUtil notificationUtil = new NotificationUtil(App.getContext());

        NotificationCompat.Builder n  = new NotificationCompat.Builder(getApplicationContext(),
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

        Intent intent = null;

        switch (type) {
            case meeting_filledin:
            case meeting_planned:
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_ACTIVITIES_ID));
                intent = new Intent(App.getContext(), MeetingActivity.class);
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
                        Integer.parseInt(remoteMessage.getData().get("id"))).trim()));
                PendingIntent pIntent = PendingIntent.getActivity(App.getContext(), 0, forumIntent, 0);
                n.setContentIntent(pIntent);
                break;
            case agenda_announcement:
                break;
            case agenda_new:
                intent = new Intent(App.getContext(), AgendaActivity.class);
                break;
            case agenda_reply:
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_PERSONAL_ID));
                intent = new Intent(App.getContext(), AgendaActivity.class);
                break;
            case news:
                intent = new Intent(App.getContext(), NewsActivity.class);
                break;
            case achievement: // what do?
                n.setChannelId(notificationUtil.getChannel(NotificationUtil.NOTIFICATION_CHANNEL_PERSONAL_ID));
                break;
            case other: // what we do?
                break;
        }

        if (intent != null) {
            intent.putExtra("id", Integer.parseInt(remoteMessage.getData().get("id")));
            intent.setAction(Intent.ACTION_VIEW);
            PendingIntent pIntent = PendingIntent.getActivity(App.getContext(), 0, intent, 0);
            n.setContentIntent(pIntent);
            Log.d(TAG, "Notification intent updated");
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());
        notificationManager.notify(Integer.parseInt(remoteMessage.getData().get("id")), n.build());

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
