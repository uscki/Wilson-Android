package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.Poll;

/**
 * Created by peter on 12/30/15.
 */
public class NewPollEvent {
    public Poll poll;

    public NewPollEvent(Poll poll) {
        this.poll = poll;
    }
}
