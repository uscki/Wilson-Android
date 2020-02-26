package nl.uscki.appcki.android.generated.media;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MediaCollection implements IWilsonBaseItem{
    @Expose
    private String date;
    @Expose
    public Integer id;
    @Expose
    public String name;
    @Expose
    @Nullable
    public MediaCollection parent;
    @Expose
    public Integer numOfPhotos;

    @Override
    public Integer getId() {
        return id;
    }

    public DateTime getDate() {
        return new DateTime(this.date);
    }
}
