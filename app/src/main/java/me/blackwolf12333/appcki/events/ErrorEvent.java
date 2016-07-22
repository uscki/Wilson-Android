package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.error.Error;

/**
 * Created by peter on 7/22/16.
 */
public class ErrorEvent {
    public Error error;

    public ErrorEvent(Error error) {
        this.error = error;
    }
}
