package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewPersonEvent;
import me.blackwolf12333.appcki.generated.Person;
import me.blackwolf12333.appcki.generated.ServerError;

/**
 * Created by peter on 12/9/15.
 */
public class PersonAPI {
    User user;
    Gson gson = new Gson();

    public PersonAPI() {
        this.user = UserHelper.getInstance().getUser();
        this.gson = new Gson();
        EventBus.getDefault().register(this);
    }

    public void getPerson(int id) {
        new APICall(user, "person/get").execute("id="+id);
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
            case "person/get":
                Person person = gson.fromJson(event.json, Person.class);
                if(person != null) {
                    EventBus.getDefault().post(new NewPersonEvent(person));
                }
                break;
        }

    }
}
