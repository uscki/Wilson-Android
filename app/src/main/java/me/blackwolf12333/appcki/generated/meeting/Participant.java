package me.blackwolf12333.appcki.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

import me.blackwolf12333.appcki.generated.organisation.Person;

public class Participant {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("person")
    @Expose
    private Person person;

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
     * The person
     */
    public Person getPerson() {
        return person;
    }

    /**
     *
     * @param person
     * The person
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}