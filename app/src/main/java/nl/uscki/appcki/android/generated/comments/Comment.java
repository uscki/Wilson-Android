package nl.uscki.appcki.android.generated.comments;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.List;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.organisation.PersonSimple;

public class Comment implements IWilsonBaseItem {
    @Expose
    public int id;
    @Expose
    public boolean announcement;
    @Expose
    public PersonSimple person;
    @Expose
    public List<Comment> reactions;
    @Expose
    public DateTime timestamp;
    @Expose
    public List<Object> comment; // List<Object> represents BB code

    @Override
    public Integer getId() {
        return id;
    }
}
