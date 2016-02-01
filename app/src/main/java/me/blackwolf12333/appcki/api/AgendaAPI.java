package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.generated.Agenda;
import me.blackwolf12333.appcki.generated.AgendaItem;
import me.blackwolf12333.appcki.generated.ServerError;

/**
 * Created by peter on 1/25/16.
 */
public class AgendaAPI {
    User user;
    Gson gson;

    public AgendaAPI() {
        this.user = UserHelper.getInstance().getUser();
        this.gson = new Gson();
        EventBus.getDefault().register(this);
    }

    public void subscribe(AgendaItem item, String note) {
        new APICall(user, "agenda/subscribe").execute("id="+item.getId(), "note=" + note);
    }

    public void getAgenda() {
        new APICall(user, "agenda/newer").execute();
    }

    public void onEventMainThread(JSONReadyEvent event) {
        ServerError error = gson.fromJson(event.json, ServerError.class);

        // status is nooit null bij een server error, dus hiermee kunnen we checken of dit het goede
        // object was voor deserialization.
        if(error.getStatus() != null) {
            Log.e("APIClass", "error getting data from BadWolf with call: " + event.call + "\nserver error: " + error.toString());
            //TODO handle errors
        } else {
            if(event.call.startsWith("agenda")) {
                jsonReadyHandler(event);
            }
        }
    }

    public void jsonReadyHandler(JSONReadyEvent event) {
        switch(event.call) {
            case "agenda/newer":
                Agenda agenda = gson.fromJson(event.json, Agenda.class);
                EventBus.getDefault().post(new AgendaEvent(agenda));
                break;
            case "agenda/subscribe":
                Log.i("AgendaAPI", "handle agenda/subscribe response: " + event.json.toString());
                break;
        }
    }
}
