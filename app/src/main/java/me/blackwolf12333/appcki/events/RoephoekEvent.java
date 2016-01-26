package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.api.RoephoekAPI;

/**
 * Created by peter on 1/25/16.
 */
public class RoephoekEvent {
    public RoephoekAPI.Roephoek roephoek;

    public RoephoekEvent(RoephoekAPI.Roephoek roephoek) {
        this.roephoek = roephoek;
    }
}
