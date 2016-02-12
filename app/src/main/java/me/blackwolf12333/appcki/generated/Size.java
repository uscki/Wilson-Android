
package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Size {

    @Expose
    private Integer id;
    @Expose
    private Integer small_x;
    @Expose
    private Integer small_y;
    @Expose
    private Integer medium_x;
    @Expose
    private Integer medium_y;

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
     *     The small_x
     */
    public Integer getSmall_x() {
        return small_x;
    }

    /**
     * 
     * @param small_x
     *     The small_x
     */
    public void setSmall_x(Integer small_x) {
        this.small_x = small_x;
    }

    /**
     * 
     * @return
     *     The small_y
     */
    public Integer getSmall_y() {
        return small_y;
    }

    /**
     * 
     * @param small_y
     *     The small_y
     */
    public void setSmall_y(Integer small_y) {
        this.small_y = small_y;
    }

    /**
     * 
     * @return
     *     The medium_x
     */
    public Integer getMedium_x() {
        return medium_x;
    }

    /**
     * 
     * @param medium_x
     *     The medium_x
     */
    public void setMedium_x(Integer medium_x) {
        this.medium_x = medium_x;
    }

    /**
     * 
     * @return
     *     The medium_y
     */
    public Integer getMedium_y() {
        return medium_y;
    }

    /**
     * 
     * @param medium_y
     *     The medium_y
     */
    public void setMedium_y(Integer medium_y) {
        this.medium_y = medium_y;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
