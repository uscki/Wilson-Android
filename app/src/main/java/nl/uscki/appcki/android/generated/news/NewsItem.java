
package nl.uscki.appcki.android.generated.news;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;

public class NewsItem {

    @Expose
    private Integer id;
    @Expose
    private String shorttext;
    @Expose
    private List<Object> shorttextJSON;
    @Expose
    private String longtext;
    @Expose
    private List<Object> longtextJSON;
    @Expose
    private String title;
    @Expose
    private String posteddate;
    @Expose
    private PersonSimpleName person;
    @Expose
    private String link;
    @Expose
    private String sticky;
    @Expose
    private Long timestamp;
    @Expose
    private NewsType type;

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
     *     The shorttext
     */
    public String getShorttext() {
        return shorttext;
    }

    /**
     * 
     * @param shorttext
     *     The shorttext
     */
    public void setShorttext(String shorttext) {
        this.shorttext = shorttext;
    }

    public List<Object> getShorttextJSON() {
        return shorttextJSON;
    }

    public void setShorttextJSON(List<Object> shorttextJSON) {
        this.shorttextJSON = shorttextJSON;
    }

    /**
     * 
     * @return
     *     The longtext
     */
    public String getLongtext() {
        return longtext;
    }

    /**
     * 
     * @param longtext
     *     The longtext
     */
    public void setLongtext(String longtext) {
        this.longtext = longtext;
    }

    public List<Object> getLongtextJSON() {
        return longtextJSON;
    }

    public void setLongtextJSON(List<Object> longtextJSON) {
        this.longtextJSON = longtextJSON;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The posteddate
     */
    public String getPosteddate() {
        return posteddate;
    }

    /**
     * 
     * @param posteddate
     *     The posteddate
     */
    public void setPosteddate(String posteddate) {
        this.posteddate = posteddate;
    }

    /**
     * 
     * @return
     *     The person
     */
    public PersonSimpleName getPerson() {
        return person;
    }

    /**
     * 
     * @param person
     *     The person
     */
    public void setPerson(PersonSimpleName person) {
        this.person = person;
    }

    /**
     * 
     * @return
     *     The link
     */
    public String getLink() {
        return link;
    }

    /**
     * 
     * @param link
     *     The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * 
     * @return
     *     The sticky
     */
    public String getSticky() {
        return sticky;
    }

    /**
     * 
     * @param sticky
     *     The sticky
     */
    public void setSticky(String sticky) {
        this.sticky = sticky;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 
     * @return
     *     The type
     */
    public NewsType getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(NewsType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
