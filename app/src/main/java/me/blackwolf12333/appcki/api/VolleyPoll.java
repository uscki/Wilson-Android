package me.blackwolf12333.appcki.api;

import android.util.Log;

import com.android.volley.Response;

import java.util.HashMap;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.api.common.VolleyAPI;
import me.blackwolf12333.appcki.events.PollEvent;
import me.blackwolf12333.appcki.generated.poll.Poll;

/**
 * Created by peter on 2/7/16.
 */
public class VolleyPoll extends VolleyAPI {
    private static VolleyPoll instance;

    public static synchronized VolleyPoll getInstance( ) {
        if (instance == null)
            instance = new VolleyPoll();
        return instance;
    }

    public void getPoll(Integer id) {
        this.apiCall(new PollCall(id));
    }

    public void getActivePoll() {
        this.apiCall(new ActivePollCall());
    }

    public void vote(Integer id) {
        this.apiCall(new VoteCall(id));
    }

    public class PollCall extends Call<Poll> {
        public PollCall(Integer id) {
            this.url = "poll/get";
            this.arguments = new HashMap<>();
            this.arguments.put("id", id);
            this.type = Poll.class;
            this.responseListener = new Response.Listener<Poll>() {
                @Override
                public void onResponse(Poll response) {
                    EventBus.getDefault().post(new PollEvent(response));
                }
            };
        }
    }

    public class ActivePollCall extends Call<Poll> {
        public ActivePollCall() {
            this.url = "poll/active";
            this.arguments = new HashMap<>();
            this.type = Poll.class;
            this.responseListener = new Response.Listener<Poll>() {
                @Override
                public void onResponse(Poll response) {
                    EventBus.getDefault().post(new PollEvent(response));
                }
            };
        }
    }

    public class VoteCall extends Call<Boolean> {
        public VoteCall(Integer id) {
            this.url = "poll/vote";
            this.arguments = new HashMap<>();
            this.arguments.put("id", id);
            this.type = Boolean.class;
            this.responseListener = new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    Log.d("VoteCall", "response: " + response);
                }
            };
        }
    }
}
