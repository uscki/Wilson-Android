package nl.uscki.appcki.android.fragments.adapters;

/**
 * Created by peter on 5/16/16.
 */

import android.support.v7.widget.RecyclerView;

import java.util.List;

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
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
