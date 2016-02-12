package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PollItem {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("createdate")
    @Expose
    private String createdate;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("bgcolor")
    @Expose
    private String bgcolor;
    @SerializedName("file")
    @Expose
    private File file;
    @SerializedName("person")
    @Expose
    private Person person;


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
     * The createdate
     */
    public String getCreatedate() {
        return createdate;
    }

    /**
     *
     * @param createdate
     * The createdate
     */
    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     *
     * @param active
     * The active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     *
     * @return
     * The bgcolor
     */
    public String getBgcolor() {
        return bgcolor;
    }

    /**
     *
     * @param bgcolor
     * The bgcolor
     */
    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     *
     * @return
     * The file
     */
    public File getFile() {
        return file;
    }

    /**
     *
     * @param file
     * The file
     */
    public void setFile(File file) {
        this.file = file;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}