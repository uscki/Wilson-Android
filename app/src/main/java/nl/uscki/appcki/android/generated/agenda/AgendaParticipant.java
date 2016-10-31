
package nl.uscki.appcki.android.generated.agenda;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.organisation.Person;

public class AgendaParticipant {

    @Expose
    private Integer id;
    @Expose
    private Person person;
    @Expose
    private String note;
    @Expose
    private Long subscribed;
    @Expose
    private Boolean backuplist;
    @Expose
    private Boolean attends;

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * 
     * @param person
     *     The person
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * 
     * @return
     *     The note
     */
    public String getNote() {
        return note;
    }

    /**
     * 
     * @param note
     *     The note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * 
     * @return
     *     The subscribed
     */
    public DateTime getSubscribed() {
        return new DateTime(subscribed);
    }

    /**
     * 
     * @param subscribed
     *     The subscribed
     */
    public void setSubscribed(Long subscribed) {
        this.subscribed = subscribed;
    }

    /**
     * 
     * @return
     *     The backuplist
     */
    public Boolean getBackuplist() {
        return backuplist;
    }

    /**
     * 
     * @param backuplist
     *     The backuplist
     */
    public void setBackuplist(Boolean backuplist) {
        this.backuplist = backuplist;
    }

    /**
     * 
     * @return
     *     The attends
     */
    public Boolean getAttends() {
        return attends;
    }

    /**
     * 
     * @param attends
     *     The attends
     */
    public void setAttends(Boolean attends) {
        this.attends = attends;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
