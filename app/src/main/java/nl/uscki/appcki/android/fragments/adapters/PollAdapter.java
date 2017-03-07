package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.poll.PollItem;

/**
 * Created by peter on 3/7/17.
 */

public class PollAdapter extends BaseItemAdapter<PollAdapter.ViewHolder, PollItem> {
    public PollAdapter(List<PollItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_quote, parent, false);// TODO correct fragment
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            PollItem quote = (PollItem) payloads.get(0);
            items.set(position, quote);
            holder.mItem = quote;

            //TODO update item
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(final PollAdapter.ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public PollItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }
    }
}
