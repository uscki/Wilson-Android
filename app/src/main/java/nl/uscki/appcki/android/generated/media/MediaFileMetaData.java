package nl.uscki.appcki.android.generated.media;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MediaFileMetaData implements IWilsonBaseItem {

    @Expose
    private Integer id;

    @Expose
    private String added;

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

    public DateTime getAdded() {
        return new DateTime(added);
    }

    public void setAdded(String added) {
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
