package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.roephoek.Roephoek;

/**
 * Created by peter on 1/25/16.
 */
public class RoephoekOlderEvent {
    public Roephoek roephoek;

    public RoephoekOlderEvent(Roephoek roephoek) {
        this.roephoek = roephoek;
    }
}
