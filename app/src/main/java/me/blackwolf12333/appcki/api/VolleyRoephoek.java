package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.android.volley.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.common.VolleyAPI;
import me.blackwolf12333.appcki.events.RoephoekEvent;
import me.blackwolf12333.appcki.events.RoephoekOlderEvent;
import me.blackwolf12333.appcki.generated.roephoek.Roephoek;

/**
 * Created by Jorik on 13/02/16.
 */
public class VolleyRoephoek extends VolleyAPI {
    private static VolleyRoephoek instance;

    public static synchronized VolleyRoephoek getInstance( ) {
        if (instance == null)
            instance = new VolleyRoephoek();
        return instance;
    }

    public void getNewer(Integer id) {this.apiCall(new NewerCall(id)); }

    public void getOlder(Integer id) {this.apiCall(new OlderCall(id)); }

    public void addShout(String nickname, String message) {this.apiCall(new ShoutCall(nickname, message));}

    public class NewerCall extends Call<Roephoek> {
        public NewerCall(Integer id) {
            this.url = "shoutbox/newer";
            this.arguments = new HashMap<>();
            this.arguments.put("id", id);
            this.type = Roephoek.class;
            this.responseListener = new Response.Listener<Roephoek>() {
                @Override
                public void onResponse(Roephoek response) {
                    EventBus.getDefault().post(new RoephoekEvent(response));
                }
            };
        }
    }

    public class OlderCall extends Call<Roephoek> {
        public OlderCall(Integer id) {
            this.url = "shoutbox/older";
            this.arguments = new HashMap<>();
            this.arguments.put("id", id);
            this.type = Roephoek.class;
            this.responseListener = new Response.Listener<Roephoek>() {
                @Override
                public void onResponse(Roephoek response) {
                    EventBus.getDefault().post(new RoephoekOlderEvent(response));
                }
            };
        }
    }

    public class ShoutCall extends Call<Roephoek> {
        public ShoutCall(String nickname, String message) {
            this.url = "shoutbox/shout";
            this.arguments = new HashMap<>();
            try {
                this.arguments.put("nickname", URLEncoder.encode(nickname, "UTF-8"));
                this.arguments.put("message", URLEncoder.encode(message, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.d("ShoutCall", "couldn't urlencode nickname or message");
            }
            this.type = Roephoek.class;
            this.responseListener = new Response.Listener<Roephoek>() {
                @Override
                public void onResponse(Roephoek response) {
                    EventBus.getDefault().post(new RoephoekEvent(response));
                }
            };
        }
    }


}
