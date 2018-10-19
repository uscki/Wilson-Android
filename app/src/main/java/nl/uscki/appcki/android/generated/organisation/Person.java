
package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Person extends PersonSimple implements IWilsonBaseItem {

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
    private String emailaddress;

    @Expose
    private String gender;

    @Expose
    private String homepage;

    @SerializedName("mobilenumber")
    @Expose
    private   String mobilenumber;

    @SerializedName("phonenumber")
    @Expose
    private   String phonenumber;

    @Expose
    private   String signature;

    @Expose
    private    String zipcode;

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

    public Integer getAge() {
        int age = 42;
        DateTime birthdate = getBirthdate();
        DateTime now = new DateTime();
        if(birthdate != null) {
            age = now.getYear() - birthdate.getYear();
            now = now.year().setCopy(birthdate.getYear());
            if(now.isAfter(birthdate)) {
                age++;
            }
        }
        return age;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
