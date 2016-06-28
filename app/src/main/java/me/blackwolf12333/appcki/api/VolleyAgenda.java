package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.android.volley.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.common.VolleyAPI;
import me.blackwolf12333.appcki.events.AgendaEvent;
import me.blackwolf12333.appcki.events.AgendaItemEvent;
import me.blackwolf12333.appcki.events.AgendaItemSubscribedEvent;
import me.blackwolf12333.appcki.generated.agenda.Agenda;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

/**
 * Created by peter on 2/6/16.
 */
public class VolleyAgenda extends VolleyAPI {
    private static VolleyAgenda instance;

    public static synchronized VolleyAgenda getInstance( ) {
        if (instance == null)
            instance = new VolleyAgenda();
        return instance;
    }

    public void getAgendaItem(Integer id) {
        this.apiCall(new GetAgendaItem(id));
    }

    public void getAgendaNewer() {
        this.apiCall(new AgendaNewer());
    }

    public void subscribe(Integer id, String note) {
        this.apiCall(new AgendaSubscribe(id, note));
    }

    public void unsubscribe(Integer id) {
        this.apiCall(new AgendaUnsubscribe(id));
    }

    public class GetAgendaItem extends Call<AgendaItem> {
        public GetAgendaItem(Integer id) {
            this.url = "agenda/get";
            this.arguments = new HashMap<>();
            this.arguments.put("id", id);
            this.type = AgendaItem.class;
            this.responseListener = new Response.Listener<AgendaItem>() {
                @Override
                public void onResponse(AgendaItem response) {
                    EventBus.getDefault().post(new AgendaItemEvent(response));
                }
            };
        }
    }

    public class AgendaNewer extends Call<Agenda> {
        public AgendaNewer() {
            this.url = "agenda/newer";
            this.arguments = new HashMap<>();
            this.arguments.put("sort", "startdate,starttime,asc");
            this.type = Agenda.class;
            this.responseListener = new Response.Listener<Agenda>() {
                @Override
                public void onResponse(Agenda response) {
                    EventBus.getDefault().post(new AgendaEvent(response));
                }
            };
        }
    }

    public class AgendaSubscribe extends Call<Boolean> {
        public AgendaSubscribe(Integer id, String note) {
            this.url = "agenda/subscribe";
            this.arguments = new HashMap<>();
            try {
                this.arguments.put("id", id);
                this.arguments.put("note", URLEncoder.encode(note, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.d("ShoutCall", "couldn't urlencode nickname or message");
            }
            this.type = Boolean.class;
            this.responseListener = new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    // this response is useless...geeft altijd true terug
                    EventBus.getDefault().post(new AgendaItemSubscribedEvent(response));
                }
            };
        }
    }

    public class AgendaUnsubscribe extends Call<Boolean> {
        public AgendaUnsubscribe(Integer id) {
            this.url = "agenda/unsubscribe";
            this.arguments = new HashMap<>();
            this.arguments.put("id", id);
            this.type = Boolean.class;
            this.responseListener = new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    EventBus.getDefault().post(new AgendaItemSubscribedEvent(false));
                }
            };
        }
    }
}
