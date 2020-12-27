package nl.uscki.appcki.android.fragments.media.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.media.captioncontest.CaptionContestDetailFragment;
import nl.uscki.appcki.android.generated.captioncontest.Caption;
import nl.uscki.appcki.android.generated.captioncontest.CaptionContest;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;

public class CaptionContestHistoryAdapter extends BaseItemAdapter<CaptionContestHistoryAdapter.ViewHolder, CaptionContest> {


    public CaptionContestHistoryAdapter(List<CaptionContest> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.caption_contest_history_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        CaptionContest contest = items.get(position);
        holder.captionContest = contest;
        holder.image = holder.itemView.findViewById(R.id.caption_history_contest_image);
        holder.titleText = holder.itemView.findViewById(R.id.caption_history_contest_title);
        holder.winningCaption = holder.itemView.findViewById(R.id.caption_history_contest_winner);
        holder.voteCount = holder.itemView.findViewById(R.id.caption_history_contest_n_votes);

        Glide.with(holder.itemView.getContext())
                .load(MediaAPI.getMediaUri(contest.getMediaFileId(), MediaAPI.MediaSize.NORMAL))
                .thumbnail(Glide.with(holder.itemView.getContext()).load(MediaAPI.getMediaUri(contest.getMediaFileId(), MediaAPI.MediaSize.SMALL)))
                .apply(new RequestOptions().dontTransform())
                .error(R.drawable.ic_wilson)
                .into(holder.image);

        holder.titleText.setText(holder.itemView.getContext().getString(R.string.wilson_media_caption_contest_header,
                contest.getStartdate().toString(holder.itemView.getContext().getString(R.string.joda_datetime_format_year_month_day_with_day_names))));

        if(contest.getIsCurrentContest()) {
            bindViewholderForCurrent(holder, contest);
        } else {
            bindViewholderForClosed(holder, contest);
        }
    }

    private void bindViewholderForCurrent(ViewHolder holder, CaptionContest contest) {
        Context c = holder.itemView.getContext();
        holder.winningCaption.setVisibility(View.GONE);
        if(contest.getStatus().isCanAdd()) {
            holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_add_caption_closes_label,
                    contest.getVotedate().toString(c.getString(R.string.joda_datetime_format_year_month_day_with_day_names)))); // TODO same as detail fragment?
        } else if (contest.getStatus().equals(CaptionContest.Status.ADDED)) {
            holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_voting_opens_label,
                    contest.getVotedate().toString(holder.itemView.getContext().getString(R.string.joda_datetime_format_year_month_day_with_day_names))));  // TODO use same string resource as in detail fragment
        } else if (contest.getStatus().isCanVote()) {
            holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_not_voted_label));  // TODO use same string resource as in detail fragment
        } else if (contest.getStatus().equals(CaptionContest.Status.VOTED)) {
            holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_no_winner_announced)); // TODO use string resources
        } else {
            holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_voting_opens_label,
                    contest.getStartdate().toString(holder.itemView.getContext().getString(R.string.joda_datetime_format_year_month_day_with_day_names))));
        }
    }

    private void bindViewholderForClosed(ViewHolder holder, CaptionContest contest) {
        Caption caption = null;
        int votes = -1;
        int numberWithVotes = 0;

        for(Caption c : contest.getCaptions()) {
            if(c.getVotes().equals(votes)) {
                numberWithVotes++;
            } else if(c.getVotes() > votes) {
                caption = c;
                votes = c.getVotes();
                numberWithVotes = 1;
            }
        }

        Context c = holder.itemView.getContext();

        if(caption != null && numberWithVotes == 1) {
            holder.winningCaption.setVisibility(View.VISIBLE);
            holder.winningCaption.setText(Parser.parse(caption.getCaption(), true, holder.winningCaption));
            if(caption.getPerson() != null) {
                holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_winner_name, caption.getPerson().getPostalname(), votes));
            } else {
                holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_winner_anonymous, votes));
            }
        } else if (caption != null) {
            holder.winningCaption.setVisibility(View.GONE);
            holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_shared_winners, numberWithVotes, votes));
        } else {
            holder.winningCaption.setVisibility(View.GONE);
            holder.voteCount.setText(c.getString(R.string.wilson_media_caption_contest_no_captions));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CaptionContest captionContest;
        TextView titleText;
        BBTextView winningCaption;
        ImageView image;
        TextView voteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CaptionContestDetailFragment fragment = new CaptionContestDetailFragment();
            Bundle args = new Bundle();
            args.putInt(CaptionContestDetailFragment.ARG_CAPTION_CONTEST_ID, captionContest.getId());
            EventBus.getDefault().post(new OpenFragmentEvent(fragment, args));
        }
    }
}
