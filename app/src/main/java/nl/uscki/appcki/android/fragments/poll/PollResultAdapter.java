package nl.uscki.appcki.android.fragments.poll;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.poll.PollOption;
import nl.uscki.appcki.android.views.VotesGraphView;

/**
 * Created by peter on 3/20/17.
 */

public class PollResultAdapter extends BaseItemAdapter<PollResultAdapter.ViewHolder, PollOption> {
    int totalvotes;
    private boolean canVote;

    public PollResultAdapter(List<PollOption> items, boolean canVote) {
        super(items);
        this.canVote = canVote;

        for (PollOption item : items) {
            totalvotes += item.getVoteCount();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_result_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());

        try {
            holder.setCanVote(canVote);
            holder.bar.setBarColor(Color.parseColor(items.get(position).getColor().toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.bar.setVotes(items.get(position).getVoteCount());
        holder.bar.setVotesTotal(totalvotes);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.pollOptionBackground)
        RelativeLayout background;

        @BindView(R.id.pollOptionForeground)
        LinearLayout foreground;

        @BindView(R.id.poll_result_option_name)
        TextView name;
        @BindView(R.id.poll_result_option_bar)
        VotesGraphView bar;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        public void setCanVote(boolean canVote) {
            if(canVote) {
                bar.setVisibility(View.INVISIBLE);
            } else {
                bar.setVisibility(View.VISIBLE);
            }
        }
    }

}
