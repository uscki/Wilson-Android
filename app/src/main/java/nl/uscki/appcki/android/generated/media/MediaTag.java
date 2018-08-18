package nl.uscki.appcki.android.generated.media;

import com.google.gson.annotations.Expose;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MediaTag implements IWilsonBaseItem {
    @Expose
    private Integer id;

    @Expose
    private String name;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
