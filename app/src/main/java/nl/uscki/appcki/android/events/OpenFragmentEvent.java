package nl.uscki.appcki.android.events;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by peter on 2/1/16.
 */
public class OpenFragmentEvent {
    public Fragment screen;
    public Bundle arguments;

    public OpenFragmentEvent(Fragment screen) {
        this.screen = screen;
        this.arguments = null;
    }

    public OpenFragmentEvent(Fragment screen, Bundle arguments) {
        this.screen = screen;
        this.arguments = arguments;
    }
}
