package me.blackwolf12333.appcki.events;

/**
 * Created by peter on 2/1/16.
 */
public class AgendaItemSubscribedEvent {
    public boolean subscribed;

    public AgendaItemSubscribedEvent(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
