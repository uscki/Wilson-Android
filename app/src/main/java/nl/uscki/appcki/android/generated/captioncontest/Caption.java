package nl.uscki.appcki.android.generated.captioncontest;

import com.google.gson.annotations.Expose;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonName;

public class Caption implements IWilsonBaseItem {

    @Expose
    Integer id;

    @Expose
    List<Object> caption;

    @Expose
    PersonName person;

    @Expose
    Integer votes;

    private int maxVotes;

    @Override
    public Integer getId() {
        return id;
    }

    public List<Object> getCaption() {
        return caption;
    }

    public PersonName getPerson() {
        return person;
    }

    public Integer getVotes() {
        return votes;
    }

    public int getMaxVotes() {
        return maxVotes;
    }

    public void setMaxVotes(int maxVotes) {
        this.maxVotes = maxVotes;
    }
}
