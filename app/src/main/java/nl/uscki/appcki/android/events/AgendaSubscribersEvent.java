package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.agenda.Subscribers;

/**
 * Created by peter on 6/29/16.
 */
public class AgendaSubscribersEvent {
    public Subscribers subscribers;

    public AgendaSubscribersEvent(Subscribers subscribers) {
        this.subscribers = subscribers;
    }
}
