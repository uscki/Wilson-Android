package nl.uscki.appcki.android.generated.agenda;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by peter on 11/24/16.
 */

public class AgendaParticipantLists {
    @Expose
    @SerializedName("participants")
    private
    List<AgendaParticipant> participants;
    @Expose
    @SerializedName("backupList")
    private
    List<AgendaParticipant> backupList;

    public List<AgendaParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<AgendaParticipant> participants) {
        this.participants = participants;
    }

    public List<AgendaParticipant> getBackupList() {
        return backupList;
    }

    public void setBackupList(List<AgendaParticipant> backupList) {
        this.backupList = backupList;
    }
}
