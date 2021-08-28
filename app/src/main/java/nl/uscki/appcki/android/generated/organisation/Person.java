
package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.util.Locale;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * This class corresponds to nl.uscki.api.web.rest.beans.organization.PersonSimpleBean in the
 * B.A.D.W.O.L.F. API
 */
public class Person extends PersonName implements IWilsonBaseItem {

    @Expose
    private String firstname;

    @Expose
    private String middlename;

    @Expose
    private String lastname;

    @Expose
    private String address1;

    @Expose
    private String address2;

    @Expose
    private    String zipcode;

    @Expose
    private String city;

    @Expose
    private String country;

    @SerializedName("phonenumber")
    @Expose
    private   String phonenumber;

    @SerializedName("mobilenumber")
    @Expose
    private   String mobilenumber;

    @Expose
    private String gender;

    @Expose
    private String emailaddress;

    @Expose
    private String homepage;

    @Expose
    private   String signature;

    @Expose
    private String birthdate;

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

    public void setBirthdate(String birthdate) {
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
            if(now.isBefore(birthdate)) {
                age--;
            }
        }
        return age;
    }

    public String getBirthdayWidthAge() {
        return String.format(
                Locale.getDefault(),
                "(%s) (%d)",
                getBirthdate().toString("dd-MM-yyyy"),
                getAge()
        );
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getFullAddres(boolean includeCountry) {
        StringBuilder b = new StringBuilder();

        boolean addNewline = false;

        if(address1 != null && address1.trim().length() != 0) {
            b.append(address1);
            addNewline = true;
        }

        if(address2 != null && address2.trim().length() != 0) {
            if(addNewline) b.append("\n");
            b.append(address2);
            addNewline = true;
        }

        if(zipcode != null && zipcode.trim().length() != 0) {
            if(addNewline) b.append("\n");
            b.append(zipcode);
            addNewline = true;

            if(city != null && city.trim().length() != 0) {
                b.append(", ");
                b.append(city);
            }
        } else if(city != null && city.trim().length() != 0) {
            b.append(city);
            addNewline = true;
        }

        if(includeCountry && country != null && country.trim().length() != 0) {
            if(addNewline) b.append("\n");
            b.append(country);
        }

        return b.toString();
    }
}
