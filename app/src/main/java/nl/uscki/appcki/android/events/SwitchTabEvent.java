package nl.uscki.appcki.android.events;

import android.widget.Switch;

/**
 * Created by peter on 5/29/16.
 */
public class SwitchTabEvent {
    public int index;
    public int itemIdToScrollTo;

    public SwitchTabEvent(int index) {
        this.index = index;
        this.itemIdToScrollTo = -1;
    }

    public SwitchTabEvent(int index, int scrollToId) {
        this.index = index;
        this.itemIdToScrollTo = scrollToId;
    }
}
