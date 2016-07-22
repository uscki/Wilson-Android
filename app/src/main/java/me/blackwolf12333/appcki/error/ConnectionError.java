package me.blackwolf12333.appcki.error;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.events.ErrorEvent;

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
