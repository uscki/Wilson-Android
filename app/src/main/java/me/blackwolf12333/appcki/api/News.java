package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import me.blackwolf12333.appcki.User;

/**
 * Created by peter on 12/9/15.
 */
public class News {

    User user;



    public News(User user) {
        this.user = user;
    }

    public void refresh() {
        try {
            Gson gson = new Gson();
            gson.fromJson( new APICall(user, "news/overview").execute().get(), News.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
