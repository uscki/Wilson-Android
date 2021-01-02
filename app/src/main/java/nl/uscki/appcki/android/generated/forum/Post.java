package nl.uscki.appcki.android.generated.forum;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonName;

public class Post implements IWilsonBaseItem {

    @Expose
    Integer id;

    @Expose
    DateTime original_post_time;

    @Expose
    PersonName person;

    @Expose
    List<Object> post;

    @Expose
    DateTime post_time;

    @Expose
    String posterName;

    @Expose
    String signature;

    @Override
    public Integer getId() {
        return id;
    }

    public DateTime getOriginal_post_time() {
        return original_post_time;
    }

    public PersonName getPerson() {
        return person;
    }

    public List<Object> getPost() {
        return post;
    }

    public DateTime getPost_time() {
        return post_time;
    }

    public String getPosterName() {
        return posterName;
    }

    public String getSignature() {
        return signature;
    }
}
