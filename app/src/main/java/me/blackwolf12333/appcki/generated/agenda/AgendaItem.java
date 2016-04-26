
package me.blackwolf12333.appcki.generated.agenda;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import me.blackwolf12333.appcki.generated.Category;
import me.blackwolf12333.appcki.generated.media.MediaCollection;
import me.blackwolf12333.appcki.generated.media.MediaFile;
import me.blackwolf12333.appcki.generated.organisation.Committee;

public class AgendaItem {

    @Expose
    private Integer id;
    @Expose
    private String startdate;
    @Expose
    private String starttime;
    @Expose
    private String enddate;
    @Expose
    private String endtime;
    @Expose
    private String shortdescription;
    @Expose
    private String longdescription;
    @Expose
    private Boolean deadline;
    @Expose
    private String deadline_date;
    @Expose
    private String deadline_time;
    @Expose
    private Committee committee;
    @Expose
    private String lastmodified;
    @Expose
    private String who;
    @Expose
    private String what;
    @Expose
    private String where;
    @Expose
    private String when;
    @Expose
    private String costs;
    @Expose
    private Boolean unregisterdeadline;
    @Expose
    private String unregisterdeadline_time;
    @Expose
    private String unregisterdeadline_date;
    @Expose
    private MediaFile posterId;
    @Expose
    private MediaCollection mediaCollection;
    @Expose
    private Boolean alwaysonfrontpage;
    @Expose
    private Boolean registrationrequired;
    @Expose
    private Integer maxregistrations;
    @Expose
    private List<Category> categories = new ArrayList<Category>();
    @Expose
    private List<AgendaParticipant> participants = new ArrayList<AgendaParticipant>();

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
     *     The startdate
     */
    public String getStartdate() {
        return startdate;
    }

    /**
     * 
     * @param startdate
     *     The startdate
     */
    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    /**
     * 
     * @return
     *     The starttime
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     * 
     * @param starttime
     *     The starttime
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    /**
     * 
     * @return
     *     The enddate
     */
    public String getEnddate() {
        return enddate;
    }

    /**
     * 
     * @param enddate
     *     The enddate
     */
    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    /**
     * 
     * @return
     *     The endtime
     */
    public String getEndtime() {
        return endtime;
    }

    /**
     * 
     * @param endtime
     *     The endtime
     */
    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    /**
     * 
     * @return
     *     The shortdescription
     */
    public String getShortdescription() {
        return shortdescription;
    }

    /**
     * 
     * @param shortdescription
     *     The shortdescription
     */
    public void setShortdescription(String shortdescription) {
        this.shortdescription = shortdescription;
    }

    /**
     * 
     * @return
     *     The longdescription
     */
    public String getLongdescription() {
        return longdescription;
    }

    /**
     * 
     * @param longdescription
     *     The longdescription
     */
    public void setLongdescription(String longdescription) {
        this.longdescription = longdescription;
    }

    /**
     * 
     * @return
     *     The deadline
     */
    public Boolean getDeadline() {
        return deadline;
    }

    /**
     * 
     * @param deadline
     *     The deadline
     */
    public void setDeadline(Boolean deadline) {
        this.deadline = deadline;
    }

    /**
     * 
     * @return
     *     The deadline_date
     */
    public String getDeadline_date() {
        return deadline_date;
    }

    /**
     * 
     * @param deadline_date
     *     The deadline_date
     */
    public void setDeadline_date(String deadline_date) {
        this.deadline_date = deadline_date;
    }

    /**
     * 
     * @return
     *     The deadline_time
     */
    public String getDeadline_time() {
        return deadline_time;
    }

    /**
     * 
     * @param deadline_time
     *     The deadline_time
     */
    public void setDeadline_time(String deadline_time) {
        this.deadline_time = deadline_time;
    }

    /**
     * 
     * @return
     *     The committee
     */
    public Committee getCommittee() {
        return committee;
    }

    /**
     * 
     * @param committee
     *     The committee
     */
    public void setCommittee(Committee committee) {
        this.committee = committee;
    }

    /**
     * 
     * @return
     *     The lastmodified
     */
    public String getLastmodified() {
        return lastmodified;
    }

    /**
     * 
     * @param lastmodified
     *     The lastmodified
     */
    public void setLastmodified(String lastmodified) {
        this.lastmodified = lastmodified;
    }

