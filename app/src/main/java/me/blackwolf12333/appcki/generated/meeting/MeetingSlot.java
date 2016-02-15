package me.blackwolf12333.appcki.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class MeetingSlot {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("starttime")
    @Expose
    private String starttime;
    @SerializedName("preferences")
    @Expose
    private List<Preference> preferences = new ArrayList<Preference>();

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
     * The starttime
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     *
     * @param starttime
     * The starttime
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    /**
     *
     * @return
     * The preferences
     */
    public List<Preference> getPreferences() {
        return preferences;
    }

    /**
     *
     * @param preferences
     * The preferences
     */
    public void setPreferences(List<Preference> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}