package nl.uscki.appcki.android.generated.forum;

import android.os.Parcel;

import com.google.gson.annotations.Expose;

public class RecentTopic extends Topic {

    @Expose
    private Forum forum;

    protected RecentTopic(Parcel in) {
        super(in);
        forum = null;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
}
