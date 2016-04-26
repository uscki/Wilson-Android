package me.blackwolf12333.appcki.fragments.poll2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import de.greenrobot.event.EventBus;
import me.blackwolf12333.appcki.R;
import me.blackwolf12333.appcki.api.VolleyPoll;
import me.blackwolf12333.appcki.events.PollVotedEvent;
import me.blackwolf12333.appcki.generated.poll.PollOption;

/**
 * Created by peter on 4/26/16.
 */
public class PollOptionAdapter extends RecyclerView.Adapter<PollOptionAdapter.ViewHolder> {
    private final List<PollOption> mValues;
    private ViewHolder holder;
    private VolleyPoll pollAPI = new VolleyPoll();

    public PollOptionAdapter(List<PollOption> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pollitem, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PollOption item = mValues.get(position);
        holder.mItem = item;
        holder.name.setText(item.getName());
        holder.vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {pollAPI.vote(item.getId());
                EventBus.getDefault().post(new PollVotedEvent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public PollOption mItem;
        public final TextView name;
        public final Button vote;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = (TextView) view.findViewById(R.id.poll_name);
            vote = (Button) view.findViewById(R.id.vote_button);
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
