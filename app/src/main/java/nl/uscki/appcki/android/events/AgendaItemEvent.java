package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.agenda.AgendaItem;

/**
 * Created by peter on 2/1/16.
 */
public class AgendaItemEvent {
    public AgendaItem agendaItem;

    public AgendaItemEvent(AgendaItem agendaItem) {
        this.agendaItem = agendaItem;
    }
}
