package nl.uscki.appcki.android.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MyPreference  implements IWilsonBaseItem {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("slotId")
    @Expose
    private Integer slotId;
    @SerializedName("starttime")
    @Expose
    private DateTime starttime;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("canattend")
    @Expose
    private Boolean canattend;

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
     * The slotId
     */
    public Integer getSlotId() {
        return slotId;
    }

    /**
     *
     * @param slotId
     * The slotId
     */
    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    /**
     *
     * @return
     * The starttime
     */
    public DateTime getStarttime() {
        return starttime;
    }

    /**
     *
     * @param starttime
     * The starttime
     */
    public void setStarttime(DateTime starttime) {
        this.starttime = starttime;
    }

    /**
     *
     * @return
     * The notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     *
     * @param notes
     * The notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     *
     * @return
     * The canattend
     */
    public Boolean getCanattend() {
        return canattend;
    }

    /**
     *
     * @param canattend
     * The canattend
     */
    public void setCanattend(Boolean canattend) {
        this.canattend = canattend;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}