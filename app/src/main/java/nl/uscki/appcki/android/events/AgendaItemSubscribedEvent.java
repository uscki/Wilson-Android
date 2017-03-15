package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;

/**
 * Created by peter on 2/1/16.
 */
public class AgendaItemSubscribedEvent {
    public AgendaParticipantLists subscribed;
    public boolean showSubscribe;

    public AgendaItemSubscribedEvent(AgendaParticipantLists subscribed, boolean showSubscribe) {
        this.subscribed = subscribed;
        this.showSubscribe = showSubscribe;
    }
}
