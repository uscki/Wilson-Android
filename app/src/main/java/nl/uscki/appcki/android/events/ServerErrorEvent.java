package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.ServerError;

/**
 * Created by peter on 2/14/16.
 */
public class ServerErrorEvent {
    public ServerError error;

    public ServerErrorEvent(ServerError error) {
        this.error = error;
    }
}
