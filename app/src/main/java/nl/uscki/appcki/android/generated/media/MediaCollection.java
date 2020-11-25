package nl.uscki.appcki.android.generated.media;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MediaCollection implements IWilsonBaseItem, Parcelable {
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

    protected MediaCollection(Parcel in) {
        date = in.readString();
        id = in.readInt();
        name = in.readString();
        parent = in.readParcelable(MediaCollection.class.getClassLoader());
        numOfPhotos = in.readInt();
    }

    public static final Creator<MediaCollection> CREATOR = new Creator<MediaCollection>() {
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
    public Integer getId() {
        return id;
    }

    public DateTime getDate() {
        return new DateTime(this.date);
    }

    public String getName() {
        return name;
    }

    @Nullable
    public MediaCollection getParent() {
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
        dest.writeString(this.date);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.parent, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeInt(this.numOfPhotos);
    }
}
