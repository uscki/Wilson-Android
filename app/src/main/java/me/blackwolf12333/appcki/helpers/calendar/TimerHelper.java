package me.blackwolf12333.appcki.helpers.calendar;

import android.app.AlarmManager;
import android.content.Context;

import me.blackwolf12333.appcki.App;

/**
 * Created by michielvanliempt on 19/01/15.
 */
public class TimerHelper {

    public static final int FIVE_MINUTES = 5 * 60 * 1000;
    private static final String SCHEME = "pku-alarm";
    private static TimerHelper instance;
    private final AlarmManager am;
    private final Context context;


    ///////////////////////////////////////////////////////////////////////////
    // timer methods
    ///////////////////////////////////////////////////////////////////////////

    public TimerHelper() {
        context = App.getContext();
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /*public static Uri makeUri(RegimeReminder reminder) {
        return new Uri.Builder().scheme(SCHEME)
                                .path(Long.toString(reminder.getSubstituteRegime()))
                                .appendPath(Long.toString(reminder.getId()))
                                .build();
    }

    public static Uri makeBloodUri() {
        return new Uri.Builder().scheme(SCHEME)
                                .path("blood")
                                .build();
    }

    public static Uri makeIntUri(int id) {
        return new Uri.Builder().scheme(SCHEME)
                                .path(Integer.toString(id))
                                .build();
    }
*/
    ///////////////////////////////////////////////////////////////////////////
    // methods to create intents for the alarmmanager
    ///////////////////////////////////////////////////////////////////////////

    public static TimerHelper getInstance() {
        if (instance == null) {
            instance = new TimerHelper();
        }
        return instance;
    }

    /**
     * the android AlarmManager has some serious limitations:
     * - you can't query active alarms
     * - you can't easily cancel a batch of alarms
     * - time is defined in millis where days and hours would be better
     * - doesn't work with daylight saving time
     * <p/>
     * the strategy to work with this is to schedule single events for the next occurence of the specified time (ignoring the weekday),
     * the BroadcastReceiver is responsible for checking whether this day is enabled for the reminder
     * and for rescheduling the alarm next day
     *
     * @param reminder
     */
    /*public void scheduleReminder(RegimeReminder reminder) {
        String substituteName = reminder.getSubstituteName();
        DateTime now = DateTime.now();
        DateTime schedule = now.withTime(reminder.getHours(), reminder.getMinutes(), 0, 0);
        if (schedule.isBefore(now)) {
            schedule = schedule.plusDays(1);
        }

        am.set(AlarmManager.RTC, schedule.getMillis(), getPendingIntent(makeSubstituteIntent(reminder, substituteName)));
    }

    public void removeReminder(RegimeReminder reminder) {
        PendingIntent pendingIntent = getPendingIntent(makeSubstituteIntent(reminder, null));
        am.cancel(pendingIntent);
    }

    public void scheduleBloodReminder(DateTime date) {
        Intent alarmReceiver = makeIntent(makeBloodUri(), AlaramReceiver.NotificationType.BLOOD_LEVEL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        if (Build.VERSION.SDK_INT < 19) {
            am.set(AlarmManager.RTC, date.getMillis(), pendingIntent);
        } else {
            am.setWindow(AlarmManager.RTC, date.getMillis(), FIVE_MINUTES, pendingIntent);
        }
    }

    public void removeBloodReminder() {
        PendingIntent pendingIntent = getPendingIntent(makeIntent(makeBloodUri(), AlaramReceiver.NotificationType.BLOOD_LEVEL));
        am.cancel(pendingIntent);
    }

    public PendingIntent getPendingIntent(Intent intent) {
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Intent makeSubstituteIntent(RegimeReminder reminder, String substitute) {
        Uri uri = makeUri(reminder);
        Intent intent = makeIntent(uri, AlaramReceiver.NotificationType.TAKE_SUBSTITUTE);
        if (substitute != null) {
            intent.putExtra(AlaramReceiver.SUBSTITUTE_NAME, substitute);
        }
        return intent;
    }

    public Intent makeMessageIntent(int id, String message) {
        Uri uri = makeIntUri(id);
        Intent intent = makeIntent(uri, AlaramReceiver.NotificationType.PKU_COACH_MESSAGE);
        intent.putExtra(AlaramReceiver.MESSAGE, message);
        intent.putExtra(AlaramReceiver.MESSAGE_ID, id);
        return intent;
    }

    public Intent makeIntent(Uri uri, AlaramReceiver.NotificationType type) {
        Intent intent = new Intent(context, AlaramReceiver.class);
        intent.putExtra(AlaramReceiver.ALARM_TYPE, type.name());
        intent.setData(uri);
        return intent;
    }

    public void rescheduleBloodReminder(Date reminderDate, byte recurrenceIndex) {
        if (reminderDate != null && recurrenceIndex > 0) {
            ReminderPeriod period = ReminderPeriod.values()[recurrenceIndex];
            DateTime dateTime = new DateTime(reminderDate);
            DateTime now = DateTime.now();

            while (dateTime.isBefore(now)) {
                dateTime = period.apply(dateTime);
            }
            scheduleBloodReminder(dateTime);
        }
    }

    public void scheduleMessage(String message, int messageId) {
        DateTime now = DateTime.now();
        DateTime schedule = now.plusDays(3);
//        DateTime schedule = now.plusSeconds(3);

        am.set(AlarmManager.RTC, schedule.getMillis(), getPendingIntent(makeMessageIntent(messageId, message)));
    }

    public void unscheduleMessage(int messageId) {
        am.cancel(getPendingIntent(makeMessageIntent(messageId, null)));
    }*/
}
