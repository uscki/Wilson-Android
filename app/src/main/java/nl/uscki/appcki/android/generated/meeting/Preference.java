package nl.uscki.appcki.android.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonSimple;

public class Preference  implements IWilsonBaseItem {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("canattend")
    @Expose
    private Boolean canattend;
    @SerializedName("person")
    @Expose
    private PersonSimple person;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     *
     * @param notes
     * The notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     *
     * @return
     * The canattend
     */
    public Boolean getCanattend() {
        return canattend;
    }

    /**
     *
     * @param canattend
     * The canattend
     */
    public void setCanattend(Boolean canattend) {
        this.canattend = canattend;
    }

    /**
     *
     * @return
     * The person
     */
    public PersonSimple getPerson() {
        return person;
    }

    /**
     *
     * @param person
     * The person
     */
    public void setPerson(PersonSimple person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}