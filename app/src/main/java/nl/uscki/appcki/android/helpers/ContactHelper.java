package nl.uscki.appcki.android.helpers;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.organisation.Person;

public class ContactHelper {

    private static ContactHelper instance;
    SharedPreferences preferences;

    private ContactHelper() {
        preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static ContactHelper getInstance() {
        if(instance == null)
            instance = new ContactHelper();
        return instance;
    }

    public String getContactDisplayName(Person person) {
        StringBuilder displayName = new StringBuilder();
        displayName.append(person.getFirstname());
        if(preferences.getBoolean("people_export_field_fullname", false)) {
            // Set full contact name as display name
            if(person.getMiddlename() != null && !person.getMiddlename().isEmpty()) {
                displayName.append(" ");
                displayName.append(person.getMiddlename().toLowerCase());
                displayName.append(" ");
            }
            displayName.append(person.getLastname());
        }
        return displayName.toString();
    }

    public String getContactAddress(Person person) {
        StringBuilder address = new StringBuilder();
        boolean addNewline = false;
        boolean addSpace = false;
        if(person.getAddress1() != null && !person.getAddress1().isEmpty()) {
            address.append(person.getAddress1());
            addNewline = true;
        }
        if(person.getAddress2() != null && !person.getAddress2().isEmpty()) {
            if(addNewline)
                address.append("\n");
            address.append(person.getAddress2());
            addNewline = true;
        }
        if(person.getCity() != null && !person.getCity().isEmpty()) {
            if(addNewline)
                address.append("\n");
            address.append(person.getCity());
            addSpace = true;
        }
        if(person.getZipcode() != null && !person.getZipcode().isEmpty()) {
            if(addSpace) {
                address.append(" ");
            } else if(addNewline) {
                address.append("\n");
            }
            address.append(person.getZipcode());
        }
        if(person.getCountry() != null && !person.getCountry().isEmpty()) {
            if(addNewline)
                address.append("\n");
            address.append(person.getCountry());
        }

        return address.toString();
    }

    public void exportContactViaIntent(Person person) {

        // Check if this contact can be exported
        if(!person.getDisplayonline()) {
            Toast.makeText(
                    App.getContext(),
                    App.getContext().getResources()
                            .getString(R.string.person_not_display_online_msg),
                    Toast.LENGTH_SHORT)
                    .show();

            return;
        }

        // Prepare add contact intention
        Intent addContactIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        addContactIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        addContactIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addContactIntent.putExtra(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        addContactIntent.putExtra(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);
        addContactIntent.putExtra("finishActivityOnSaveCompleted", true);

        // ArrayList will contain the content values for this intention
        ArrayList<ContentValues> contentValues = new ArrayList<>();

        // Name format depends on preferences
        // For some reason, structured name can't be added using contact contracts
        if(preferences.getBoolean("people_export_field_fullname", true)) {
            addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, person.getPostalname());
        } else {
            addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, person.getFirstname());
        }

        // Check if nickname is to be exported according to preferences
        if(preferences.getBoolean("people_export_nickname", true)) {
            addNickNameToValues(person, contentValues);
        }

        addMobileToValues(person, contentValues);
        addHomePhoneToValues(person, contentValues);
        addAddressToValues(person, contentValues);
        addEmailToValues(person, contentValues);
        addHomepageToValues(person, contentValues);

        // Add category
        if(preferences.getBoolean("people_export_field_organisation", false)) {
            addContactIntent.putExtra(
                    ContactsContract.Intents.Insert.COMPANY,
                    "U.S.C.K.I. Incognito");
        }

        // Add content values as data
        addContactIntent.putParcelableArrayListExtra(
                ContactsContract.Intents.Insert.DATA,
                contentValues);

        // Start the activity
        App.getContext().startActivity(addContactIntent);
    }

    /**
     * Add nickname to set of content values, if the field has a value on a person object
     * @param person        Person object, possibly containing nickname
     * @param values        ArrayList of content values
     */
    private void addNickNameToValues(Person person, ArrayList<ContentValues> values) {
        if(person.getNickname() != null && !person.getNickname().isEmpty()) {
            ContentValues nickname = new ContentValues();
            nickname.put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
            nickname.put(
                    ContactsContract.CommonDataKinds.Nickname.NAME,
                    person.getNickname());
            values.add(nickname);
        }
    }

