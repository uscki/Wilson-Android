package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import butterknife.OnItemClick;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 5-9-16.
 */

public class PersonSimple extends PersonSimpleName implements IWilsonBaseItem {
    @Expose
    private String username;

    @Expose
    private String firstname;

    @Expose
    private String middlename;

    @Expose
    private String lastname;

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getFirstname() { return firstname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getMiddlename() { return middlename; }

    public void setMiddlename(String middlename) { this.middlename = middlename; }

    public String getLastname() { return lastname; }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
