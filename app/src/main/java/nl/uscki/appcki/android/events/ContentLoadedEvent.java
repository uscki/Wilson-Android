package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.fragments.PageableFragment;

/**
 * Event to notify the system new items have been loaded in a PageableFragment
 */
public class ContentLoadedEvent {
    public PageableFragment updatedPageableFragment;

    public ContentLoadedEvent(PageableFragment updatedPageableFragment) {
        this.updatedPageableFragment = updatedPageableFragment;
    }
}
