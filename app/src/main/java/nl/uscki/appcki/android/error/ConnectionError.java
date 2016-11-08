package nl.uscki.appcki.android.error;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.events.ErrorEvent;

/**
 * Created by peter on 7/22/16.
 */
public class ConnectionError extends Error {
    private Throwable throwable;

    public ConnectionError(Throwable throwable) {
        this.throwable = throwable;
        message = App.getContext().getString(R.string.connection_error);
        EventBus.getDefault().post(new ErrorEvent(this));
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
