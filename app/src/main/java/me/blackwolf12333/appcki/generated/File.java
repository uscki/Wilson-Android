package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

public class File {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("person")
    @Expose
    private Person person;
    @SerializedName("date_added")
    @Expose
    private String dateAdded;
    @SerializedName("md5")
    @Expose
    private String md5;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("allpersonstagged")
    @Expose
    private Boolean allpersonstagged;
    @SerializedName("mimetype")
    @Expose
    private String mimetype;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The person
     */
    public Person getPerson() {
        return person;
    }

    /**
     *
     * @param person
     * The person
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     *
     * @return
     * The dateAdded
     */
    public String getDateAdded() {
        return dateAdded;
    }

    /**
     *
     * @param dateAdded
     * The date_added
     */
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     *
     * @return
     * The md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     *
     * @param md5
     * The md5
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     *
     * @return
     * The filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param filename
     * The filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     *
     * @return
     * The allpersonstagged
     */
    public Boolean getAllpersonstagged() {
        return allpersonstagged;
    }

    /**
     *
     * @param allpersonstagged
     * The allpersonstagged
     */
    public void setAllpersonstagged(Boolean allpersonstagged) {
        this.allpersonstagged = allpersonstagged;
    }

    /**
     *
     * @return
     * The mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     *
     * @param mimetype
     * The mimetype
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}