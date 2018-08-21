package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;

/**
 * Created by peter on 1/25/16.
 *
 * @Depricated No longer used since shouts are no longer added through a dialog and now
 * have direct access to the list of shout items
 */
@Deprecated
public class RoephoekEvent {
    public RoephoekItem roephoek;

    public RoephoekEvent(RoephoekItem roephoek) {
        this.roephoek = roephoek;
    }
}
