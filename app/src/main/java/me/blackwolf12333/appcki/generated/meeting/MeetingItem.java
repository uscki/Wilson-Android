package me.blackwolf12333.appcki.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class MeetingItem {

    @SerializedName("meeting")
    @Expose
    private Meeting meeting;
    @SerializedName("enrolledPersons")
    @Expose
    private List<EnrolledPerson> enrolledPersons = new ArrayList<EnrolledPerson>();
    @SerializedName("slots")
    @Expose
    private List<Slot> slots = new ArrayList<Slot>();
    @SerializedName("myPreferences")
    @Expose
    private List<MyPreference> myPreferences = new ArrayList<MyPreference>();

    /**
     *
     * @return
     * The meeting
     */
    public Meeting getMeeting() {
        return meeting;
    }

    /**
     *
     * @param meeting
     * The meeting
     */
    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    /**
     *
     * @return
     * The enrolledPersons
     */
    public List<EnrolledPerson> getEnrolledPersons() {
        return enrolledPersons;
    }

    /**
     *
     * @param enrolledPersons
     * The enrolledPersons
     */
    public void setEnrolledPersons(List<EnrolledPerson> enrolledPersons) {
        this.enrolledPersons = enrolledPersons;
    }

    /**
     *
     * @return
     * The slots
     */
    public List<Slot> getSlots() {
        return slots;
    }

    /**
     *
     * @param slots
     * The slots
     */
    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    /**
     *
     * @return
     * The myPreferences
     */
    public List<MyPreference> getMyPreferences() {
        return myPreferences;
    }

    /**
     *
     * @param myPreferences
     * The myPreferences
     */
    public void setMyPreferences(List<MyPreference> myPreferences) {
        this.myPreferences = myPreferences;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}