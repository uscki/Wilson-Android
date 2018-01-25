package nl.uscki.appcki.android.events;

/**
 * Created by peter on 2/3/16.
 */
public class UserLoggedInEvent {

    public boolean loggedIn;

    public UserLoggedInEvent(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
