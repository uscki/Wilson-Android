package nl.uscki.appcki.android.generated.agenda;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class AgendaUserParticipation implements IWilsonBaseItem {

    @Expose private Integer id;
    @Expose
    private String answer;
    @Expose private boolean attends;
    @Expose private boolean backuplist;
    @Expose private String note;
    @Expose private DateTime subscribed;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isAttends() {
        return attends;
    }

    public void setAttends(boolean attends) {
        this.attends = attends;
    }

    public boolean isBackuplist() {
        return backuplist;
    }

    public void setBackuplist(boolean backuplist) {
        this.backuplist = backuplist;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public DateTime getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(DateTime subscribed) {
        this.subscribed = subscribed;
    }
}
