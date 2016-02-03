package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.events.AgendaItemEvent;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.ShowProgressEvent;
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
        this.gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        EventBus.getDefault().register(this);
    }

    public void subscribe(AgendaItem item, String note) {
        new APICall(user, "agenda/subscribe").execute("id="+item.getId(), "note=" + note);
    }

    public void unsubscribe(AgendaItem item) {
        new APICall(user, "agenda/unsubscribe").execute("id="+item.getId());
    }

    public void getAgendaNewer() {
        EventBus.getDefault().post(new ShowProgressEvent(true));
        new APICall(user, "agenda/newer").execute("sort=startdate,starttime,desc");
    }

    public void getAgendaItem(Integer id) {
        EventBus.getDefault().post(new ShowProgressEvent(true));
        new APICall(user, "agenda/get").execute("id=" + id);
    }

    public void onEventMainThread(JSONReadyEvent event) {
        ServerError error = null;
        try {
            error = gson.fromJson(event.json, ServerError.class);
        } catch(JsonSyntaxException e) {
            Log.i("AgendaAPI", "json syntax exception: " + event.json.toString());
        }


        // status is nooit null bij een server error, dus hiermee kunnen we checken of dit het goede
        // object was voor deserialization.
        if(error != null && error.getStatus() != null) {
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
                EventBus.getDefault().post(new AgendaItemSubscribedEvent(true));
                break;
            case "agenda/unsubscribe":
                Log.i("AgendaAPI", "handle agenda/unsubscribe response: " + event.json.toString());
                EventBus.getDefault().post(new AgendaItemSubscribedEvent(false));
                break;
            case "agenda/get":
                AgendaItem agendaItem = gson.fromJson(event.json, AgendaItem.class);
                EventBus.getDefault().post(new AgendaItemEvent(agendaItem));
                break;
            default:
                Log.i("AgendaAPI", "unhandled api call: " + event.call);
        }
    }
}
