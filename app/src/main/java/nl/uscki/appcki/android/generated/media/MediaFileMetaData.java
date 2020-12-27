package nl.uscki.appcki.android.generated.media;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class MediaFileMetaData extends MediaCollectionMember {

    public MediaFileMetaData() {

    }

    public MediaFileMetaData(Parcel in) {
        super(in);
        this.added = DateTime.parse(in.readString());
        this.allPersonsTagged = in.readInt() == 1;
        this.tags = new ArrayList<>();
        in.readList(this.tags, MediaTag.class.getClassLoader());
        this.collection = in.readParcelable(MediaCollection.class.getClassLoader());
    }

    @Expose
    private DateTime added;

    @Expose
    private boolean allPersonsTagged;

    @Expose
    private MediaCollection collection;

    @Expose
    public List<MediaTag> tags;

    public boolean isAllPersonsTagged() {
        return allPersonsTagged;
    }

    public void setAllPersonsTagged(boolean allPersonsTagged) {
        this.allPersonsTagged = allPersonsTagged;
    }

    @Override
    DateTime getDateAdded() {
        return this.added;
    }

    @Override
    public MediaCollection getParentCollection() {
        return collection;
    }

    public List<MediaTag> getTags() {
        return tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.added.toString());
        dest.writeInt(this.allPersonsTagged ? 1 : 0);
        dest.writeList(this.tags);
        dest.writeParcelable(this.collection, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Creator<MediaFileMetaData> CREATOR = new Creator<MediaFileMetaData>() {
        @Override
        public MediaFileMetaData createFromParcel(Parcel in) {
            return new MediaFileMetaData(in);
        }

        @Override
        public MediaFileMetaData[] newArray(int size) {
            return new MediaFileMetaData[size];
        }
    };

    @Override
    public String toString() {
        return "MediaFileMetaData{" +
                "id=" + id +
                ", collection=" + collection +
                ", tags=" + tags.size() +
                '}';
    }
}
