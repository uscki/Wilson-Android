package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.agenda.AgendaItem;

public class AgendaItemUpdatedEvent {

    private AgendaItem item;

    public AgendaItemUpdatedEvent(AgendaItem item) {
        this.item = item;
    }

    public AgendaItem getUpdatedItem() {
        return item;
    }
}
