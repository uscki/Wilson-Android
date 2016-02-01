package me.blackwolf12333.appcki;

import android.content.SharedPreferences;
import android.util.Log;

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
    }

    public void logout() {
        this.user.loggedIn = false;
        this.user.TOKEN = null;
        this.user.person = null;
    }

    public boolean isLoggedIn() {
        return user.loggedIn;
    }

    public void save(SharedPreferences preferences) {
        if(!preferences.contains("TOKEN")) {
            System.out.println("no token in place");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("TOKEN", user.TOKEN);
            editor.commit();
        }
    }
}
