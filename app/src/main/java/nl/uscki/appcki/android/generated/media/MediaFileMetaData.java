package nl.uscki.appcki.android.generated.media;

import com.google.gson.annotations.Expose;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MediaFileMetaData implements IWilsonBaseItem {

    @Expose
    private Integer id;

    @Expose
    private long added;

    @Expose
    private boolean allPersonsTagged;

    @Expose
    private MediaCollection collection;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public boolean isAllPersonsTagged() {
        return allPersonsTagged;
    }

    public void setAllPersonsTagged(boolean allPersonsTagged) {
        this.allPersonsTagged = allPersonsTagged;
    }

    public MediaCollection getCollection() {
        return collection;
    }

    public void setCollection(MediaCollection collection) {
        this.collection = collection;
    }
}
