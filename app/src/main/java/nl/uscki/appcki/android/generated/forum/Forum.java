package nl.uscki.appcki.android.generated.forum;

import com.google.gson.annotations.Expose;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Forum implements IWilsonBaseItem {

    @Expose
    Integer id;

    @Expose
    String name;

    @Expose
    String description;

    @Override
    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
