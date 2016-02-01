
package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class Person {

    @Expose
    private Integer id;
    @Expose
    private String username;
    @Expose
    private Long expires;
    @Expose
    private String firstname;
    @Expose
    private String middlename;
    @Expose
    private String lastname;
    @Expose
    private String firstletters;
    @Expose
    private String address2;
    @Expose
    private String gender;
    @Expose
    private String signature;
    @Expose
    private Boolean displayonline;
    @Expose
    private String nickname;
    @Expose
    private Integer photomediaid;
    @Expose
    private String fbname;
    @Expose
    private List<String> roles = new ArrayList<String>();

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     *     The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return
     *     The expires
     */
    public Long getExpires() {
        return expires;
    }

    /**
     * 
     * @param expires
     *     The expires
     */
    public void setExpires(Long expires) {
        this.expires = expires;
    }

    /**
     * 
     * @return
     *     The firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * 
     * @param firstname
     *     The firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * 
     * @return
     *     The middlename
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * 
     * @param middlename
     *     The middlename
     */
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    /**
     * 
     * @return
     *     The lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * 
     * @param lastname
     *     The lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * 
     * @return
     *     The firstletters
     */
    public String getFirstletters() {
        return firstletters;
    }

    /**
     * 
     * @param firstletters
     *     The firstletters
     */
    public void setFirstletters(String firstletters) {
        this.firstletters = firstletters;
    }

    /**
     * 
     * @return
     *     The address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * 
     * @param address2
     *     The address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * 
     * @return
     *     The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * 
     * @param gender
     *     The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 
     * @return
     *     The signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * 
     * @param signature
     *     The signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * 
     * @return
     *     The displayonline
     */
    public Boolean getDisplayonline() {
        return displayonline;
    }

    /**
     * 
     * @param displayonline
     *     The displayonline
     */
    public void setDisplayonline(Boolean displayonline) {
        this.displayonline = displayonline;
    }

    /**
     * 
     * @return
     *     The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 
     * @param nickname
     *     The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 
     * @return
     *     The photomediaid
     */
    public Integer getPhotomediaid() {
        return photomediaid;
    }

    /**
     * 
     * @param photomediaid
     *     The photomediaid
     */
    public void setPhotomediaid(Integer photomediaid) {
        this.photomediaid = photomediaid;
    }

    /**
     * 
     * @return
     *     The fbname
     */
    public String getFbname() {
        return fbname;
    }

    /**
     * 
     * @param fbname
     *     The fbname
     */
    public void setFbname(String fbname) {
        this.fbname = fbname;
    }

    /**
     * 
     * @return
     *     The roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * 
     * @param roles
     *     The roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
