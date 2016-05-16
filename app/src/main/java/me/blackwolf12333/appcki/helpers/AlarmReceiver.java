package me.blackwolf12333.appcki.helpers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;

import me.blackwolf12333.appcki.MainActivity;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.helpers.calendar.ReminderPeriod;

/**
 * Created by michielvanliempt on 30/12/14.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String ALARM_TYPE = "alarm-type";
    public static final String SUBSTITUTE_NAME = "substitute-name";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_ID = "message-id";

    private static final int BLOOD_NOTIFICATION_ID = 1;
    private static final int SUBSTITUTE_NOTIFICATION_ID = 2;
    private static final int SUBSTITUTE_NOTIFICATION_ID_SHIFT = 2;

    public static int makeSubstituteId(Uri data) {
        int id = Integer.valueOf(data.getLastPathSegment());
        return SUBSTITUTE_NOTIFICATION_ID | (id << SUBSTITUTE_NOTIFICATION_ID_SHIFT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarm", "recieved!");

        if (!intent.hasExtra(ALARM_TYPE)) {
            Log.w("alarm", "intent didn't have type set");
            return;
        }

        String type = intent.getStringExtra(ALARM_TYPE);
        if (NotificationType.BLOOD_LEVEL.name().equals(type)) {
            createNotification(context, BLOOD_NOTIFICATION_ID, NotificationType.BLOOD_LEVEL.name(),
                               R.string.app_name, 0);
            Byte reminderRecurrence = 0;//UserHelper.getInstance().getCurrentUser().getBloodLevelReminderRecurrence();
            if (reminderRecurrence != null && reminderRecurrence > 0) {
                ReminderPeriod period = ReminderPeriod.values()[reminderRecurrence];
                DateTime nextTime = period.apply(DateTime.now().withTime(12, 0, 0, 0));
                //TimerHelper.getInstance().scheduleBloodReminder(nextTime);
            }
        } else if (NotificationType.TAKE_SUBSTITUTE.name().equals(type)) {
            handleReminderIntent(context, intent);
        } else {
            Log.w("alarm", "intent didn't have valid type set");
        }

    }

    public void handleReminderIntent(Context context, Intent intent) {
        Uri data = intent.getData();
        Log.i("alarm", "received alarm " + data);

        int reminderId = Integer.parseInt(data.getLastPathSegment());
        /*RegimeReminderHelper helper = RegimeReminderHelper.getInstance();
        RegimeReminder reminder = helper.get(reminderId);
        if (reminder == null) {
            // reminder deleted by user?
            // do nothing, we won't get the same intent
            return;
        }

        // reschedule the alarm for tomorrow
        //DateTime nextTime = DateTime.now().plusDays(1).withTime(reminder.getHours(), reminder.getMinutes(), 0, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //am.set(AlarmManager.RTC, nextTime.getMillis(), PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));

        if (RegimeReminderHelper.isEnabledToday(reminder)) {
            String substituteName = intent.getStringExtra(SUBSTITUTE_NAME);
            createNotification(context, makeSubstituteId(data), NotificationType.TAKE_SUBSTITUTE.name(),
                               R.string.notification_title_take_sub, substituteName);
        }*/
    }

    private void createNotification(Context context, int id, String typeName, int titleResId, int textResId) {
        String message = context.getString(textResId);
        createNotification(context, id, typeName, titleResId, message);
    }

    private void createNotification(Context context, int id, String typeName, int titleResId, String message) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(ALARM_TYPE, typeName);
        notificationIntent.putExtra(MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(context.getString(titleResId))
                .setContentText(message)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 500, 500});
        notificationManager.notify(id, mNotifyBuilder.build());
    }

    protected void checkActivityForeground(Context context, final Runnable inForeground, final Runnable inBackground) {
        Log.d("alarm", "start checking for Activity in foreground");
        Intent intent = new Intent();
        //intent.setAction(MainActivity.ACTION_ACTIVITY_DETECTOR);
        context.sendOrderedBroadcast(intent, null, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int result = getResultCode();

                if (result != Activity.RESULT_CANCELED) { // Activity caught it
                    Log.d("alarm", "An activity caught the broadcast, result " + result);
                    inForeground.run();
                    return;
                }
                Log.d("alarm", "No activity did catch the broadcast.");
                inBackground.run();
            }
        }, null, Activity.RESULT_CANCELED, null, null);
    }

    public enum NotificationType {
        BLOOD_LEVEL,
        TAKE_SUBSTITUTE,
    }
}
