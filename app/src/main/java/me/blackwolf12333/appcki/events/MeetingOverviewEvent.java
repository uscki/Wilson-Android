package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.meeting.MeetingOverview;

/**
 * Created by peter on 2/14/16.
 */
public class MeetingOverviewEvent {
    public MeetingOverview overview;

    public MeetingOverviewEvent(MeetingOverview overview) {
        this.overview = overview;
    }
}
