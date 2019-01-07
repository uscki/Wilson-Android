package nl.uscki.appcki.android.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Slot  implements IWilsonBaseItem {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("starttime")
    @Expose
    private String starttime;
    @SerializedName("preferences")
    @Expose
    private List<Preference> preferences = new ArrayList<>();

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
     * The starttime
     */
    public DateTime getStarttime() {
        return new DateTime(starttime);
    }

    /**
     *
     * @param starttime
     * The starttime
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    /**
     *
     * @return
     * The preferences
     */
    public List<Preference> getPreferences() {
        return preferences;
    }

    /**
     *
     * @param preferences
     * The preferences
     */
    public void setPreferences(List<Preference> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}