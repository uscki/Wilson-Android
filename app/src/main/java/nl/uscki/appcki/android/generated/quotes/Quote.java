package nl.uscki.appcki.android.generated.quotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 3/1/17.
 */

public class Quote implements IWilsonBaseItem {
    @Expose
    @SerializedName("hasVoted")
    boolean hasVoted;
    @Expose
    @SerializedName("id")
    Integer id;
    @Expose
    @SerializedName("negativeVotes")
    Integer negativeVotes;
    @Expose
    @SerializedName("positiveVotes")
    Integer positiveVotes;
    @Expose
    @SerializedName("posted")
    DateTime posted;
    @Expose
    @SerializedName("quote")
    List<Object> quote;
    @Expose
    @SerializedName("totalWeight")
    Integer totalWeight;
    @Expose
    @SerializedName("weight")
    Integer weight;

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNegativeVotes() {
        return negativeVotes;
    }

    public void setNegativeVotes(Integer negativeVotes) {
        this.negativeVotes = negativeVotes;
    }

    public Integer getPositiveVotes() {
        return positiveVotes;
    }

    public void setPositiveVotes(Integer positiveVotes) {
        this.positiveVotes = positiveVotes;
    }

    public DateTime getPosted() {
        return posted;
    }

    public void setPosted(DateTime posted) {
        this.posted = posted;
    }

    public List<Object> getQuote() {
        return quote;
    }

    public void setQuote(List<Object> quoteJSON) {
        this.quote = quoteJSON;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Integer totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
