package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.agenda.Subscribers;

/**
 * Created by peter on 6/29/16.
 */
public class AgendaSubscribersEvent {
    public Subscribers subscribers;

    public AgendaSubscribersEvent(Subscribers subscribers) {
        this.subscribers = subscribers;
    }
}
