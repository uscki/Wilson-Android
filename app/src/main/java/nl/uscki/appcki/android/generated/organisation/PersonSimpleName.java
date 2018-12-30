package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 5-12-16.
 */

public class PersonSimpleName implements IWilsonBaseItem {
    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("nickname")
    private String nickname;

    @Expose
    @SerializedName("postalname")
    private String postalname;

    @Expose
    @SerializedName("photomediaid")
    private Integer photomediaid;

    @Expose
    @SerializedName("displayonline")
    private Boolean displayonline;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return displayonline != null && displayonline;
    }

    public void setDisplayonline(Boolean displayonline) {
        this.displayonline = displayonline;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PersonSimpleName) {
            return ((PersonSimpleName) o).getId().equals(getId());
        }
        return false;
    }
}
