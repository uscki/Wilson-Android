
package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.DateTypeAdapter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

public class Person {
    @Expose
    String address1;
    @Expose
    String address2;
    @Expose
    Long birthdate;
    @Expose
    String city;
    @Expose
    String country;
    @Expose
    boolean displayonline;
    @Expose
    String emailaddress;
    @Expose
    String firstname;
    @Expose
    String gender;
    @Expose
    String homepage;
    @Expose
    Integer id;
    @Expose
    String lastname;
    @Expose
    String middlename;
    @SerializedName("mobilenumber")
    @Expose
    String mobilenumber;
    @Expose
    String nickname;
    @SerializedName("phonenumber")
    @Expose
    String phonenumber;
    @Expose
    Integer photomediaid;
    @Expose
    String postalname;
    @Expose
    String signature;
    @Expose
    String zipcode;
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
    /*public MediaFile getPhotomediaid() {
        return photomediaid;
    }*/

    /**
     * 
     * @param photomediaid
     *     The photomediaid
     */
    public void setPhotomediaid(Integer photomediaid) {
        this.photomediaid = photomediaid;
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

    public boolean isDisplayonline() {
        return displayonline;
    }

    public void setDisplayonline(boolean displayonline) {
        this.displayonline = displayonline;
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

    public String getPostalname() {
        return postalname;
    }

    public void setPostalname(String postalname) {
        this.postalname = postalname;
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
