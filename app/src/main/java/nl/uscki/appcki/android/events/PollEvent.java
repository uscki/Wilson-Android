package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.poll.Poll;

/**
 * Created by peter on 12/30/15.
 */
public class PollEvent {
    public Poll poll;

    public PollEvent(Poll poll) {
        this.poll = poll;
    }
}
