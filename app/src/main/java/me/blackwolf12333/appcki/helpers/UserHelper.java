package me.blackwolf12333.appcki.helpers;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.generated.organisation.Person;

/**
 * Created by peter on 1/31/16.
 */
public class UserHelper {
    private static UserHelper singleton;
    private User user;
    private SharedPreferences preferences;

    private UserHelper() {
        user = new User(null, null);
    }

    public static synchronized UserHelper getInstance( ) {
        if (singleton == null)
            singleton=new UserHelper();
        return singleton;
    }

    public User getUser() {
        return user;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void login(String token, Person person) {
        this.user = new User(token, person);
        this.user.loggedIn = true;
    }

    public void logout() {
        if(preferences.contains("TOKEN")) {
            Log.d("UserHelper", "token in place, removing it");
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("TOKEN");
            editor.apply();
        }
        this.user.loggedIn = false;
        this.user.TOKEN = null;
        this.user.setPerson(null);
    }

    public boolean isLoggedIn() {
        return user.loggedIn;
    }

    public void save() {
        if(!preferences.contains("TOKEN")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("TOKEN", user.TOKEN);
            editor.apply();
        }
    }

    public void load() {
        if(preferences.contains("TOKEN")) {
            Gson gson = new Gson();
            String token = preferences.getString("TOKEN", "null");
            try {
                if (!token.equals("null")) {
                    Person person = gson.fromJson(new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), "UTF-8"), Person.class);
                    login(token, person);
                } else {
                    user.loggedIn = false;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }
}
