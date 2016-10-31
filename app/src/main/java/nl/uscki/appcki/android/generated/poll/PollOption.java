package nl.uscki.appcki.android.generated.poll;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PollOption {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("textcolor")
    @Expose
    private String textcolor;
    @SerializedName("voteCount")
    @Expose
    private Integer voteCount;

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
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The color
     */
    public String getColor() {
        return color;
    }

    /**
     *
     * @param color
     * The color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     *
     * @return
     * The textcolor
     */
    public String getTextcolor() {
        return textcolor;
    }

    /**
     *
     * @param textcolor
     * The textcolor
     */
    public void setTextcolor(String textcolor) {
        this.textcolor = textcolor;
    }

    /**
     *
     * @return
     * The voteCount
     */
    public Integer getVoteCount() {
        return voteCount;
    }

    /**
     *
     * @param voteCount
     * The voteCount
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}