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
    private Integer id;
    @Expose
    private String username;
    @Expose
    private String firstname;
    @Expose
    private String middlename;
    @Expose
    private String lastname;
    @Expose
    private String nickname;
    @Expose
    private String postalname;
    @Expose
    private Integer photomediaid;
    @Expose
    @SerializedName("displayonline")
    private Boolean displayonline;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getPostalname() {
        return postalname;
    }

    @Override
    public void setPostalname(String postalname) {
        this.postalname = postalname;
    }

    @Override
    public Integer getPhotomediaid() {
        return photomediaid;
    }

    @Override
    public void setPhotomediaid(Integer photomediaid) {
        this.photomediaid = photomediaid;
    }

    @Override
    public Boolean getDisplayonline() {
        return displayonline;
    }

    @Override
    public void setDisplayonline(Boolean displayonline) {
        this.displayonline = displayonline;
    }

    /* Start fields not in PersonSimpleName */

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
