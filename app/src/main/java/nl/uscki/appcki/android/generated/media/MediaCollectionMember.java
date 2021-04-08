package nl.uscki.appcki.android.generated.media;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public abstract class MediaCollectionMember implements IWilsonBaseItem, Parcelable {

    @Expose
    protected Integer id;

    public MediaCollectionMember() {

    }

    protected MediaCollectionMember(Parcel in) {
        this.id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    abstract DateTime getDateAdded();

    @Nullable
    abstract MediaCollection getParentCollection();
}
