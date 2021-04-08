package nl.uscki.appcki.android.fragments.poll;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.poll.PollOption;
import nl.uscki.appcki.android.views.votesgraphview.PollVotesGraphView;

/**
 * Created by peter on 3/20/17.
 */

public class PollResultAdapter extends BaseItemAdapter<PollResultAdapter.ViewHolder, PollOption> {

    private boolean canVote;

    public PollResultAdapter(List<PollOption> items, boolean canVote) {
        super(items);
        this.canVote = canVote;
        int totalVotes = 0;
        int highestVoteCount = 0;

        for (PollOption item : items) {
            totalVotes += item.getVoteCount();
            if(item.getVoteCount() > highestVoteCount) highestVoteCount = item.getVoteCount();
        }

        for(PollOption item : items) {
            item.setTotalVoteCount(totalVotes);
            item.setMaxVote(highestVoteCount);
        }
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_result_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        PollOption item = items.get(position);
        holder.bar.setVoteItem(item);
        holder.setOptionName(item.getName());
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

        RelativeLayout background;
        RelativeLayout foreground;
        TextView name;
        TextView centeredName;
        PollVotesGraphView bar;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            background = view.findViewById(R.id.pollOptionBackground);
            foreground = view.findViewById(R.id.pollOptionForeground);
            name = view.findViewById(R.id.poll_result_option_name);
            centeredName = view.findViewById(R.id.poll_result_option_name_centered);
            bar = view.findViewById(R.id.poll_result_option_bar);
        }

        void setCanVote(boolean canVote) {

            if(canVote) {
                mView.setOnClickListener(this);
                bar.setVisibility(View.INVISIBLE);
                name.setVisibility(View.INVISIBLE);
                centeredName.setVisibility(View.VISIBLE);
                if(!hasAnimated)
                    startAnimation();
            } else {
                mView.setOnClickListener(null);
                bar.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                centeredName.setVisibility(View.GONE);
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
