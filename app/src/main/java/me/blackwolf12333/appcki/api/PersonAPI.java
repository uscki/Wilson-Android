package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewPersonEvent;

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

    public class Person {
        @SerializedName("id")
        int id;

        @SerializedName("username")
        String userName;

        @SerializedName("expires")
        long expires;

        @SerializedName("firstname")
        String firstName;

        @SerializedName("middlename")
        String middleName;

        @SerializedName("lastname")
        String lastName;

        @SerializedName("firstletters")
        String firstLetters;

        @SerializedName("address2")
        String address;

        @SerializedName("gender")
        Object gender;

        @SerializedName("signature")
        String signature;

        @SerializedName("displayonline")
        boolean displayOnline;

        @SerializedName("nickname")
        String nickname;

        @SerializedName("photomediaid")
        int photoMediaID;

        @SerializedName("fbname")
        String facebookName;

        @SerializedName("roles")
        String[] roles;
    }
}