    /**
     * 
     * @return
     *     The who
     */
    public String getWho() {
        return who;
    }

    /**
     * 
     * @param who
     *     The who
     */
    public void setWho(String who) {
        this.who = who;
    }

    /**
     * 
     * @return
     *     The what
     */
    public String getWhat() {
        return what;
    }

    /**
     * 
     * @param what
     *     The what
     */
    public void setWhat(String what) {
        this.what = what;
    }

    /**
     * 
     * @return
     *     The where
     */
    public String getWhere() {
        return where;
    }

    /**
     * 
     * @param where
     *     The where
     */
    public void setWhere(String where) {
        this.where = where;
    }

    /**
     * 
     * @return
     *     The when
     */
    public String getWhen() {
        return when;
    }

    /**
     * 
     * @param when
     *     The when
     */
    public void setWhen(String when) {
        this.when = when;
    }

    /**
     * 
     * @return
     *     The costs
     */
    public String getCosts() {
        return costs;
    }

    /**
     * 
     * @param costs
     *     The costs
     */
    public void setCosts(String costs) {
        this.costs = costs;
    }

    /**
     * 
     * @return
     *     The unregisterdeadline
     */
    public Boolean getUnregisterdeadline() {
        return unregisterdeadline;
    }

    /**
     * 
     * @param unregisterdeadline
     *     The unregisterdeadline
     */
    public void setUnregisterdeadline(Boolean unregisterdeadline) {
        this.unregisterdeadline = unregisterdeadline;
    }

    /**
     * 
     * @return
     *     The unregisterdeadline_time
     */
    public String getUnregisterdeadline_time() {
        return unregisterdeadline_time;
    }

    /**
     * 
     * @param unregisterdeadline_time
     *     The unregisterdeadline_time
     */
    public void setUnregisterdeadline_time(String unregisterdeadline_time) {
        this.unregisterdeadline_time = unregisterdeadline_time;
    }

    /**
     * 
     * @return
     *     The unregisterdeadline_date
     */
    public String getUnregisterdeadline_date() {
        return unregisterdeadline_date;
    }

    /**
     * 
     * @param unregisterdeadline_date
     *     The unregisterdeadline_date
     */
    public void setUnregisterdeadline_date(String unregisterdeadline_date) {
        this.unregisterdeadline_date = unregisterdeadline_date;
    }

    public MediaFile getPosterid() {
        return posterId;
    }

    public void setPosterid(MediaFile posterid) {
        this.posterId = posterid;
    }

    /**
     * 
     * @return
     *     The mediaCollection
     */
    public MediaCollection getMediaCollection() {
        return mediaCollection;
    }

    /**
     * 
     * @param mediaCollection
     *     The mediaCollection
     */
    public void setMediaCollection(MediaCollection mediaCollection) {
        this.mediaCollection = mediaCollection;
    }

    /**
     * 
     * @return
     *     The alwaysonfrontpage
     */
    public Boolean getAlwaysonfrontpage() {
        return alwaysonfrontpage;
    }

    /**
     * 
     * @param alwaysonfrontpage
     *     The alwaysonfrontpage
     */
    public void setAlwaysonfrontpage(Boolean alwaysonfrontpage) {
        this.alwaysonfrontpage = alwaysonfrontpage;
    }

    /**
     * 
     * @return
     *     The registrationrequired
     */
    public Boolean getRegistrationrequired() {
        return registrationrequired;
    }

    /**
     * 
     * @param registrationrequired
     *     The registrationrequired
     */
    public void setRegistrationrequired(Boolean registrationrequired) {
        this.registrationrequired = registrationrequired;
    }

    /**
     * 
     * @return
     *     The maxregistrations
     */
    public Integer getMaxregistrations() {
        return maxregistrations;
    }

    /**
     * 
     * @param maxregistrations
     *     The maxregistrations
     */
    public void setMaxregistrations(Integer maxregistrations) {
        this.maxregistrations = maxregistrations;
    }

    /**
     * 
     * @return
     *     The categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * 
     * @param categories
     *     The categories
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    /**
     * 
     * @return
     *     The participants
     */
    public List<AgendaParticipant> getParticipants() {
        return participants;
    }

    /**
     * 
     * @param participants
     *     The participants
     */
    public void setParticipants(List<AgendaParticipant> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
