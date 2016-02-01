package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.RoephoekEvent;
import me.blackwolf12333.appcki.generated.Roephoek;
import me.blackwolf12333.appcki.generated.ServerError;

/**
 * Created by peter on 1/25/16.
 */
public class RoephoekAPI {
    User user;
    Gson gson;

    public RoephoekAPI() {
        this.user = UserHelper.getInstance().getUser();
        this.gson = new Gson();
        EventBus.getDefault().register(this);
    }

    public void getRoephoek() {
        new APICall(user, "shoutbox/older").execute();
    }

    public void roep(String nickname, String message) {
        new APICall(user, "shoutbox/shout").execute("nickname=" + nickname, "message=" + message);
    }

    public void onEventMainThread(JSONReadyEvent event) {
        ServerError error = gson.fromJson(event.json, ServerError.class);

        // status is nooit null bij een server error, dus hiermee kunnen we checken of dit het goede
        // object was voor deserialization.
        if(error.getStatus() != null) {
            Log.e("APIClass", "error getting data from BadWolf with call: " + event.call + "\nserver error: " + error.toString());
            //TODO handle errors
        } else {
            if(event.call.startsWith("poll")) {
                jsonReadyHandler(event);
            }
        }
    }

    public void jsonReadyHandler(JSONReadyEvent event) {
        switch (event.call) {
            case "shoutbox/older":
                Roephoek roephoek = gson.fromJson(event.json, Roephoek.class);
                EventBus.getDefault().post(new RoephoekEvent(roephoek));
                break;
            case "shoutbox/shout":
                System.out.println(event.json);
        }
    }
}
