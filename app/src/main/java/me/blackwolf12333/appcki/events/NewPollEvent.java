package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.api.PollAPI;

/**
 * Created by peter on 12/30/15.
 */
public class NewPollEvent {
    public PollAPI.Poll poll;

    public NewPollEvent(PollAPI.Poll poll) {
        this.poll = poll;
    }
}
