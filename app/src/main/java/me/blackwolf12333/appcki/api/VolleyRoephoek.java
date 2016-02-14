package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.android.volley.Response;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.events.RoephoekEvent;
import me.blackwolf12333.appcki.generated.Roephoek;

/**
 * Created by Jorik on 13/02/16.
 */
public class VolleyRoephoek extends VolleyAPI {
    public void getNewer(Integer id) {this.apiCall(new NewerCall(id)); }

    public void getOlder(Integer id) {this.apiCall(new OlderCall(id)); }

    public void getShout(String nickname, String message) {this.apiCall(new ShoutCall(nickname, message));}

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
                    Log.i("Response: ", response.toString());
                    EventBus.getDefault().post(new RoephoekEvent(response));
                }
            };
        }
    }

    public class ShoutCall extends Call<Roephoek> {
        public ShoutCall(String nickname, String message) {
            this.url = "shoutbox/shout";
            this.arguments = new HashMap<>();
            this.arguments.put("nickname", nickname);
            this.arguments.put("message", message);
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
