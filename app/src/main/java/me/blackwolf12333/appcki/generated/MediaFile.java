
package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MediaFile {

    @Expose
    private Integer id;
    @Expose
    private Person person;
    @Expose
    private Long date_added;
    @Expose
    private String md5;
    @Expose
    private String filename;
    @Expose
    private Boolean allpersonstagged;
    @Expose
    private String mimetype;

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
     *     The date_added
     */
    public Long getDate_added() {
        return date_added;
    }

    /**
     * 
     * @param date_added
     *     The date_added
     */
    public void setDate_added(Long date_added) {
        this.date_added = date_added;
    }

    /**
     * 
     * @return
     *     The md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * 
     * @param md5
     *     The md5
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 
     * @return
     *     The filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 
     * @param filename
     *     The filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * 
     * @return
     *     The allpersonstagged
     */
    public Boolean getAllpersonstagged() {
        return allpersonstagged;
    }

    /**
     * 
     * @param allpersonstagged
     *     The allpersonstagged
     */
    public void setAllpersonstagged(Boolean allpersonstagged) {
        this.allpersonstagged = allpersonstagged;
    }

    /**
     * 
     * @return
     *     The mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * 
     * @param mimetype
     *     The mimetype
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
