package me.blackwolf12333.appcki.fragments.poll;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.generated.poll.PollOption;

/**
 * Created by peter on 4/26/16.
 */
public class PollResultAdapter extends RecyclerView.Adapter<PollResultAdapter.ViewHolder> {
    private final List<PollOption> mValues;
    private ViewHolder holder;

    public PollResultAdapter(List<PollOption> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pollresult, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PollOption item = mValues.get(position);
        holder.mItem = item;
        holder.name.setText(item.getName());
        //TODO holder.votes;
        holder.votes_number.setText(item.getVoteCount()+"");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public PollOption mItem;
        public final TextView name;
        //public final View votes;
        public final TextView votes_number;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = (TextView) view.findViewById(R.id.poll_name);
            //votes = view.findViewById(R.id.poll_results);
            votes_number = (TextView) view.findViewById(R.id.poll_results_number);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<PollOption> list) {
        mValues.addAll(list);
        notifyDataSetChanged();
    }
}
