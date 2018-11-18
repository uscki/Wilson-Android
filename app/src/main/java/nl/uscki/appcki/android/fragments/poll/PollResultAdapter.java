package nl.uscki.appcki.android.fragments.poll;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.poll.PollItem;
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
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_result_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        PollOption item = items.get(position);

        holder.setOptionName(item.getName());
        holder.voteCount.setText(String.format(Locale.getDefault(), "(%d)", item.getVoteCount()));

        try {
            holder.bar.setBarColor(Color.parseColor(item.getColor().toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.setCanVote(canVote);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;

        private float startingX;
        private boolean hasAnimated = false;

        @BindView(R.id.pollOptionBackground)
        RelativeLayout background;

        @BindView(R.id.pollOptionForeground)
        RelativeLayout foreground;

        @BindView(R.id.poll_result_option_name)
        TextView name;

        @BindView(R.id.poll_result_option_name_centered)
        TextView centeredName;

        @BindView(R.id.poll_result_option_bar)
        VotesGraphView bar;

        @BindView(R.id.poll_option_vote_count)
        TextView voteCount;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        public void setCanVote(boolean canVote) {
            bar.setVotesTotal(totalvotes);

            if(canVote) {
                mView.setOnClickListener(this);
                bar.setVisibility(View.INVISIBLE);
                name.setVisibility(View.INVISIBLE);
                centeredName.setVisibility(View.VISIBLE);
                voteCount.setVisibility(View.GONE);
                voteCount.setText("");
                if(!hasAnimated)
                    startAnimation();
            } else {
                mView.setOnClickListener(null);
                bar.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                centeredName.setVisibility(View.GONE);
                voteCount.setVisibility(View.VISIBLE);
                ObjectAnimator.ofInt(bar, "VotesAnimated", 0, items.get(getAdapterPosition()).getVoteCount()).setDuration(400).start();
            }
        }

        @Override
        public void onClick(View view) {
            hintSwipeOption();
        }

        private void startAnimation() {
            hasAnimated = true;
            startingX = 400f + (100 * getAdapterPosition());
            int startDropDelay = (25 * getAdapterPosition());

            // Keep still fifth time around
            final int nOfBounces = 5;
            float[] args = new float[(nOfBounces)*2];
            args[0] = startingX;
            args[1] = 0;
            for(int i = 2; i < nOfBounces; i+=2) {
                args[i] = startingX / i;
                args[i+1] = 0;
            }
            ObjectAnimator anim = ObjectAnimator.ofFloat(foreground, "x", args);
            anim.setDuration(1300);
            anim.setStartDelay(startDropDelay);
            anim.start();
        }

        private void hintSwipeOption() {
            AnimatorSet set = new AnimatorSet();
            set.play(
                    ObjectAnimator.ofFloat(foreground, "x", 0).setDuration(100)
            ).after(
                    ObjectAnimator.ofFloat(foreground, "x", 300).setDuration(100)
            );
            set.start();
        }

        void setOptionName(String name) {
            this.name.setText(name);
            centeredName.setText(name);
        }
    }

}
