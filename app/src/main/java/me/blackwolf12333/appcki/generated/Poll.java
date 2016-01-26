package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 1/25/16.
 */
public class Poll {
    @SerializedName("poll")
    private PollItem pollItem;
    @SerializedName("options")
    @Expose
    private List<PollOption> options = new ArrayList<PollOption>();
    @SerializedName("myVote")
    @Expose
    private Integer myVote;

    public void setMyVote(Integer myVote) {
        this.myVote = myVote;
    }

    public Integer getMyVote() {
        return myVote;
    }

    public PollItem getPollItem() {
        return pollItem;
    }

    public void setPollItem(PollItem pollItem) {
        this.pollItem = pollItem;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }
}
