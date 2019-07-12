package nl.uscki.appcki.android.events;

/**
 * Updating the current user requires scheduling a JobIntentService, which requires a context.
 * The update can be initiated at any time when the current user is requested from the UserHelper,
 * but the last cached version of that object is too old. As a context is required this job
 * should be scheduled by an object that has a context available. This event can be fired if no
 * context is directly available.
 */
public class CurrentUserUpdateRequiredDirectiveEvent {


}
