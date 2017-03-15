
package nl.uscki.appcki.android.generated.media;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.generated.organisation.Person;

public class MediaCollection {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String date;
    @Expose
    private String location;
    @Expose
    private List<MediaCollection> subCollections = new ArrayList<>();
    @Expose
    private Size size;
    @Expose
    private String adddate;
    @Expose
    private Boolean tobackup;
    @Expose
    private Boolean internal;
    @Expose
    private Boolean showinfeeds;
    @Expose
    private Person person;
    @Expose
    private List<MediaFile> mediaFiles = new ArrayList<>();

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
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The date
     */
    public String getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 
     * @return
     *     The location
     */
    public String getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 
     * @return
     *     The subCollections
     */
    public List<MediaCollection> getSubCollections() {
        return subCollections;
    }

    /**
     * 
     * @param subCollections
     *     The subCollections
     */
    public void setSubCollections(List<MediaCollection> subCollections) {
        this.subCollections = subCollections;
    }

    /**
     * 
     * @return
     *     The size
     */
    public Size getSize() {
        return size;
    }

    /**
     * 
     * @param size
     *     The size
     */
    public void setSize(Size size) {
        this.size = size;
    }

    /**
     * 
     * @return
     *     The adddate
     */
    public String getAdddate() {
        return adddate;
    }

    /**
     * 
     * @param adddate
     *     The adddate
     */
    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    /**
     * 
     * @return
     *     The tobackup
     */
    public Boolean getTobackup() {
        return tobackup;
    }

    /**
     * 
     * @param tobackup
     *     The tobackup
     */
    public void setTobackup(Boolean tobackup) {
        this.tobackup = tobackup;
    }

    /**
     * 
     * @return
     *     The internal
     */
    public Boolean getInternal() {
        return internal;
    }

    /**
     * 
     * @param internal
     *     The internal
     */
    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    /**
     * 
     * @return
     *     The showinfeeds
     */
    public Boolean getShowinfeeds() {
        return showinfeeds;
    }

    /**
     * 
     * @param showinfeeds
     *     The showinfeeds
     */
    public void setShowinfeeds(Boolean showinfeeds) {
        this.showinfeeds = showinfeeds;
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
     *     The mediaFiles
     */
    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    /**
     * 
     * @param mediaFiles
     *     The mediaFiles
     */
    public void setMediaFiles(List<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
