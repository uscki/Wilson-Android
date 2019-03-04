package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class DetailItemUpdatedEvent<T extends IWilsonBaseItem> {

    private T item;

    public DetailItemUpdatedEvent(T item) {
        this.item = item;
    }

    public T getUpdatedItem() {
        return item;
    }

    /**
     * Test if this event relates to a certain item type monitored by the event bus
     * @param target    Class of target type that is monitored for changes
     * @return          Boolean indicating if this event relates to type target
     */
    public boolean isOfType(Class<? extends IWilsonBaseItem> target) {
        return item.getClass().equals(target);
    }

}
