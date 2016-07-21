package me.blackwolf12333.appcki.generated.agenda;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.List;

import me.blackwolf12333.appcki.generated.media.MediaFile;

/**
 * Created by peter on 7/17/16.
 */
public class AgendaItem {
    @Expose
    private Integer id;
    @Expose
    private String committee;
    @Expose
    private String costs;
    @Expose
    private Boolean hasDeadline;
    @Expose
    private Long deadline;
    @Expose
    private String location;
    @Expose
    private Long start;
    @Expose
    private Long end;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private List<AgendaParticipant> participants;
    @Expose
    private MediaFile poster;

    public MediaFile getPoster() {
        return poster;
    }

    public void setPoster(MediaFile poster) {
        this.poster = poster;
    }

    public List<AgendaParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<AgendaParticipant> participants) {
        this.participants = participants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getEnd() {
        return new DateTime(end);
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public DateTime getStart() {
        return new DateTime(start);
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public DateTime getDeadline() {
        return new DateTime(deadline);
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getHasDeadline() {
        return hasDeadline;
    }

    public void setHasDeadline(Boolean hasDeadline) {
        this.hasDeadline = hasDeadline;
    }

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
