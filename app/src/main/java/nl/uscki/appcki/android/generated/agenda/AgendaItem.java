package nl.uscki.appcki.android.generated.agenda;

import com.google.gson.annotations.Expose;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.List;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.SingleValueWilsonItem;

/**
 * Created by peter on 7/17/16.
 */
public class AgendaItem implements IWilsonBaseItem{
    @Expose
    private Integer id;
    @Expose
    private String costs;
    @Expose
    private String who;
    @Expose
    private Boolean hasDeadline;
    @Expose
    private Boolean hasUnregisterDeadline;
    @Expose
    private Integer maxregistrations;
    @Expose
    private Boolean alwaysonfrontpage;
    @Expose
    private Boolean registrationrequired;
    @Expose
    private Long deadline;
    @Expose
    private Long unregisterDeadline;
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
    private List<Object> descriptionJSON;
    @Expose
    private String what;
    @Expose
    private String when;
    @Expose
    private Integer posterid;
    @Expose
    private List<AgendaParticipant> participants;
    @Expose
    private List<AgendaParticipant> backupList;
    @Expose
    private List<AgendaCategory> categories;
    @Expose
    private String question;
    @Expose String[] possible_answers;

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

    public List<Object> getDescriptionJSON() {
        return descriptionJSON;
    }

    public void setDescriptionJSON(List<Object> descriptionJSON) {
        this.descriptionJSON = descriptionJSON;
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

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
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

    public Long getUnregisterDeadline() {
        return unregisterDeadline;
    }

    public void setUnregisterDeadline(Long unregisterDeadline) {
        this.unregisterDeadline = unregisterDeadline;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public Integer getPosterid() {
        return posterid;
    }

    public void setPosterid(Integer posterid) {
        this.posterid = posterid;
    }

    public List<AgendaParticipant> getBackupList() {
        return backupList;
    }

    public void setBackupList(List<AgendaParticipant> backupList) {
        this.backupList = backupList;
    }

    public List<AgendaCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<AgendaCategory> categories) {
        this.categories = categories;
    }

    public String getQuestion() { return question; }

    public void setQuestion(String question) {this.question = question; }

    public String[] getPossibleAnswers() { return possible_answers; }

    public void setPossibleAnswers(String[] possible_answers) { this.possible_answers = possible_answers; }

    public List<SingleValueWilsonItem<String>> getPossibleAnswersAsWilsonItemList() {
        List<SingleValueWilsonItem<String>> possibleAnswers = new ArrayList<>();
        for(int i = 0; i < possible_answers.length; i++) {
            possibleAnswers.add(new SingleValueWilsonItem<>(possible_answers[i], i));
        }
        return possibleAnswers;
    }
}
