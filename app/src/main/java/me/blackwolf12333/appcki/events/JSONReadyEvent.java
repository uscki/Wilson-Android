package me.blackwolf12333.appcki.events;

import com.google.gson.JsonElement;

/**
 * Created by peter on 12/30/15.
 */
public class JSONReadyEvent {
    public JsonElement json;

    public JSONReadyEvent(JsonElement jsonElement) {
        this.json = jsonElement;
    }
}
