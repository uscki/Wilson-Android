package nl.uscki.appcki.android.generated.smobo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.Committee;
import nl.uscki.appcki.android.generated.organisation.Person;

/**
 * Created by peter on 3/4/17.
 */

public class SmoboItem implements IWilsonBaseItem {
    @Expose
    @SerializedName("groups")
    List<Committee> groups;
    @Expose
    @SerializedName("mentorNode")
    SmoboMentorNode mentorNode;
    @Expose
    @SerializedName("person")
    Person person;
    @Expose
    @SerializedName("wickiPage")
    List<Object> wickiPage;
    @Expose
    @SerializedName("numOfPhotos")
    Integer numOfPhotos;

    public List<Committee> getGroups() {
        return groups;
    }

    public void setGroups(List<Committee> groups) {
        this.groups = groups;
    }

    public SmoboMentorNode getMentorNode() {
        return mentorNode;
    }

    public void setMentorNode(SmoboMentorNode mentorNode) {
        this.mentorNode = mentorNode;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Object> getWickiPage() {
        return wickiPage;
    }

    public void setWickiPage(List<Object> wickiPage) {
        this.wickiPage = wickiPage;
    }

    public int getNumOfPhotos() {
        return numOfPhotos == null ? 0 : numOfPhotos;
    }

    @Override
    public Integer getId() {
        return person.getId();
    }
}
