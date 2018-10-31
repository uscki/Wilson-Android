package nl.uscki.appcki.android.events;

public class PrivacyPolicyPreferenceChangedEvent {
    public final boolean hasAgreedToGeneralTerms;
    public final boolean hasAgreedToAppTerms;
    public final boolean hasAgreedToNotificationTerms;

    public PrivacyPolicyPreferenceChangedEvent(
            boolean hasAgreedToGeneralTerms,
            boolean hasAgreedToAppTerms,
            boolean hasAgreedToNotificationTerms
    ) {
        this.hasAgreedToGeneralTerms = hasAgreedToGeneralTerms;
        this.hasAgreedToAppTerms = hasAgreedToAppTerms;
        this.hasAgreedToNotificationTerms = hasAgreedToNotificationTerms;
    }
}
