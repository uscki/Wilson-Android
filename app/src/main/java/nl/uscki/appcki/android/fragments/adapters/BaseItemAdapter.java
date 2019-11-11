package nl.uscki.appcki.android.fragments.adapters;

/**
 * Created by peter on 5/16/16.
 */

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import nl.uscki.appcki.android.generated.ListSectionHeader;
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
        // TODO: this function is called inside the scroll callback. Android is complaining that we can't
        // TODO: call notifyDataSetChanged inside a scroll callback.
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
        } else if(viewType == 2) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_agenda_participant_section_header, parent, false);
            return (T) new ListSectionHeaderHolder(view);
        } {
            return onCreateCustomViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        if(holder instanceof BaseItemAdapter.ListSectionHeaderHolder) {
            bindListDividerViewHolder((ListSectionHeaderHolder) holder, (ListSectionHeader) items.get(position));
        } else if(!(holder instanceof BaseItemAdapter.LoadingMoreViewHolder)) {
            onBindCustomViewHolder(holder, position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if(holder instanceof BaseItemAdapter.ListSectionHeaderHolder) {
            bindListDividerViewHolder((ListSectionHeaderHolder) holder, (ListSectionHeader) items.get(position));
        } else if(!(holder instanceof BaseItemAdapter.LoadingMoreViewHolder)) {
            onBindCustomViewHolder(holder, position, payloads);
        }
    }

    private void bindListDividerViewHolder(@NonNull ListSectionHeaderHolder holder, final ListSectionHeader headerInfo) {
        holder.header.setText(headerInfo.getDividingListHeader());
        if(headerInfo.getDividingSubHeader() != null) {
            holder.subHeader.setText(headerInfo.getDividingSubHeader());
            holder.subHeader.setVisibility(View.VISIBLE);
        } else {
            holder.subHeader.setVisibility(View.GONE);
        }
        if(headerInfo.getMessageBody() != null) {
            holder.messageBody.setText(headerInfo.getMessageBody());
            holder.messageBody.setVisibility(View.VISIBLE);
        } else {
            holder.messageBody.setVisibility(View.GONE);
        }
        if(headerInfo.getHelpText() != null) {
            holder.helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), headerInfo.getHelpText(), Toast.LENGTH_LONG).show();
                }
            });
            holder.helpButton.setVisibility(View.VISIBLE);
        } else {
            holder.helpButton.setVisibility(View.GONE);
        }
        if(headerInfo.isBottomDividerVisible()) {
            holder.listDivider.setVisibility(View.VISIBLE);
        }

        // TODO maybe add custom margins so it aligns with any list?
    }

    @Override
    public int getItemViewType(int position) {
        final K item = items.get(position);
        if(item instanceof LoadingMoreItem) {
            return 1;
        } else if(item instanceof ListSectionHeader) {
            return 2;
        } else {
            return 0;
        }
    }

    public class LoadingMoreViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        ProgressBar progressBar;

        LoadingMoreViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadMoreProgressBar);
            mView = itemView;
        }

        public void showLoadingMore(boolean visible) {
            progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public class ListSectionHeaderHolder extends RecyclerView.ViewHolder {
        public final View view;

        TextView header;
        TextView subHeader;
        TextView messageBody;
        Button helpButton;
        ImageView listDivider;

        ListSectionHeaderHolder(View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.dividing_list_header);
            subHeader = itemView.findViewById(R.id.dividing_list_subheader);
            messageBody = itemView.findViewById(R.id.dividing_list_message_body);
            helpButton = itemView.findViewById(R.id.helpButton);
            listDivider = itemView.findViewById(R.id.dividing_list_header_bottom_divider);

            this.view = itemView;
        }
    }
}
