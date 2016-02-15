package me.blackwolf12333.appcki.events;

import com.google.gson.JsonElement;

import me.blackwolf12333.appcki.api.common.APIError;

/**
 * Created by peter on 12/30/15.
 */
public class JSONReadyEvent {
    public JsonElement json;
    public String call;
    public APIError error;

    public JSONReadyEvent(APIError error, JsonElement jsonElement, String call) {
        this.error = error;
        this.json = jsonElement;
        this.call = call;
    }
}
