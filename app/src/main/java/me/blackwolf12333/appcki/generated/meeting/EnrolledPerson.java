package me.blackwolf12333.appcki.generated.meeting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

import me.blackwolf12333.appcki.generated.media.MediaFile;

public class EnrolledPerson {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("photomediaid")
    @Expose
    private MediaFile photomediaid;
    @SerializedName("nickname")
    @Expose
    private String nickname;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The photomediaid
     */
    public MediaFile getPhotomediaid() {
        return photomediaid;
    }

    /**
     *
     * @param photomediaid
     * The photomediaid
     */
    public void setPhotomediaid(MediaFile photomediaid) {
        this.photomediaid = photomediaid;
    }

    /**
     *
     * @return
     * The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     *
     * @param nickname
     * The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}