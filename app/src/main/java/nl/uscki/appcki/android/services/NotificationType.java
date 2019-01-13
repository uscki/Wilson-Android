package nl.uscki.appcki.android.services;

/*
 * B.A.D.W.O.L.F.
 * AppCKI der USCKI Incognito
 *
 * Copyright (c) 2017 USCKI Incognito. All rights reserved.
 *
 */

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.helpers.notification.EmptyNotification;
import nl.uscki.appcki.android.helpers.notification.achievement.AchievementNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaAnnouncementNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaFromBackupNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaNewNotification;
import nl.uscki.appcki.android.helpers.notification.agenda.AgendaReplyNotification;
import nl.uscki.appcki.android.helpers.notification.bugtracker.BugTrackerNotification;
import nl.uscki.appcki.android.helpers.notification.forum.ForumNewTopicNotification;
import nl.uscki.appcki.android.helpers.notification.forum.ForumReplyNotification;
import nl.uscki.appcki.android.helpers.notification.meeting.MeetingFilledInNotification;
import nl.uscki.appcki.android.helpers.notification.meeting.MeetingNewNotification;
import nl.uscki.appcki.android.helpers.notification.meeting.MeetingPlannedNotification;
import nl.uscki.appcki.android.helpers.notification.news.NewsNotification;

public enum NotificationType {
    achievement(
            AchievementNotification.class,
            null,
            CompatNotificationChannel.PERSONAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_PROMO),
    agenda_announcement(
            AgendaAnnouncementNotification.class,
            MainActivity.ACTION_AGENDA_OVERVIEW,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_LOW,
            NotificationCompat.CATEGORY_EVENT),
    agenda_from_backup(
            AgendaFromBackupNotification.class,
            MainActivity.ACTION_AGENDA_OVERVIEW,
            CompatNotificationChannel.PERSONAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_EVENT),
    agenda_new(
            AgendaNewNotification.class,
            MainActivity.ACTION_AGENDA_OVERVIEW,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_EVENT),
    agenda_reply(
            AgendaReplyNotification.class,
            MainActivity.ACTION_AGENDA_OVERVIEW,
            CompatNotificationChannel.PERSONAL,
            NotificationManagerCompat.IMPORTANCE_LOW,
            NotificationCompat.CATEGORY_SOCIAL),
    bugtracker_comment(
            BugTrackerNotification.class,
            null,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_STATUS),
    bugtracker_new(
            BugTrackerNotification.class,
            null,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_SERVICE),
    bugtracker_status_changed(
            BugTrackerNotification.class,
            null,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_STATUS),
    forum_new_topic(
            ForumNewTopicNotification.class,
            null,
            CompatNotificationChannel.PERSONAL,
            NotificationManagerCompat.IMPORTANCE_LOW,
            NotificationCompat.CATEGORY_SOCIAL),
    forum_reply(
            ForumReplyNotification.class,
            null,
            CompatNotificationChannel.PERSONAL,
            NotificationManagerCompat.IMPORTANCE_LOW,
            NotificationCompat.CATEGORY_SOCIAL),
    meeting_filledin(
            MeetingFilledInNotification.class,
            MainActivity.ACTION_MEETING_OVERVIEW,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_STATUS),
    meeting_new(
            MeetingNewNotification.class,
            MainActivity.ACTION_MEETING_OVERVIEW,
            CompatNotificationChannel.INTERACTIVE,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_EVENT),
    meeting_planned(
            MeetingPlannedNotification.class,
            MainActivity.ACTION_MEETING_OVERVIEW,
            CompatNotificationChannel.INTERACTIVE,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_EVENT),
    news(
            NewsNotification.class,
            null,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_RECOMMENDATION),
    other(
            EmptyNotification.class,
            MainActivity.ACTION_NEWS_OVERVIEW,
            CompatNotificationChannel.GENERAL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            NotificationCompat.CATEGORY_SERVICE);

    private Class<? extends BadWolfNotification> c;
    private String backstackAction;
    private CompatNotificationChannel channel;
    private int importance;
    private String category;

    /**
     * Construct a new ENUM item in the NotificationType ENUM. Enum item names should correspond
     * one-to-one with the value "type" sent by the Zebra / B.A.D.W.O.L.F. Notification system and
     * every possible notification should be present.
     *
     * @param c                 The BadWolfNotification subclass to instantiate for this notification
     * @param backstackAction   The action from the Main Activity which, when passed to an intent,
     *                          shows a fragment or screen.
     * @param channel           For older versions of android, the channel type to show this type
     *                          of notification on
     * @param importance        A notification importance, used by modern versions of the Android OS
     *                          to determine if it is important enough to show to the user at that
     *                          time. Should be one of the options on
     *                          NotificationManagerCompat.IMPORTANCE_X
     * @param category          The notification category for this notification. Should be one of
     *                          the options on NotificationCompat.CATEGORY_X
     */
    NotificationType(
            Class<? extends BadWolfNotification> c,
            String backstackAction,
            CompatNotificationChannel channel,
            int importance,
            String category) {
        this.c = c;
        this.backstackAction = backstackAction;
        this.channel = channel;
        this.importance = importance;
        this.category = category;
    }

    public Class<? extends BadWolfNotification> getC() {
        return this.c;
    }

    public String getBackstackAction() {
        return this.backstackAction;
    }

    public CompatNotificationChannel getChannel() {
        return this.channel;
    }

    public int getImportance() {
        return this.importance;
    }

    public String getCategory() {
        return this.category;
    }
}