package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import me.blackwolf12333.appcki.User;

/**
 * Created by peter on 12/9/15.
 */
public class Person {

    User user;

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

    public Person(User user) {
        this.user = user;

    }

    public Person getPerson(int id) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(new APICall(user, "person/get").execute("id="+id).get(), Person.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
