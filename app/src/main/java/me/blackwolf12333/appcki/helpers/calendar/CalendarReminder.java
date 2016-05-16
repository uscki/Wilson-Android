package me.blackwolf12333.appcki.helpers.calendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michielvanliempt on 11/01/15.
 */
public class CalendarReminder {
    public static final int NONE = 0;
    public static final int AT_TIME = 1;
    public static final int MINUTES_5 = 2;
    public static final int MINUTES_10 = 3;
    public static final int MINUTES_30 = 4;
    public static final int HOURS_1 = 5;
    public static final int HOURS_10 = 6;
    public static final int DAY = 7;
    public static final int CUSTOM = 8;
    public static final List<Integer> DEFAULTS = new ArrayList<>();
    static {
        DEFAULTS.add(-1);
        DEFAULTS.add(0);
        DEFAULTS.add(5);
        DEFAULTS.add(10);
        DEFAULTS.add(30);
        DEFAULTS.add(60);
        DEFAULTS.add(600);
        DEFAULTS.add(24*60);
    };

    private final int minutes;

    public CalendarReminder(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSpinnerPosition() {
        return getSpinnerPosition(minutes);
    }
    
    public static int getSpinnerPosition(int minutes) {
        int position = DEFAULTS.indexOf(minutes);
        if (position == -1)
            return DEFAULTS.size();  // not in array, means custom
        return position;
    }

    public boolean isCustom() {
        return !DEFAULTS.contains(minutes);
    }
}
