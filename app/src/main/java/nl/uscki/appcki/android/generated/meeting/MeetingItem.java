package nl.uscki.appcki.android.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonName;
import nl.uscki.appcki.android.helpers.UserHelper;

public class MeetingItem implements IWilsonBaseItem {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("meeting")
    @Expose
    private Meeting meeting;
    @SerializedName("enrolledPersons")
    @Expose
    private List<PersonName> enrolledPersons = new ArrayList<>(); // alle mensen die gereageerd hebben
    @SerializedName("participation")
    @Expose
    private List<Participation> participation = new ArrayList<>(); // alle mensen die zijn uitgenodigd
    @SerializedName("slots")
    @Expose
    private List<Slot> slots = new ArrayList<>();
    @SerializedName("myPreferences")
    @Expose
    private List<MyPreference> myPreferences = new ArrayList<>();

    public Integer getId() {
        return id;
    }

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
    public List<PersonName> getEnrolledPersons() {
        return enrolledPersons;
    }

    /**
     *
     * @param enrolledPersons
     * The enrolledPersons
     */
    public void setEnrolledPersons(List<PersonName> enrolledPersons) {
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

    public MeetingResponseStatus getResponseStatus() {
        if (getMeeting().getStartdate() != null) {
            return MeetingResponseStatus.MEETING_PLANNED;
        } else {
            if (!getEnrolledPersons().contains(UserHelper.getInstance().getCurrentUser())) {
                return MeetingResponseStatus.MEETING_NO_RESPONSE;
            } else {
                return MeetingResponseStatus.MEETING_RESPONSE_OK;
            }
        }
    }

    public enum MeetingResponseStatus {
        MEETING_PLANNED(R.string.meeting_planned, R.drawable.check, R.drawable.account_multiple),
        MEETING_NO_RESPONSE(R.string.meeting_response_MISSING, R.drawable.ic_outline_hourglass_empty_24px, R.drawable.account_multiple_backup),
        MEETING_RESPONSE_OK(R.string.meeting_response_OK, R.drawable.ic_outline_hourglass_empty_24px, R.drawable.account_multiple_subscribed);

        private int responseStatusMessage;
        private int responseStatusPeopleIcon;
        private int responseStatusIcon;

        MeetingResponseStatus(int responseStatusMessage, int responseStatusIcon, int responseStatusPeopleIcon) {
            this.responseStatusMessage = responseStatusMessage;
            this.responseStatusIcon = responseStatusIcon;
            this.responseStatusPeopleIcon = responseStatusPeopleIcon;
        }

        public int getResponseStatusMessage() {
            return responseStatusMessage;
        }

        public int getResponseStatusPeopleIcon() {
            return responseStatusPeopleIcon;
        }

        public int getResponseStatusIcon() {
            return responseStatusIcon;
        }
    }

}