package me.blackwolf12333.appcki.events;

import com.google.gson.JsonElement;

/**
 * Created by peter on 12/30/15.
 */
public class JSONReadyEvent {
    public JsonElement json;
    public String call;

    public JSONReadyEvent(JsonElement jsonElement, String call) {
        this.json = jsonElement;
        this.call = call;
    }
}
