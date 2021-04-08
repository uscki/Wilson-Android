package nl.uscki.appcki.android.generated.forum;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
