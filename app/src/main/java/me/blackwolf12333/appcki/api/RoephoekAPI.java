package me.blackwolf12333.appcki.api;

import com.google.gson.annotations.SerializedName;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.RoephoekEvent;

/**
 * Created by peter on 1/25/16.
 */
public class RoephoekAPI extends APIClass {
    public RoephoekAPI(User user) {
        super(user);
        EventBus.getDefault().register(this);
    }

    public void getRoephoek() {
        new APICall(user, "shoutbox/older").execute();
    }

    public void roep(String nickname, String message) {
        new APICall(user, "shoutbox/shout").execute("nickname=" + nickname, "message=" + message);
    }

    //@Override
    public void jsonReadyHandler(JSONReadyEvent event) {
        switch (event.call) {
            case "shoutbox/older":
                Roephoek roephoek = gson.fromJson(event.json, Roephoek.class);
                if(roephoek != null) {
                    EventBus.getDefault().post(new RoephoekEvent(roephoek));
                }
            case "shoutbox/shout":
                //System.out.println(event.json);
        }
    }

    public class Roephoek {
        @SerializedName("content")
        public Roep[] content;
        @SerializedName("totalElements")
        public int totalElements;
        @SerializedName("totalPages")
        public int totalPages;
        @SerializedName("last")
        public boolean last;
        @SerializedName("sort")
        public Object sort;
        @SerializedName("numberOfElements")
        public int numberOfElements;
        @SerializedName("first")
        public boolean first;
        @SerializedName("size")
        public int size;
        @SerializedName("number")
        public int number;
    }

    public class Roep {
        @SerializedName("id")
        public int id;
        @SerializedName("nickname")
        public String nickname;
        @SerializedName("message")
        public String message;
        @SerializedName("timestamp")
        public String timestamp;
    }
}
