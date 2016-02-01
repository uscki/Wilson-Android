package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.UserHelper;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.generated.ServerError;

/**
 * Created by peter on 1/25/16.
 */
public abstract class APIClass {
    protected User user;
    protected Gson gson;

    private AgendaEventListener agendaListener;
    private PollEventListener pollListener;
    private NewsEventListener newsListener;

    public APIClass() {
        this.user = UserHelper.getInstance().getUser();
        this.gson = new Gson();
        EventBus.getDefault().register(this);
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
                agendaListener.jsonReadyHandler(event);
            } else if(event.call.startsWith("poll")) {
                pollListener.jsonReadyHandler(event);
            } else if(event.call.startsWith("news")) {
                newsListener.jsonReadyHandler(event);
            }

        }
    }

    public void setAgendaListener(AgendaEventListener agendaListener) {
        this.agendaListener = agendaListener;
    }

    public void setNewsListener(NewsEventListener newsListener) {
        this.newsListener = newsListener;
    }

    public void setPollListener(PollEventListener pollListener) {
        this.pollListener = pollListener;
    }

    public interface AgendaEventListener {
        void jsonReadyHandler(JSONReadyEvent event);
    }

    public interface PollEventListener {
        void jsonReadyHandler(JSONReadyEvent event);
    }

    public interface NewsEventListener {
        void jsonReadyHandler(JSONReadyEvent event);
    }
}
