
package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Group {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String wickipage;
    @Expose
    private String emailaddress;
    @Expose
    private Boolean active;
    @Expose
    private Media media;
    @Expose
    private Parent parent;

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
     *     The wickipage
     */
    public String getWickipage() {
        return wickipage;
    }

    /**
     * 
     * @param wickipage
     *     The wickipage
     */
    public void setWickipage(String wickipage) {
        this.wickipage = wickipage;
    }

    /**
     * 
     * @return
     *     The emailaddress
     */
    public String getEmailaddress() {
        return emailaddress;
    }

    /**
     * 
     * @param emailaddress
     *     The emailaddress
     */
    public void setEmailaddress(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    /**
     * 
     * @return
     *     The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * 
     * @param active
     *     The active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * 
     * @return
     *     The media
     */
    public Media getMedia() {
        return media;
    }

    /**
     * 
     * @param media
     *     The media
     */
    public void setMedia(Media media) {
        this.media = media;
    }

    /**
     * 
     * @return
     *     The parent
     */
    public Parent getParent() {
        return parent;
    }

    /**
     * 
     * @param parent
     *     The parent
     */
    public void setParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
