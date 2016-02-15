package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.ServerError;

/**
 * Created by peter on 2/14/16.
 */
public class ServerErrorEvent {
    public ServerError error;

    public ServerErrorEvent(ServerError error) {
        this.error = error;
    }
}
