package nl.uscki.appcki.android.generated.agenda;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class SimpleAgendaItem implements IWilsonBaseItem {

    @Expose
    private Integer id;

    @Expose
    private String title;

    @Expose
    private Boolean alwaysonfrontpage;

    @Expose
    private DateTime start;

    @Expose
    private DateTime end;

    @Expose
    private String location;

    @Expose
    private Boolean registrationrequired;

    @Expose
    private DateTime deadline;

    @Expose
    private Boolean hasDeadline;

    @Expose
    private DateTime unregisterDeadline;

    @Expose
    private Boolean hasUnregisterDeadline;

    @Expose
    private Integer maxregistrations;

    @Expose
    private Integer posterid;

    @Expose
    private AgendaCategory category;

    @Expose
    private int totalBackuplist;

    @Expose
    private int totalComments;

    @Expose
    private int totalParticipants;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getEnd() {
        return new DateTime(end);
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public DateTime getStart() {
        return new DateTime(start);
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getDeadline() {
        return new DateTime(deadline);
    }

    public void setDeadline(DateTime deadline) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getHasUnregisterDeadline() {
        return hasUnregisterDeadline;
    }

    public void setHasUnregisterDeadline(Boolean hasUnregisterDeadline) {
        this.hasUnregisterDeadline = hasUnregisterDeadline;
    }

    public Integer getMaxregistrations() {
        return maxregistrations;
    }

    public void setMaxregistrations(Integer maxregistrations) {
        this.maxregistrations = maxregistrations;
    }

    public Boolean getAlwaysonfrontpage() {
        return alwaysonfrontpage;
    }

    public void setAlwaysonfrontpage(Boolean alwaysonfrontpage) {
        this.alwaysonfrontpage = alwaysonfrontpage;
    }

    public Boolean getRegistrationrequired() {
        return registrationrequired;
    }

    public void setRegistrationrequired(Boolean registrationrequired) {
        this.registrationrequired = registrationrequired;
    }

    public DateTime getUnregisterDeadline() {
        return new DateTime(unregisterDeadline);
    }

    public void setUnregisterDeadline(DateTime unregisterDeadline) {
        this.unregisterDeadline = unregisterDeadline;
    }

    public Integer getPosterid() {
        return posterid;
    }

    public void setPosterid(Integer posterid) {
        this.posterid = posterid;
    }

    public AgendaCategory getCategory() {
        return category;
    }

    public void setCategory(AgendaCategory categories) {
        this.category = categories;
    }

    public int getTotalBackuplist() {
        return totalBackuplist;
    }

    public void setTotalBackuplist(int totalBackuplist) {
        this.totalBackuplist = totalBackuplist;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }
}
