package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.meeting.Meeting;

/**
 * Created by peter on 2/14/16.
 */
public class MeetingEvent {
    public Meeting meeting;

    public MeetingEvent(Meeting response) {
        this.meeting = response;
    }
}
