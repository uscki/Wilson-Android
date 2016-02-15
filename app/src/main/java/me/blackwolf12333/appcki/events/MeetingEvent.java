package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.meeting.Meeting;

/**
 * Created by peter on 2/14/16.
 */
public class MeetingEvent {
    public Meeting meeting;

    public MeetingEvent(Meeting response) {
        this.meeting = response;
    }
}
