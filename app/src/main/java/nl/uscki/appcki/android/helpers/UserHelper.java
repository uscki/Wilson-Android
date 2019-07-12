package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import java.util.HashMap;
import java.util.Map;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.CurrentUserUpdateRequiredDirectiveEvent;
import nl.uscki.appcki.android.generated.organisation.CurrentUser;
import nl.uscki.appcki.android.services.LoadFullUserInfoService;
import nl.uscki.appcki.android.services.NotificationReceiver;
import nl.uscki.appcki.android.generated.shop.Product;

/**
 * Created by peter on 1/31/16.
 */
public class UserHelper {
    private static UserHelper singleton;
    private String TOKEN;
    private CurrentUser person;
    private boolean loggedIn;
    private SharedPreferences preferences;
    private Map<Integer, Integer> preferedProducts = new HashMap<>();

    public String getToken() {
        return this.TOKEN;
    }

    private static final String FULL_USER_INFO_PREFERENCE_KEY = "nl.uscki.appcki.android.preference.CURRENT_USER_INFO";
    private static final String FULL_USER_INFO_PREFERENCE_LAST_SAVE_KEY = "nl.uscki.appcki.android.preference.CURRENT_USER_INFO_LAST_SAVED";

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

    public CurrentUser getCurrentUser() {
        return getCurrentUser(null);
    }

    public CurrentUser getCurrentUser(@Nullable Context context) {
        if(this.person == null) {
            String personJson = preferences.getString(FULL_USER_INFO_PREFERENCE_KEY, null);
            String lastSavedString = preferences.getString(FULL_USER_INFO_PREFERENCE_LAST_SAVE_KEY, null);
            if(personJson != null) {
                Gson gson = new Gson();
                this.person = gson.fromJson(personJson, CurrentUser.class);
            }

            if(this.person == null || lastSavedString == null ||
                    new DateTime(lastSavedString).isBefore(DateTime.now().minusDays(1))
            ) {
                if(context != null) {
                    scheduleCurrentUserUpdate(context);
                } else {
                    EventBus.getDefault().post(new CurrentUserUpdateRequiredDirectiveEvent());
                }
            }
        }

        return this.person;
    }

    public void scheduleCurrentUserUpdate(Context context) {
        Log.d(getClass().getSimpleName(), "Loading or refreshing full user info");
        Intent intent = new Intent(context, LoadFullUserInfoService.class);
        intent.setAction(LoadFullUserInfoService.ACTION_LOAD_USER);
        context.startService(intent);
    }

    public void setCurrentUser(CurrentUser person) {
        this.person = person;
        Gson gson = new Gson();
        SharedPreferences.Editor e = preferences.edit();
        e.putString(FULL_USER_INFO_PREFERENCE_KEY, gson.toJson(person));
        e.putString(FULL_USER_INFO_PREFERENCE_LAST_SAVE_KEY, DateTime.now().toString());
        e.apply();
    }

    private void removeFullPerson() {
        SharedPreferences.Editor e = preferences.edit();
        e.remove(FULL_USER_INFO_PREFERENCE_KEY);
        e.remove(FULL_USER_INFO_PREFERENCE_LAST_SAVE_KEY);
        e.apply();
    }

    public void login(String token) {
        this.TOKEN = token;
        this.loggedIn = true;
        Services.invalidate();
        Log.e("UserHelper", "succesfully logged in");
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

        removeFullPerson();
        this.loggedIn = false;
        this.TOKEN = null;
        this.setCurrentUser(null);
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

    public void load(String token) {
        Log.e("UserHelper", "got token from savedInstance");
        if(token != null && !token.isEmpty()) {
            getCurrentUser();
            login(token);
        } else {
            this.person = null;
            this.TOKEN = null;
            this.loggedIn = false;
        }
    }

    public void load() {
        if(preferences == null) return;

        if(preferences.contains("TOKEN")) {
            String token = preferences.getString("TOKEN", "null");
            if (!token.equals("null")) {
                getCurrentUser();
                login(token);
            } else {
                loggedIn = false;
            }
        } else {
            this.person = null;
            this.TOKEN = null;
            this.loggedIn = false;
        }
    }
}
