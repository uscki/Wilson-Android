package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.poll.Poll;

/**
 * Created by peter on 12/30/15.
 */
public class PollEvent {
    public Poll poll;

    public PollEvent(Poll poll) {
        this.poll = poll;
    }
}
