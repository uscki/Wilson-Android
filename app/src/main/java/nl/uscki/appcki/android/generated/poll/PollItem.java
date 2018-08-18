package nl.uscki.appcki.android.generated.poll;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 3/7/17.
 */

public class PollItem implements IWilsonBaseItem {
    @Expose
    @SerializedName("myVote")
    Integer myVote;
    @Expose
    @SerializedName("options")
    List<PollOption> options;
    @Expose
    @SerializedName("poll")
    PollMetadata poll;

    public Integer getId() {
        if(poll == null) return -1;
        return poll.getId();
    }

    public Integer getMyVote() {
        return myVote;
    }

    public void setMyVote(Integer myVote) {
        this.myVote = myVote;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public PollMetadata getPoll() {
        return poll;
    }

    public void setPoll(PollMetadata poll) {
        this.poll = poll;
    }
}