    /**
     * Added the contact mobile phone number to the array of content values, if it has a value
     * @param person        Person object, possibly containing mobile phone number
     * @param values        ArrayList of content values
     */
    private void addMobileToValues(Person person, ArrayList<ContentValues> values) {
        // Add phone numbers
        if (personFieldNotEmpty(person.getMobilenumber())) {
            ContentValues mobile = new ContentValues();
            mobile.put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            mobile.put(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    person.getMobilenumber());
            mobile.put(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            values.add(mobile);
        }

    }

    /**
     * Added the contact home phone number to the array of content values, if it has a value
     * @param person        Person object, possibly containing home phone number
     * @param values        ArrayList of content values
     */
    private void addHomePhoneToValues(Person person, ArrayList<ContentValues> values) {
        // Add home phone
        if (personFieldNotEmpty(person.getPhonenumber())) {
            ContentValues homePhone = new ContentValues();
            homePhone.put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            homePhone.put(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    person.getPhonenumber());
            homePhone.put(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            values.add(homePhone);
        }
    }

    /**
     * Added the contact email address to the array of content values, if it has a value
     * @param person        Person object, possibly containing email address
     * @param values        ArrayList of content values
     */
    private void addEmailToValues(Person person, ArrayList<ContentValues> values) {
        // Add e-mail address
        if (personFieldNotEmpty(person.getEmailaddress())) {
            ContentValues email = new ContentValues();
            email.put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            email.put(
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    person.getEmailaddress());
            email.put(
                    ContactsContract.CommonDataKinds.Email.TYPE,
                    ContactsContract.CommonDataKinds.Email.TYPE_HOME);
            values.add(email);
        }
    }

    /**
     * Added the contact home address to the array of content values, if it has a value
     *
     * This method uses the data contract for contacts and sets both the formatted address field
     * (which is a summary to all possible address fields, structured in a certain manner), and the
     * separate fields available. Some devices only look at the formatted address, in which case
     * the separate fields are ignored, and vice versa.
     *
     * @param person        Person object, possibly containing home address
     * @param values        ArrayList of content values
     */
    private void addAddressToValues(Person person, ArrayList<ContentValues> values) {
        // Add postal address
        ContentValues homeAddress = new ContentValues();
        homeAddress.put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);

        // Add all fields of the structured postal separately
        if(personFieldNotEmpty(person.getCountry()))
            homeAddress.put(
                    ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                    person.getCountry());

        if(personFieldNotEmpty(person.getCity()))
            homeAddress.put(
                    ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                    person.getCity());

        if(personFieldNotEmpty(person.getAddress1()))
            homeAddress.put(
                    ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                    person.getAddress1());

        if(personFieldNotEmpty(person.getZipcode()))
            homeAddress.put(
                    ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                    person.getZipcode());

        // Also add a formatted address for devices that do not use the separate fields.
        // This does not seem to clash on devices that do support it
        homeAddress.put(
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                getContactAddress(person));

        // Home address is always of type home
        homeAddress.put(
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);

        values.add(homeAddress);
    }

    /**
     * Added the contact home page to the array of content values, if it has a value
     * @param person        Person object, possibly containing homepage
     * @param values        ArrayList of content values
     */
    private void addHomepageToValues(Person person, ArrayList<ContentValues> values) {
        if(person.getHomepage() != null && !person.getHomepage().isEmpty()) {
            ContentValues website = new ContentValues();
            website.put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            website.put(
                    ContactsContract.CommonDataKinds.Website.TYPE,
                    ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE);
            website.put(
                    ContactsContract.CommonDataKinds.Website.URL,
                    person.getHomepage());
            values.add(website);
        }
    }

    /**
     * Check if a field on a person object is instantiated and, if so, if it is non empty
     * @param field     The field to check for nonemptiness
     * @return          True iff field has a value
     */
    private boolean personFieldNotEmpty(String field) {
        return field != null && !field.isEmpty();
    }
}