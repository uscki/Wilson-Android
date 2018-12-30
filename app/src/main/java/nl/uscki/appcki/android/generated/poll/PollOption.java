package nl.uscki.appcki.android.generated.poll;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 3/7/17.
 */

public class PollOption implements IWilsonBaseItem {
    /*
    * "color": "#ff000",
"id": 0,
"name": "Ja.",
"voteCount": 0*/
    @Expose
    @SerializedName("id")
    Integer id;
    @Expose
    @SerializedName("color")
    String color;
    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("voteCount")
    Integer voteCount;

    private int totalVoteCount;
    private int maxVote; // Best result for this poll

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public int getTotalVoteCount() {
        return totalVoteCount;
    }

    public void setTotalVoteCount(int totalVoteCount) {
        this.totalVoteCount = totalVoteCount;
    }

    public int getMaxVote() {
        return maxVote;
    }

    public void setMaxVote(int maxVote) {
        this.maxVote = maxVote;
    }
}
