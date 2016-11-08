package nl.uscki.appcki.android.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.generated.organisation.PersonSimple;

public class MeetingItem {

    @SerializedName("meeting")
    @Expose
    private Meeting meeting;
    @SerializedName("enrolledPersons")
    @Expose
    private List<PersonSimple> enrolledPersons = new ArrayList<PersonSimple>();
    @SerializedName("participation")
    @Expose
    private List<Participation> participation = new ArrayList<Participation>();
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
    public List<PersonSimple> getEnrolledPersons() {
        return enrolledPersons;
    }

    /**
     *
     * @param enrolledPersons
     * The enrolledPersons
     */
    public void setEnrolledPersons(List<PersonSimple> enrolledPersons) {
        this.enrolledPersons = enrolledPersons;
    }

    /**
     *
     * @return
     * The participation
     */
    public List<Participation> getParticipation() {
        return participation;
    }

    /**
     *
     * @param participation
     * The participation
     */
    public void setParticipation(List<Participation> participation) {
        this.participation = participation;
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

}