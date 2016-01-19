package me.blackwolf12333.appcki.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.User;
import me.blackwolf12333.appcki.events.JSONReadyEvent;
import me.blackwolf12333.appcki.events.NewPollEvent;

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
            System.out.println(poll.pollItem.title);
            EventBus.getDefault().post(new NewPollEvent(poll));
        }
    }

    public void vote(int id) {
        new APICall(user, "poll/vote").execute("id=" + id);
    }

    public boolean hasVoted() {
        return false; // FIXME: 12/30/15
    }

    public class Poll {
        @SerializedName("poll")
        public PollItem pollItem;
        @SerializedName("options")
        public PollOption[] options;
        @SerializedName("myVote")
        public int myvote;

        @Override
        public String toString() {
            return "Poll id=" + pollItem.id;
        }
    }

    public class PollItem {
        @SerializedName("id")
        public int id;
        @SerializedName("createdate")
        public String createdate;
        @SerializedName("title")
        public String title;
        @SerializedName("active")
        public boolean active;
        @SerializedName("bgcolor")
        public String bgcolor;
        @SerializedName("person")
        public PersonAPI.Person person;
    }

    public class PollFile {
        @SerializedName("id")
        int id;
        @SerializedName("person")
        PersonAPI.Person person;
        @SerializedName("date_added")
        String date_added;
        @SerializedName("md5")
        String md5;
        @SerializedName("filename")
        String filename;
        @SerializedName("allpersonstagged")
        boolean allpersonstagged;
        @SerializedName("mimetype")
        String mimetype;

        @Override
        public String toString() {
            return "PollFile id=" + id;
        }
    }

    public class PollOption {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("color")
        public String color;
        @SerializedName("textcolor")
        public String textcolor;
        @SerializedName("voteCount")
        public int votecount;

        @Override
        public String toString() {
            return "PollOption id=" + id;
        }
    }
}
