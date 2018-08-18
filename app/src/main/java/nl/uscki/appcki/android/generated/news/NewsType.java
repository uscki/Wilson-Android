
package nl.uscki.appcki.android.generated.news;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class NewsType implements IWilsonBaseItem {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String icon;

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
     *     The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 
     * @param icon
     *     The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
