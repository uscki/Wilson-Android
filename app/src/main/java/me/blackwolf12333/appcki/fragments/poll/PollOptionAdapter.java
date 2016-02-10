package me.blackwolf12333.appcki.fragments.poll;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.PollOption;

/**
 * Created by peter on 2/7/16.
 */
public class PollOptionAdapter extends RecyclerView.Adapter<PollOptionAdapter.ViewHolder> {
    private final List<PollOption> mValues;
    private ViewHolder holder;

    public PollOptionAdapter(List<PollOption> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_poll_item, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PollOption item = mValues.get(position);
        holder.mItem = item;
        holder.mName.setText(item.getName());

        holder.mBar.setBackgroundColor(Color.parseColor(item.getColor()));
        holder.mBar.setLayoutParams(new LinearLayout.LayoutParams(10*item.getVoteCount(), 30));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.mBar.getLayoutParams();
        params.setMargins(5, 10, 0, 0); // substitute parameters for left,top, right, bottom
        holder.mBar.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public PollOption mItem;
        public TextView mName;
        public View mBar;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.poll_item_name);
            mBar = view.findViewById(R.id.poll_item_bar);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}
