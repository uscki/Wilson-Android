package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.error.Error;

/**
 * Created by peter on 7/22/16.
 */
public class ErrorEvent {
    public Error error;

    public ErrorEvent(Error error) {
        this.error = error;
    }
}
