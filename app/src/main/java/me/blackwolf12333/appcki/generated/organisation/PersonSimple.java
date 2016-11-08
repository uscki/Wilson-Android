package me.blackwolf12333.appcki.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by peter on 9/4/16.
 */
public class PersonSimple {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("photomediaid")
    @Expose
    private Integer photomediaid;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("middlename")
    @Expose
    private String middlename;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("postalname")
    @Expose
    private String postalname;

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

    public Integer getPhotomediaid() {
        return photomediaid;
    }

    public void setPhotomediaid(Integer photomediaid) {
        this.photomediaid = photomediaid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getPostalname() {
        return postalname;
    }

    public void setPostalname(String postalname) {
        this.postalname = postalname;
    }

    public String getName() {
        if (middlename != null) {
            return getFirstname() + " " + getMiddlename() + " " + getLastname();
        } else {
            return getFirstname() + " " + getLastname();
        }
    }
}
