package nl.uscki.appcki.android.generated.media;

import android.support.annotation.Nullable;
import com.google.gson.annotations.Expose;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MediaCollection implements IWilsonBaseItem{
    @Expose
    public Long date;
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
}
