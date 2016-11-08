package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.meeting.MeetingOverview;

/**
 * Created by peter on 2/14/16.
 */
public class MeetingOverviewEvent {
    public MeetingOverview overview;

    public MeetingOverviewEvent(MeetingOverview overview) {
        this.overview = overview;
    }
}
