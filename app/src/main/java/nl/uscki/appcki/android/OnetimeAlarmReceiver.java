package nl.uscki.appcki.android;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.gson.Gson;

import nl.uscki.appcki.android.generated.agenda.AgendaItem;

/**
 * Created by peter on 1/18/17.
 */

public class OnetimeAlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent originalIntent) {
        // prepare intent which is triggered if the
        // notification is selected

        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.putExtra("item", originalIntent.getStringExtra("item"));
        intent2.setAction(Intent.ACTION_VIEW);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent2, 0);

        Gson gson = new Gson();
        AgendaItem item = gson.fromJson(originalIntent.getStringExtra("item"), AgendaItem.class);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new NotificationCompat.Builder(context)
                .setContentTitle(item.getTitle())
                .setContentText(item.getWhat())
                .setSmallIcon(R.drawable.ckilogo)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, n);
    }
}
