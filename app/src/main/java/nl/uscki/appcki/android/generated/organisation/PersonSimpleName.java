package nl.uscki.appcki.android.generated.organisation;

import android.view.ViewDebug;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by peter on 5-12-16.
 */

public class PersonSimpleName {
    @Expose
    @SerializedName("id")
    Integer id;
    @Expose
    @SerializedName("nickname")
    String nickname;
    @Expose
    @SerializedName("postalname")
    String postalname;
    @Expose
    @SerializedName("photomediaid")
    Integer photomediaid;

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

    public void setPhotomediaid(int photomediaid) {
        this.photomediaid = photomediaid;
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
