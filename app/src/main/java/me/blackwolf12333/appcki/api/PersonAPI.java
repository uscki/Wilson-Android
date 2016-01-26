package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewPersonEvent;
import me.blackwolf12333.appcki.generated.Person;

/**
 * Created by peter on 12/9/15.
 */
public class PersonAPI {

    User user;
    Gson gson = new Gson();

    public PersonAPI(User user) {
        this.user = user;
        EventBus.getDefault().register(this);
    }

    public void getPerson(int id) {
        new APICall(user, "person/get").execute("id="+id);
    }

    public void onEvent(JSONReadyEvent event) {
        Person person = gson.fromJson(event.json, Person.class);
        if(person != null) {
            EventBus.getDefault().post(new NewPersonEvent(person));
        }
    }
}
