
package nl.uscki.appcki.android.generated.news;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonName;

public class NewsItem implements IWilsonBaseItem {

    @Expose
    private Integer id;
    @Expose
    private List<Object> shorttext;
    @Expose
    private List<Object> longtext;
    @Expose
    private String title;
    @Expose
    private DateTime posteddate;
    @Expose
    private PersonName person;
    @Expose
    private String link;
    @Expose
    private String sticky;
    @Expose
    private DateTime timestamp;
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
    public List<Object> getShorttext() {
        return shorttext;
    }

    /**
     * 
     * @param shorttext
     *     The shorttext
     */
    public void setShorttext(List<Object> shorttext) {
        this.shorttext = shorttext;
    }

    /**
     * 
     * @return
     *     The longtext
     */
    public List<Object> getLongtext() {
        return longtext;
    }

    /**
     * 
     * @param longtext
     *     The longtext
     */
    public void setLongtext(List<Object> longtext) {
        this.longtext = longtext;
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
    public DateTime getPosteddate() {
        return posteddate;
    }

    /**
     * 
     * @param posteddate
     *     The posteddate
     */
    public void setPosteddate(DateTime posteddate) {
        this.posteddate = posteddate;
    }

    /**
     * 
     * @return
     *     The person
     */
    public PersonName getPerson() {
        return person;
    }

    /**
     * 
     * @param person
     *     The person
     */
    public void setPerson(PersonName person) {
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

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
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
