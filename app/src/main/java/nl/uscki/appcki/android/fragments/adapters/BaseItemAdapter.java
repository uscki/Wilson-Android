package nl.uscki.appcki.android.fragments.adapters;

/**
 * Created by peter on 5/16/16.
 */

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.LoadingMoreItem;

public abstract class BaseItemAdapter<T extends RecyclerView.ViewHolder, K extends IWilsonBaseItem> extends RecyclerView.Adapter<T> {
    private int loadingMoreIndex = -1;

    protected List<K> items;

    protected BaseItemAdapter(List<K> items) {
        this.items = items;
    }

    public void update(List<K> items) {
        this.items.clear();
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    public void add(K item) {
        this.items.add(item);
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
            if(items.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }

    public void showLoadingMoreItems(boolean show) {
        if(show && loadingMoreIndex < 0) {
            LoadingMoreItem item = new LoadingMoreItem();
            this.loadingMoreIndex = this.items.size();
            this.items.add((K) item);
            this.notifyDataSetChanged();
        } else {
            try {
                if (loadingMoreIndex >= 0 && this.items.get(loadingMoreIndex) instanceof LoadingMoreItem) {
                    this.items.remove(loadingMoreIndex);
                    this.notifyDataSetChanged();
                }
            } catch(IndexOutOfBoundsException e) {
                Log.d(getClass().getSimpleName(), "Loading-more-items spinner already removed earlier");
            } finally {
                loadingMoreIndex = -1;
            }
        }
    }

    public abstract T onCreateCustomViewHolder(ViewGroup parent);
    public abstract void onBindCustomViewHolder(T holder, int position);


    public void onBindCustomViewHolder(T holder, int position, List<Object> payloads) {
        // Override this where necessary
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1) {
            // Loading more placeholder
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_loading_bottom, parent, false);
            return (T) new LoadingMoreViewHolder(view);
        } else {
            return onCreateCustomViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        if(holder instanceof BaseItemAdapter.LoadingMoreViewHolder) {
            Log.e(getClass().getSimpleName(), "OnBindViewHolder!");
        } else {
            onBindCustomViewHolder(holder, position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if(!(holder instanceof BaseItemAdapter.LoadingMoreViewHolder)) {
            onBindCustomViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final K item = items.get(position);
        if(item instanceof LoadingMoreItem) {
            return 1;
        } else {
            return 0;
        }
    }

    public class LoadingMoreViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.loadMoreProgressBar)
        ProgressBar progressBar;

        public LoadingMoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }

        public void showLoadingMore(boolean visible) {
            progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
