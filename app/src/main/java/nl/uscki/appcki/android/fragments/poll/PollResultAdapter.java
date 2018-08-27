package nl.uscki.appcki.android.fragments.poll;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import nl.uscki.appcki.android.helpers.GravityFallingValueEvaluator;
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
        holder.setOptionName(items.get(position).getName());

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

        private float startingX;

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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        public void setCanVote(boolean canVote) {
            if(canVote) {
                bar.setVisibility(View.INVISIBLE);
                name.setVisibility(View.INVISIBLE);
                centeredName.setVisibility(View.VISIBLE);
                startAnimation();
            } else {
                bar.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                centeredName.setVisibility(View.GONE);
            }
        }

        private void startAnimation() {
            startingX = 400f + (100 * getAdapterPosition());
            int startDropDelay = 50 + (25 * getAdapterPosition());
            foreground.setTranslationX(startingX);
            GravityFallingValueEvaluator evaluator = new GravityFallingValueEvaluator();
            evaluator.setBounces(3);
            final ValueAnimator positionAnimator = ValueAnimator.ofObject(evaluator, startingX, 0f);
            positionAnimator.setStartDelay(startDropDelay);
            positionAnimator.setDuration(500);
            positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    foreground.setTranslationX((float) valueAnimator.getAnimatedValue());
                }
            });
            positionAnimator.start();
        }

        void setOptionName(String name) {
            this.name.setText(name);
            centeredName.setText(name);
        }
    }

}
