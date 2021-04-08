package nl.uscki.appcki.android.fragments.media.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.captioncontest.Caption;
import nl.uscki.appcki.android.generated.captioncontest.CaptionContest;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;
import nl.uscki.appcki.android.views.votesgraphview.CaptionVotesGraphView;

public class CaptionAdapter extends BaseItemAdapter<CaptionAdapter.CaptionViewHolder, Caption> {

    private CaptionContest contest;

    public CaptionAdapter() {
        super(new ArrayList<>(Collections.emptyList()));
    }

    public void setContest(CaptionContest contest) {
        this.contest = contest;
    }

    @Override
    public void update(List<Caption> items) {
        this.items.clear();
        this.items.addAll(items);
        updateMaxItems();
        notifyDataSetChanged();
    }

    @Override
    public void add(Caption item) {
        this.items.add(item);
        updateMaxItems();
        notifyDataSetChanged();
    }

    @Override
    public void addItems(List<Caption> items) {
        this.items.addAll(items);
        updateMaxItems();
        notifyDataSetChanged();
    }

    @Override
    public void insert(int position, Caption item) {
        this.items.add(position, item);
        updateMaxItems();
        notifyDataSetChanged();
    }

    private void updateMaxItems() {
        int maxVotes = 0;
        for(Caption item : items) {
            maxVotes = Math.max(maxVotes, item.getVotes());
        }
        for(Caption item : items) {
            item.setMaxVotes(maxVotes);
        }
        notifyDataSetChanged();
    }

    @Override
    public CaptionViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.caption_item, parent, false);
        return new CaptionViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(CaptionViewHolder holder, int position) {
        Caption caption = this.items.get(position);

        holder.caption = caption;
        holder.background = holder.itemView.findViewById(R.id.caption_option_background);
        holder.foreground = holder.itemView.findViewById(R.id.caption_option_foreground);

        holder.captionAuthorText = holder.itemView.findViewById(R.id.caption_author);
        holder.captionAuthorImage = holder.itemView.findViewById(R.id.caption_author_image);
        holder.captionText = holder.itemView.findViewById(R.id.caption_text);
        holder.captionVotesBar = holder.itemView.findViewById(R.id.caption_votes_bar);
        holder.captionVotesBar.setVoteItem(caption);
        holder.captionSeparator = holder.itemView.findViewById(R.id.caption_separator);

        holder.captionText.setText(Parser.parse(caption.getCaption(), true, holder.captionText));

        CaptionContest.Status status = this.contest.getStatus();

        if(status.isCanVote()) {
            holder.itemView.setOnClickListener(holder);
            holder.captionText.setOnClickListener(holder);
            if(!holder.hasAnimated) holder.startAnimation();
        } else {
            holder.itemView.setOnClickListener(null);
            holder.captionText.setOnClickListener(null);
        }

        boolean showAuthor = false;
        if(status.isResultVisible()) {
            holder.captionVotesBar.setVisibility(View.VISIBLE);
            showAuthor = caption.getPerson() != null;
        } else {
            holder.captionVotesBar.setVisibility(View.GONE);
        }

        if(showAuthor && status.isResultVisible()) {
            holder.captionAuthorImage.setVisibility(View.VISIBLE);
            if (caption.getPerson().getPhotomediaid() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(MediaAPI.getMediaUri(caption.getPerson().getPhotomediaid(), MediaAPI.MediaSize.SMALL))
                        .error(R.drawable.account)
                        .fitCenter()
                        .optionalCircleCrop()
                        .into(holder.captionAuthorImage);
            }
            holder.captionAuthorImage.setOnClickListener(v -> {
                if (v.getContext() instanceof BasicActivity) {
                    BasicActivity o = (BasicActivity) v.getContext();
                    o.openSmoboFor(caption.getPerson());
                }
            });
            holder.captionAuthorText.setVisibility(View.VISIBLE);
            holder.captionAuthorText.setText(String.format(Locale.getDefault(), "%s:", caption.getPerson().getPostalname()));
        } else if (status.isResultVisible()) {
            holder.captionAuthorText.setVisibility(View.GONE);
            holder.captionAuthorImage.setVisibility(View.INVISIBLE);
        } else {
            holder.captionAuthorText.setVisibility(View.GONE);
            holder.captionAuthorImage.setVisibility(View.GONE);
        }

        holder.captionSeparator.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);
    }

    public class CaptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private float startingX;
        private boolean hasAnimated = false;

        private Caption caption;
        RelativeLayout background;
        public RelativeLayout foreground;
        TextView captionAuthorText;
        ImageView captionAuthorImage;
        BBTextView captionText;
        CaptionVotesGraphView captionVotesBar;
        ImageView captionSeparator;

        public CaptionViewHolder(@NonNull View itemView) {
            super(itemView);
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

        public Caption getCaption() {
            return caption;
        }
    }

}
