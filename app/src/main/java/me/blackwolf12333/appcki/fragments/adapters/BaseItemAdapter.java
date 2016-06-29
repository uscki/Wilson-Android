package me.blackwolf12333.appcki.fragments.adapters;

/**
 * Created by peter on 5/16/16.
 */

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class BaseItemAdapter<T extends RecyclerView.ViewHolder, K> extends RecyclerView.Adapter<T> {
    protected List<K> items;

    public BaseItemAdapter(List<K> items) {
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

    public abstract Integer getLastID();
}
