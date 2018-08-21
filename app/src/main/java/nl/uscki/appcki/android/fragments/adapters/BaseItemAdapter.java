package nl.uscki.appcki.android.fragments.adapters;

/**
 * Created by peter on 5/16/16.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;
import java.util.ListIterator;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public abstract class BaseItemAdapter<T extends RecyclerView.ViewHolder, K extends IWilsonBaseItem> extends RecyclerView.Adapter<T> {
    protected List<K> items;

    protected BaseItemAdapter(List<K> items) {
        this.items = items;
    }

    public void update(List<K> items) {
        this.items.clear();
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    public void addItems(List<K> items) {
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    public void insert(int position, K item) {
        this.items.add(position, item);
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.items.clear();
        this.notifyDataSetChanged();
    }

    public List<K> getItems() {
        return items;
    }

    public int getItemPosition(int id) {
        ListIterator<K> iterator = getItems().listIterator();

        while(iterator.hasNext())
        {
            if(iterator.next().getId().equals(id))
            {
                // The previous index is actually the one we compared, as .next() returns the object
                // and then advances the cursor.
                return iterator.previousIndex();
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
