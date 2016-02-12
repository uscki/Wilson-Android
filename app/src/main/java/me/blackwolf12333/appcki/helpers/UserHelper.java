package me.blackwolf12333.appcki.helpers;

import android.content.SharedPreferences;
import android.util.Log;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.UserLoggedInEvent;
import me.blackwolf12333.appcki.generated.Person;

/**
 * Created by peter on 1/31/16.
 */
public class UserHelper {
    private static UserHelper singleton;
    private User user;

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

    public void setUser(User user) {
        this.user = user;
    }

    public void updateUser(String token, Person person) {
        Log.i("updateUser", token);
        Log.i("updateUser", person.toString());
        this.user = new User(token, person);
    }

    public void login(String token, Person person) {
        this.user = new User(token, person);
        this.user.loggedIn = true;
        EventBus.getDefault().post(new UserLoggedInEvent());
    }

    public void logout(SharedPreferences preferences) {
        if(preferences.contains("TOKEN")) {
            System.out.println("token in place, removing it");
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("TOKEN");
            editor.commit();
        }
        this.user.loggedIn = false;
        this.user.TOKEN = null;
        this.user.setPerson(null);
    }

    public boolean isLoggedIn() {
        return user.loggedIn;
    }

    public void save(SharedPreferences preferences) {
        if(!preferences.contains("TOKEN")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("TOKEN", user.TOKEN);
            editor.commit();
        }
    }
}
