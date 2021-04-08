package nl.uscki.appcki.android.generated.media;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class MediaTag implements IWilsonBaseItem, Parcelable {

    public MediaTag() {

    }

    public MediaTag(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public static final Creator<MediaTag> CREATOR = new Creator<MediaTag>() {
        @Override
        public MediaTag createFromParcel(Parcel in) {
            return new MediaTag(in);
        }

        @Override
        public MediaTag[] newArray(int size) {
            return new MediaTag[size];
        }
    };
}
