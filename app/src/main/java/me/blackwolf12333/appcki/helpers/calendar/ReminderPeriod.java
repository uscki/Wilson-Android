package me.blackwolf12333.appcki.helpers.calendar;

import org.joda.time.DateTime;

/**
 * Created by michielvanliempt on 18/01/15.
 */
public enum ReminderPeriod {
    None{
        @Override
        public DateTime apply(DateTime date) {
            return date;
        }
    },
    Daily{
        @Override
        public DateTime apply(DateTime date) {
            return date.plusDays(1);
        }
    },
    Weekly{
        @Override
        public DateTime apply(DateTime date) {
            return date.plusWeeks(1);
        }
    },
    BiWeekly {
        @Override
        public DateTime apply(DateTime date) {
            return date.plusWeeks(2);
        }
    },
    Monthly{
        @Override
        public DateTime apply(DateTime date) {
            return date.plusMonths(1);
        }
    },
    Yearly {
        @Override
        public DateTime apply(DateTime date) {
            return date.plusYears(1);
        }
    },

    ;

    public abstract DateTime apply(DateTime date);
}