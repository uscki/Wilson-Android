package nl.uscki.appcki.android.helpers;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import nl.uscki.appcki.android.generated.organisation.Person;

/**
 * Created by peter on 1/31/16.
 */
public class UserHelper {
    private static UserHelper singleton;
    public String TOKEN;
    private Person person;
    private boolean loggedIn;
    private SharedPreferences preferences;

    private UserHelper() {
        this.TOKEN = null;
        this.person = null;
        this.loggedIn = false;
    }

    public static synchronized UserHelper getInstance( ) {
        if (singleton == null)
            singleton=new UserHelper();
        return singleton;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void login(String token, Person person) {
        this.TOKEN = token;
        this.person = person;
        this.loggedIn = true;
    }

    public void logout() {
        if(preferences.contains("TOKEN")) {
            Log.d("UserHelper", "token in place, removing it");
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("TOKEN");
            editor.apply();
        }
        this.loggedIn = false;
        this.TOKEN = null;
        this.setPerson(null);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void save() {
        if(!preferences.contains("TOKEN")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("TOKEN", TOKEN);
            editor.apply();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("TOKEN");
            editor.putString("TOKEN", TOKEN);
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
                    loggedIn = false;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            this.person = null;
            this.TOKEN = null;
            this.loggedIn = false;
        }
    }
}
