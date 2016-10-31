package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.poll.Poll;

/**
 * Created by peter on 2/7/16.
 */
public class PollVoteEvent {
    public Poll poll;

    public PollVoteEvent(Poll poll) {
        this.poll = poll;
    }
}
