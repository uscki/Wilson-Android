package nl.uscki.appcki.android.generated.agenda;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import nl.uscki.appcki.android.generated.SingleValueWilsonItem;

/**
 * Created by peter on 7/17/16.
 */
public class AgendaItem extends SimpleAgendaItem{
    @Expose
    private String costs;
    @Expose
    private String who;
    @Expose
    private List<Object> description;
    @Expose
    private String what;
    @Expose
    private String when;
    @Expose
    private List<AgendaParticipant> participants;
    @Expose
    private List<AgendaParticipant> backupList;
    @Expose
    private String question;
    @Expose
    private String[] possible_answers;

    @Expose
    private AgendaUserParticipation userParticipation;

    public List<AgendaParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<AgendaParticipant> participants) {
        this.participants = participants;
    }

    public List<Object> getDescription() {
        return description;
    }

    public void setDescription(List<Object> description) {
        this.description = description;
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

    public List<AgendaParticipant> getBackupList() {
        return backupList;
    }

    public void setBackupList(List<AgendaParticipant> backupList) {
        this.backupList = backupList;
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

    public void setPossible_answers(String[] possible_answers) {
        this.possible_answers = possible_answers;
    }

    public AgendaUserParticipation getUserParticipation() {
        // API passes null, which is inconvenient. Make sure an object is always available
        if(this.userParticipation == null) this.userParticipation = new AgendaUserParticipation();
        return this.userParticipation;
    }

    public void setUserParticipation(AgendaUserParticipation userParticipation) {
        this.userParticipation = userParticipation;
    }
}
