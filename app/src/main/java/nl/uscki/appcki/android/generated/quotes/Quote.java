package nl.uscki.appcki.android.generated.quotes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by peter on 3/1/17.
 */

public class Quote {
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
    Long posted;
    @Expose
    @SerializedName("quote")
    String quote;
    @Expose
    @SerializedName("quoteJSON")
    List<Object> quoteJSON;
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

    public Long getPosted() {
        return posted;
    }

    public void setPosted(Long posted) {
        this.posted = posted;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public List<Object> getQuoteJSON() {
        return quoteJSON;
    }

    public void setQuoteJSON(List<Object> quoteJSON) {
        this.quoteJSON = quoteJSON;
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
