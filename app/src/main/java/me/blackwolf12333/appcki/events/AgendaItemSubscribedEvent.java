package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.agenda.Subscribers;

/**
 * Created by peter on 2/1/16.
 */
public class AgendaItemSubscribedEvent {
    public Subscribers subscribed;

    public AgendaItemSubscribedEvent(Subscribers subscribed) {
        this.subscribed = subscribed;
    }
}
