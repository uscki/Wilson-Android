
package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;


public class Category {

    @Expose
    private Integer id;
    @Expose
    private String menutitle;
    @Expose
    private String singular;
    @Expose
    private String plural;

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
     *     The menutitle
     */
    public String getMenutitle() {
        return menutitle;
    }

    /**
     * 
     * @param menutitle
     *     The menutitle
     */
    public void setMenutitle(String menutitle) {
        this.menutitle = menutitle;
    }

    /**
     * 
     * @return
     *     The singular
     */
    public String getSingular() {
        return singular;
    }

    /**
     * 
     * @param singular
     *     The singular
     */
    public void setSingular(String singular) {
        this.singular = singular;
    }

    /**
     * 
     * @return
     *     The plural
     */
    public String getPlural() {
        return plural;
    }

    /**
     * 
     * @param plural
     *     The plural
     */
    public void setPlural(String plural) {
        this.plural = plural;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
