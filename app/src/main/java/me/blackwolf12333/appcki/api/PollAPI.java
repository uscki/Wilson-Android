package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.PollEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
import me.blackwolf12333.appcki.generated.Poll;
import me.blackwolf12333.appcki.generated.ServerError;

/**
 * Created by peter on 12/18/15.
 */
public class PollAPI {
    User user;
    Gson gson;

    public PollAPI() {
        this.user = UserHelper.getInstance().getUser();
        this.gson = new Gson();
        EventBus.getDefault().register(this);
    }

    public void getPoll(int id) {
        EventBus.getDefault().post(new ShowProgressEvent(true));
        new APICall(user, "poll/get").execute("id="+id);
    }

    public void getActivePoll() {
        EventBus.getDefault().post(new ShowProgressEvent(true));
        new APICall(user, "poll/active").execute();
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
        Poll poll = gson.fromJson(event.json, Poll.class);
        if(poll != null) {
            EventBus.getDefault().post(new PollEvent(poll));
        }
    }

    public void vote(int id) {
        new APICall(user, "poll/vote").execute("id=" + id);
    }
}
