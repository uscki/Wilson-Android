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

    @Expose
    Post lastRead;

    @Expose
    Post lastPost;

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

    public boolean isRead() {
        return lastPost == null || (lastRead != null && (lastPost.getOriginal_post_time().isBefore(lastRead.getPost_time()) || lastPost.getOriginal_post_time().equals(lastRead.getPost_time())));
    }

    public boolean isRead(Post post) {
        return lastRead != null && (post.getPost_time().isBefore(lastRead.getOriginal_post_time()) || post.getPost_time().equals(lastRead.getOriginal_post_time()));
    }

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

    public Post getLastRead() {
        return lastRead;
    }

    public Post getLastPost() {
        return lastPost;
    }

    /**
     * WARNING: This call only changes the object temporarily until the next API refresh.
     * To make the last read post persistent, make sure to use the ForumAPI.
     * @param lastRead  Last read post
     */
    public void setLastRead(Post lastRead) {
        this.lastRead = lastRead;
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
