package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

/**
 * Created by peter on 2/1/16.
 */
public class AgendaItemEvent {
    public AgendaItem agendaItem;

    public AgendaItemEvent(AgendaItem agendaItem) {
        this.agendaItem = agendaItem;
    }
}
