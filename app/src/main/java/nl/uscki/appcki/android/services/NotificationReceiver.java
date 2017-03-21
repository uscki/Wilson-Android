package nl.uscki.appcki.android.services;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;

/**
 * Created by peter on 3/21/17.
 */

public class NotificationReceiver extends FirebaseMessagingService {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Notification n  = new NotificationCompat.Builder(App.getContext())
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("content"))
                .setSmallIcon(R.drawable.ckilogo)
                //.setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());
        notificationManager.notify(0, n);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
