
package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Person extends PersonSimple implements IWilsonBaseItem {
    @Expose
    private   Integer id;

    @Expose
    private String address1;

    @Expose
    private String address2;

    @Expose
    private Long birthdate;

    @Expose
    private String city;

    @Expose
    private String country;

    @Expose
    private boolean displayonline;

    @Expose
    private  String emailaddress;

    @Expose
    private  String firstname;

    @Expose
    private  String gender;

    @Expose
    private   String homepage;

    @Expose
    private   String lastname;

    @Expose
    private   String middlename;

    @SerializedName("mobilenumber")
    @Expose
    private   String mobilenumber;

    @Expose
    private   String nickname;

    @SerializedName("phonenumber")
    @Expose
    private   String phonenumber;

    @Expose
    private   Integer photomediaid;

    @Expose
    private   String postalname;

    @Expose
    private   String signature;

    @Expose
    private    String zipcode;

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
    public Integer getPhotomediaid() { return photomediaid; }

    @Override
    public void setPhotomediaid(Integer photomediaid) { this.photomediaid = photomediaid; }

    @Override
    public Boolean getDisplayonline() {
        return displayonline;
    }

    @Override
    public void setDisplayonline(Boolean displayonline) {
        this.displayonline = displayonline;
    }

    public String getUsername() { return null; }

    public void setUsername(String username) {  }

    @Override
    public String getFirstname() {
        return firstname;
    }

    @Override
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getMiddlename() {
        return middlename;
    }

    @Override
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    @Override
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    /* Start fields not in PersonSimpleName */

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public DateTime getBirthdate() {
        return new DateTime(birthdate);
    }

    public void setBirthdate(Long birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public void setEmailaddress(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
