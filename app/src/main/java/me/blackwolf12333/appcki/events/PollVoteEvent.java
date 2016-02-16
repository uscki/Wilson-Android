package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.poll.Poll;

/**
 * Created by peter on 2/7/16.
 */
public class PollVoteEvent {
    public Poll poll;

    public PollVoteEvent(Poll poll) {
        this.poll = poll;
    }
}
