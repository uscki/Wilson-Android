package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 5-9-16.
 */

public class PersonSimple implements IWilsonBaseItem {
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
    Boolean displayonline;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPostalname() {
        return postalname;
    }

    public void setPostalname(String postalname) {
        this.postalname = postalname;
    }

    public Integer getPhotomediaid() {
        return photomediaid;
    }

    public void setPhotomediaid(Integer photomediaid) {
        this.photomediaid = photomediaid;
    }

    public Boolean getDisplayonline() {
        return displayonline;
    }

    public void setDisplayonline(Boolean displayonline) {
        this.displayonline = displayonline;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Person) {
            return ((Person)o).getId().equals(this.getId());
        } else if(o instanceof  PersonSimple) {
            return ((PersonSimple)o).getId().equals(this.getId());
        } else if (o instanceof PersonSimpleName) {
            return ((PersonSimpleName)o).getId().equals(this.getId());
        }
        return false;
    }
}
