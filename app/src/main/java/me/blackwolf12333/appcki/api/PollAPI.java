package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewPollEvent;
import me.blackwolf12333.appcki.generated.Poll;

/**
 * Created by peter on 12/18/15.
 */
public class PollAPI {
    private User user;
    private Gson gson;

    public PollAPI(User user) {
        this.user = user;
        this.gson = new Gson();
        EventBus.getDefault().register(this);
    }

    public void getPoll(int id) {
        new APICall(user, "poll/get").execute("id="+id);
    }

    public void getActivePoll() {
        new APICall(user, "poll/active").execute();
    }

    public void onEventMainThread(JSONReadyEvent event) {
        Poll poll = gson.fromJson(event.json, Poll.class);
        if(poll != null) {
            //System.out.println(poll.pollItem.title);
            EventBus.getDefault().post(new NewPollEvent(poll));
        }
    }

    public void vote(int id) {
        new APICall(user, "poll/vote").execute("id=" + id);
    }

    public boolean hasVoted() {
        return false; // FIXME: 12/30/15
    }
}
