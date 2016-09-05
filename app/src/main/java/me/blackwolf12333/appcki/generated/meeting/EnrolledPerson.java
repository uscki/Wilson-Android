package me.blackwolf12333.appcki.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

public class EnrolledPerson {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("photomediaid")
    @Expose
    private Integer photomediaid;
    @SerializedName("name")
    @Expose
    private String name;

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
     * The photomediaid
     */
    public Integer getPhotomediaid() {
        return photomediaid;
    }

    /**
     *
     * @param photomediaid
     * The photomediaid
     */
    public void setPhotomediaid(Integer photomediaid) {
        this.photomediaid = photomediaid;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}