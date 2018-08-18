package nl.uscki.appcki.android.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;

public class Meeting implements IWilsonBaseItem{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("person")
    @Expose
    private PersonSimpleName person;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("actual_time")
    @Expose
    private Long actualTime;
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
    private Long startdate;
    @SerializedName("enddate")
    @Expose
    private Long enddate;
    @SerializedName("agenda")
    @Expose
    private String agenda;
    @SerializedName("plannotes")
    @Expose
    private String plannotes;
    @SerializedName("endPlanningPeriod")
    @Expose
    private Long endPlanningPeriod;
    @SerializedName("startPlanningPeriod")
    @Expose
    private Long startPlanningPeriod;

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
    public PersonSimpleName getPerson() {
        return person;
    }

    /**
     *
     * @param person
     * The person
     */
    public void setPerson(PersonSimpleName person) {
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
    public Long getActualTime() {
        return actualTime;
    }

    /**
     *
     * @param actualTime
     * The actual_time
     */
    public void setActualTime(Long actualTime) {
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
        if(startdate == null) {
            return null;
        }
        return new DateTime(startdate);
    }

    /**
     *
     * @param startdate
     * The startdate
     */
    public void setStartdate(Long startdate) {
        this.startdate = startdate;
    }

    /**
     *
     * @return
     * The enddate
     */
    public DateTime getEnddate() {
        if(enddate == null) {
            return null;
        }
        return new DateTime(enddate);
    }

    /**
     *
     * @param enddate
     * The enddate
     */
    public void setEnddate(Long enddate) {
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
        return new DateTime(endPlanningPeriod);
    }

    public void setEndPlanningPeriod(Long endPlanningPeriod) {
        this.endPlanningPeriod = endPlanningPeriod;
    }

    public DateTime getStartPlanningPeriod() {
        return new DateTime(startPlanningPeriod);
    }

    public void setStartPlanningPeriod(Long startPlanningPeriod) {
        this.startPlanningPeriod = startPlanningPeriod;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}