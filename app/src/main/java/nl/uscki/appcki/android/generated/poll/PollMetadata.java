package nl.uscki.appcki.android.generated.poll;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonName;

/**
 * Created by peter on 3/7/17.
 */

public class PollMetadata implements IWilsonBaseItem{
    /*{
"active": true,
"creation": "2017-03-07T13:31:42Z",
"id": 0,
"maker": {},
"title": "Bestaat vrije wil?"
}*/
    @Expose
    @SerializedName("active")
    Boolean active;
    @Expose
    @SerializedName("creation")
    DateTime creation;
    @Expose
    @SerializedName("id")
    Integer id;
    @Expose
    @SerializedName("maker")
    PersonName maker;
    @Expose
    @SerializedName("title")
    String title;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public DateTime getCreation() {
        return creation;
    }

    public void setCreation(DateTime creation) {
        this.creation = creation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PersonName getMaker() {
        return maker;
    }

    public void setMaker(PersonName maker) {
        this.maker = maker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
