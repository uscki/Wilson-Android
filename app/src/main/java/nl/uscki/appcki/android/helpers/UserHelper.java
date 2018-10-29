package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.organisation.PersonSimple;
import nl.uscki.appcki.android.services.NotificationReceiver;
import nl.uscki.appcki.android.generated.shop.Product;

/**
 * Created by peter on 1/31/16.
 */
public class UserHelper {
    private static UserHelper singleton;
    private String TOKEN;
    private PersonSimple person;
    private boolean loggedIn;
    private SharedPreferences preferences;
    private Map<Integer, Integer> preferedProducts = new HashMap<>();

    public String getToken() {
        return this.TOKEN;
    }

    private UserHelper() {
        this.TOKEN = null;
        this.person = null;
        this.loggedIn = false;
        preferences = App.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    }

    public static synchronized UserHelper getInstance( ) {
        if (singleton == null)
            singleton = new UserHelper();
        return singleton;
    }

    public void addPreferedProduct(Product p) {
        if (this.preferedProducts.containsKey(p.id)) {
            Integer count = this.preferedProducts.get(p.id);
            this.preferedProducts.remove(p.id);
            this.preferedProducts.put(p.id, ++count);
        } else {
            this.preferedProducts.put(p.id, 1);
        }
    }

    public int isPreferedProduct(Product p, Product competitor) {
       /* if (this.preferedProducts.containsKey(p.id) && this.preferedProducts.containsKey(competitor.id)) {
            Log.e("UserHelper", "found 2 competitors");
            int productCount = this.preferedProducts.get(p.id);
            int competitorCount = this.preferedProducts.get(competitor.id);

            return Integer.compare(productCount, competitorCount);
        } else if (this.preferedProducts.containsKey(p.id) && !this.preferedProducts.containsKey(competitor.id)) {
            Log.e("UserHelper", "contains prefered1 " + p.id);
            return -1;
        } else if (!this.preferedProducts.containsKey(p.id) && this.preferedProducts.containsKey(competitor.id)) {
            Log.e("UserHelper", "contains prefered2 " + competitor.id);
            return 1;
        } else {
            Log.e("UserHelper", "!contains prefered");
            return p.title.compareTo(competitor.title);
        }*/
       return 0;
    }

    public PersonSimple getPerson() {
        return person;
    }

    public void setPerson(PersonSimple person) {
        this.person = person;
    }

    public void login(String token, PersonSimple person) {
        this.TOKEN = token;
        this.person = person;
        this.loggedIn = true;

        Services.invalidate();
    }

    public void logout() {
        ServiceGenerator.client.dispatcher().cancelAll();

        // unregister this device from fcm
        NotificationReceiver.invalidateFirebaseInstanceId(false);

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

    public void loadCurrentUser() {
        if (preferences.contains("current_user")) {
            String user = preferences.getString("current_user", "null");
            if (!user.equals("null")) {
                Log.e("UserHelper", user);
                this.person = new Gson().fromJson(user, PersonSimple.class);
            }
        }
    }

    public void saveCurrentUser() {
        if (!preferences.contains("current_user")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("current_user", new Gson().toJson(person));
            editor.apply();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("current_user");
            editor.putString("current_user", new Gson().toJson(person));
            editor.apply();
        }
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

    public void load(String token) {
        Log.e("UserHelper", "got token from savedInstance");
        if(token != null && !token.isEmpty()) {
            Gson gson = new Gson();
            try {
                PersonSimple person = gson.fromJson(new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), "UTF-8"), PersonSimple.class);
                login(token, person);
                Log.e("UserHelper", "succesfully logged in");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            this.person = null;
            this.TOKEN = null;
            this.loggedIn = false;
        }
    }

    public void load() {
        if(preferences == null) return;

        if(preferences.contains("TOKEN")) {
            Log.e("UserHelper", "prefrences contains token");
            Gson gson = new Gson();
            String token = preferences.getString("TOKEN", "null");
            try {
                if (!token.equals("null")) {
                    PersonSimple person = gson.fromJson(new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT), "UTF-8"), PersonSimple.class);
                    login(token, person);
                    Log.e("UserHelper", "succesfully logged in");
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
