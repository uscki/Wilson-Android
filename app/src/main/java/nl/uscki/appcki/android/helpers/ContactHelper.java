package nl.uscki.appcki.android.helpers;

import android.content.ContentProviderOperation;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

import nl.uscki.appcki.android.App;
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

    public void exportContact(Person person) {
        if(!exportContactViaProvider(person)) exportContactViaIntent(person);
    }

    private boolean exportContactViaProvider(Person person) {
        if(!PermissionHelper.canExportContact() || !person.getDisplayonline())
            return false;

        ArrayList<ContentProviderOperation> options =
                new ArrayList<ContentProviderOperation>();

        options.add(
                ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        );

        options.add(
                getBasicContentProviderOperationBuilder()
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        person.getFirstname())
                .build()
        );

        if(preferences.getBoolean("people_export_field_fullname", true)) {
            if(person.getMiddlename() != null && !person.getMiddlename().isEmpty()) {
                options.add(
                        getBasicContentProviderOperationBuilder()
                                .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, person.getMiddlename())
                                .build()
                );
            }

            options.add(
                    getBasicContentProviderOperationBuilder()
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, person.getLastname())
                    .build()
            );

            options.add(
                    getBasicContentProviderOperationBuilder()
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            getContactDisplayName(person))
                    .build()
            );
        }

        // TODO: Nickname not a StructuredName field?

        if(preferences.getBoolean("people_export_field_mobile", true) &&
                person.getMobilenumber() != null && !person.getMobilenumber().isEmpty()) {
            options.add(
                    getBasicContentProviderOperationBuilder()
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                            person.getMobilenumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build()
            );
        }

        if(preferences.getBoolean("people_export_field_phone", true) &&
                person.getPhonenumber() != null && !person.getPhonenumber().isEmpty()) {
            options.add(
                    getBasicContentProviderOperationBuilder()
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    person.getPhonenumber())
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                            .build()
            );
        }

        if(preferences.getBoolean("people_export_field_email", true) &&
                person.getEmailaddress() != null && !person.getEmailaddress().isEmpty()) {
            options.add(
                    getBasicContentProviderOperationBuilder()
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, person.getEmailaddress())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                    .build()
            );
        }

        if(preferences.getBoolean("people_export_field_address", true)) {

            ContentProviderOperation.Builder addressContentBuilder =
                    getBasicContentProviderOperationBuilder()
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                            getContactAddress(person));

            if(person.getAddress1() != null && !person.getAddress1().isEmpty()) {
                addressContentBuilder
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                                person.getCountry());
            }

            // TODO: Is there a field for address line 2? Region? Neighbourhood?

            if(person.getZipcode() != null && !person.getZipcode().isEmpty()) {
                addressContentBuilder
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                                person.getZipcode());
            }

            if(person.getCity() != null && !person.getCity().isEmpty()) {
                addressContentBuilder
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                                person.getCity());
            }

            if(person.getCountry() != null && !person.getCountry().isEmpty()) {
                addressContentBuilder
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                                person.getCountry());
            }

            options.add(addressContentBuilder.build());
        }

        if(preferences.getBoolean("people_export_field_website", true) &&
                person.getHomepage() != null && !person.getHomepage().isEmpty()) {
            options.add(
                    getBasicContentProviderOperationBuilder()
                    .withValue(ContactsContract.CommonDataKinds.Website.URL, person.getHomepage())
                    .withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE)
                    .build()
            );
        }

        if(preferences.getBoolean("people_export_field_organisation", false)) {
            // Set USCKI Incognito as organisation
            options.add(
                    getBasicContentProviderOperationBuilder()
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY,
                            "U.S.C.K.I Incognito")
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE,
                            ContactsContract.CommonDataKinds.Organization.TYPE_OTHER)
                    .build()
            );
        }

        try {
            App.getContext().getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, options
            );
            return true;
        } catch (Exception e) {
            Log.e("ContactsProviderExport", e.getStackTrace().toString());
            return false;
        }
    }

    private ContentProviderOperation.Builder getBasicContentProviderOperationBuilder() {
        return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
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

    private void exportContactViaIntent(Person person) {

    }
}
