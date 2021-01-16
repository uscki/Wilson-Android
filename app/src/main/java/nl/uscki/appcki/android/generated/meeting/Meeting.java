package nl.uscki.appcki.android.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonName;

public class Meeting implements IWilsonBaseItem{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("person")
    @Expose
    private PersonName person;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("actual_time")
    @Expose
    private DateTime actualTime;
    @SerializedName("actual_slot")
    @Expose
    private Slot actual_slot;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("startdate")
    @Expose
    private DateTime startdate;

    // TODO this should be a DateTime in the future. Ref https://git.dev.uscki.nl/AppCKI/B.A.D.W.O.L.F./issues/165
    @SerializedName("enddate")
    @Expose
    private DateTime enddate;

    @SerializedName("agenda")
    @Expose
    private String agenda;
    @SerializedName("plannotes")
    @Expose
    private String plannotes;
    @SerializedName("endPlanningPeriod")
    @Expose
    private DateTime endPlanningPeriod;
    @SerializedName("startPlanningPeriod")
    @Expose
    private DateTime startPlanningPeriod;

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
    public PersonName getPerson() {
        return person;
    }

    /**
     *
     * @param person
     * The person
     */
    public void setPerson(PersonName person) {
        this.person = person;
    }

    /**
     *
     * @return
     * The duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     * The actualTime
     */
    public DateTime getActualTime() {
        return actualTime;
    }

    /**
     *
     * @param actualTime
     * The actual_time
     */
    public void setActualTime(DateTime actualTime) {
        this.actualTime = actualTime;
    }

    public Slot getActual_slot() {
        return actual_slot;
    }

    public void setActual_slot(Slot actual_slot) {
        this.actual_slot = actual_slot;
    }

    /**
     *
     * @return
     * The location
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(String location) {
        this.location = location;
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
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The startdate
     */
    public DateTime getStartdate() {
        return startdate;
    }

    /**
     *
     * @param startdate
     * The startdate
     */
    public void setStartdate(DateTime startdate) {
        this.startdate = startdate;
    }

    /**
     *
     * @return
     * The enddate
     */
    public DateTime getEnddate() {
        // TODO should return enddate in future (see TODO at member)
        return new DateTime(this.enddate);
    }

    /**
     *
     * @param enddate
     * The enddate
     */
    public void setEnddate(DateTime enddate) {
        // TODO should just set the passed variable in future (see TODO at member)
        this.enddate = enddate;
    }

    /**
     *
     * @return
     * The agenda
     */
    public String getAgenda() {
        return agenda;
    }

    /**
     *
     * @param agenda
     * The agenda
     */
    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    /**
     *
     * @return
     * The plannotes
     */
    public String getPlannotes() {
        return plannotes;
    }

    /**
     *
     * @param plannotes
     * The plannotes
     */
    public void setPlannotes(String plannotes) {
        this.plannotes = plannotes;
    }

    public DateTime getEndPlanningPeriod() {
        return endPlanningPeriod;
    }

    public void setEndPlanningPeriod(DateTime endPlanningPeriod) {
        this.endPlanningPeriod = endPlanningPeriod;
    }

    public DateTime getStartPlanningPeriod() {
        return new DateTime(startPlanningPeriod);
    }

    public void setStartPlanningPeriod(DateTime startPlanningPeriod) {
        this.startPlanningPeriod = startPlanningPeriod;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}