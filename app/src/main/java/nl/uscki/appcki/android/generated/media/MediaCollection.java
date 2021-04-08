package nl.uscki.appcki.android.generated.media;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

public class MediaCollection extends MediaCollectionMember {
    @Expose
    private String date;
    @Expose
    public String name;
    @Expose
    @Nullable
    public MediaCollection parent;
    @Expose
    public Integer numOfPhotos;

    protected MediaCollection(Parcel in) {
        super(in);
        date = in.readString();
        name = in.readString();
        parent = in.readParcelable(MediaCollection.class.getClassLoader());
        numOfPhotos = in.readInt();
    }

    public static final Parcelable.Creator<MediaCollection> CREATOR = new Creator<MediaCollection>() {
        @Override
        public MediaCollection createFromParcel(Parcel in) {
            return new MediaCollection(in);
        }

        @Override
        public MediaCollection[] newArray(int size) {
            return new MediaCollection[size];
        }
    };

    @Override
    public DateTime getDateAdded() {
        return new DateTime(this.date);
    }

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public MediaCollection getParentCollection() {
        return parent;
    }

    public Integer getNumOfPhotos() {
        return numOfPhotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.date);
        dest.writeString(this.name);
        dest.writeParcelable(this.parent, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeInt(this.numOfPhotos);
    }
}
