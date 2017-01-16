package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;

/**
 * Created by peter on 1/25/16.
 */
public class RoephoekEvent {
    public RoephoekItem roephoek;

    public RoephoekEvent(RoephoekItem roephoek) {
        this.roephoek = roephoek;
    }
}
