package nl.uscki.appcki.android.generated.forum;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Topic implements IWilsonBaseItem, Parcelable {

    @Expose
    Integer id;

    @Expose
    boolean locked;

    @Expose
    DateTime posted;

    @Expose
    boolean sticky;

    @Expose
    String title;

    @Expose
    Integer views;

    protected Topic(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        locked = in.readByte() != 0;
        posted = new DateTime(in.readLong());
        sticky = in.readByte() != 0;
        title = in.readString();
        if (in.readByte() == 0) {
            views = null;
        } else {
            views = in.readInt();
        }
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    @Override
    public Integer getId() {
        return this.id;
    }

    public boolean isLocked() {
        return locked;
    }

    public DateTime getPosted() {
        return posted;
    }

    public boolean isSticky() {
        return sticky;
    }

    public String getTitle() {
        return title;
    }

    public Integer getViews() {
        return views;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeBoolean(this.locked);
        dest.writeLong(this.posted.getMillis());
        dest.writeBoolean(this.sticky);
        dest.writeString(title);
        dest.writeInt(this.views);
    }
}
