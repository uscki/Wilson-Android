package me.blackwolf12333.appcki.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

import me.blackwolf12333.appcki.generated.organisation.Person;

public class Meeting {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("person")
    @Expose
    private Person person;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("actual_time")
    @Expose
    private Long actualTime;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("reminder_time")
    @Expose
    private Integer reminderTime;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("interval")
    @Expose
    private String interval;
    @SerializedName("startdate")
    @Expose
    private String startdate;
    @SerializedName("starttime")
    @Expose
    private String starttime;
    @SerializedName("enddate")
    @Expose
    private String enddate;
    @SerializedName("endtime")
    @Expose
    private String endtime;
    @SerializedName("agenda")
    @Expose
    private String agenda;
    @SerializedName("plannotes")
    @Expose
    private String plannotes;
    @SerializedName("notifypct")
    @Expose
    private Integer notifypct;

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

    /**
     *
     * @return
     * The duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(Integer duration) {
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
     * The reminderTime
     */
    public Integer getReminderTime() {
        return reminderTime;
    }

    /**
     *
     * @param reminderTime
     * The reminder_time
     */
    public void setReminderTime(Integer reminderTime) {
        this.reminderTime = reminderTime;
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
     * The interval
     */
    public String getInterval() {
        return interval;
    }

    /**
     *
     * @param interval
     * The interval
     */
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     *
     * @return
     * The startdate
     */
    public String getStartdate() {
        return startdate;
    }

    /**
     *
     * @param startdate
     * The startdate
     */
    public void setStartdate(String startdate) {
        this.startdate = startdate;
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
     * The enddate
     */
    public String getEnddate() {
        return enddate;
    }

    /**
     *
     * @param enddate
     * The enddate
     */
    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    /**
     *
     * @return
     * The endtime
     */
    public String getEndtime() {
        return endtime;
    }

    /**
     *
     * @param endtime
     * The endtime
     */
    public void setEndtime(String endtime) {
        this.endtime = endtime;
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

    /**
     *
     * @return
     * The notifypct
     */
    public Integer getNotifypct() {
        return notifypct;
    }

    /**
     *
     * @param notifypct
     * The notifypct
     */
    public void setNotifypct(Integer notifypct) {
        this.notifypct = notifypct;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}