package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;

/**
 * Created by peter on 1/25/16.
 */
public abstract class APIClass {
    protected User user;
    protected Gson gson;

    public APIClass(User user) {
        this.user = user;
        this.gson = new Gson();
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(JSONReadyEvent event) {
        jsonReadyHandler(event);
    }

    public abstract void jsonReadyHandler(JSONReadyEvent event);
}
