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
    Integer id;
    @Expose
    @SerializedName("nickname")
    String nickname;
    @Expose
    @SerializedName("postalname")
    String postalname;
    @Expose
    @SerializedName("photomediaid")
    int photomediaid;
    @Expose
    @SerializedName("displayonline")
    Boolean displayonline;

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

    public int getPhotomediaid() {
        return photomediaid;
    }

    public void setPhotomediaid(int photomediaid) {
        this.photomediaid = photomediaid;
    }

    public Boolean getDisplayonline() {
        return displayonline;
    }

    public void setDisplayonline(Boolean displayonline) {
        this.displayonline = displayonline;
    }

    public static PersonSimpleName from(PersonSimple p) {
        PersonSimpleName simple = new PersonSimpleName();
        simple.setDisplayonline(p.getDisplayonline());
        simple.setId(p.getId());
        simple.setNickname(p.getNickname());
        if (null != p.getPhotomediaid())
            simple.setPhotomediaid(p.getPhotomediaid());
        simple.setPostalname(p.getPostalname());
        return simple;
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
