package nl.uscki.appcki.android.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.activities.MeetingActivity;

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

        String title = remoteMessage.getData().get("title");
        String content = remoteMessage.getData().get("content");
        String type = remoteMessage.getData().get("type");

        // Because androids default BitmapFactory doesn't work with vector drawables
        Bitmap bm = Utils.getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_wilson);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        NotificationCompat.Builder n  = new NotificationCompat.Builder(getApplicationContext());
                n.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            n.setSmallIcon(R.drawable.ckilogo)
                    .setLargeIcon(bm);
        } else {
            n.setSmallIcon(R.drawable.ckilogo);
        }

        Intent intent = null;

        if(type != null)
        {
            switch (type) {
                case "meeting":
                    intent = new Intent(App.getContext(), MeetingActivity.class);
                    break;
                case "forum":
                    Intent forumIntent = new Intent(Intent.ACTION_VIEW);
                    forumIntent.setData(Uri.parse(String.format("https://www.uscki.nl/?pagina=Forum/ViewTopic&topicId=%d&newest=true#newest",
                            Integer.parseInt(remoteMessage.getData().get("id"))).trim()));
                    PendingIntent pIntent = PendingIntent.getActivity(App.getContext(), 0, forumIntent, 0);
                    n.setContentIntent(pIntent);
                    break;
            }

            if (intent != null) {
                intent.putExtra("id", Integer.parseInt(remoteMessage.getData().get("id")));
                intent.setAction(Intent.ACTION_VIEW);
                PendingIntent pIntent = PendingIntent.getActivity(App.getContext(), 0, intent, 0);
                n.setContentIntent(pIntent);
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());
        notificationManager.notify(0, n.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
