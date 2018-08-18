package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 3/4/17.
 */

public class Committee  implements IWilsonBaseItem {
    @Expose
    @SerializedName("committeeId")
    private Integer id;
    @Expose
    @SerializedName("start")
    Long start;
    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("end")
    Long end;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DateTime getStart() {
        return new DateTime(start);
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getEnd() {
        return new DateTime(end);
    }

    public void setEnd(Long end) {
        this.end = end;
    }
}
