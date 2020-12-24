package nl.uscki.appcki.android.fragments.media.adapters;

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
import java.util.Locale;

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

        holder.titleText.setText(String.format(Locale.getDefault(), "Captioncontest %s",
                contest.getStartdate().toString("EEEE d MMMM Y"))); // TODO string resource, same as in detailFragment

        if(contest.getIsCurrentContest()) {
            bindViewholderForCurrent(holder, contest);
        } else {
            bindViewholderForClosed(holder, contest);
        }
    }

    private void bindViewholderForCurrent(ViewHolder holder, CaptionContest contest) {
        holder.winningCaption.setVisibility(View.GONE);
        if(contest.getStatus().isCanAdd()) {
            holder.voteCount.setText(String.format(Locale.getDefault(), "Je kunt nog onderschriften toevoegen tot: %s",
                    contest.getVotedate().toString("EEEE d MMMM Y")));  // TODO use same string resource as in detail fragment
        } else if (contest.getStatus().equals(CaptionContest.Status.ADDED)) {
            holder.voteCount.setText(String.format(Locale.getDefault(), "Je kunt stemmen vanaf %s",
                    contest.getVotedate().toString("EEEE d MMMM Y")));  // TODO use same string resource as in detail fragment
        } else if (contest.getStatus().isCanVote()) {
            holder.voteCount.setText(String.format(Locale.getDefault(), "Je hebt nog niet gestemd"));  // TODO use same string resource as in detail fragment
        } else if (contest.getStatus().equals(CaptionContest.Status.VOTED)) {
            holder.voteCount.setText(String.format(Locale.getDefault(), "Nog geen winnaar bekend")); // TODO use string resources
        } else {
            holder.voteCount.setText(String.format(Locale.getDefault(), "Deze caption contest opent op %s",
                    contest.getStartdate().toString("EEEE d MMMM Y")));
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

        if(caption != null && numberWithVotes == 1) {
            holder.winningCaption.setVisibility(View.VISIBLE);
            holder.winningCaption.setText(Parser.parse(caption.getCaption(), true, holder.winningCaption));
            if(caption.getPerson() != null) {
                holder.voteCount.setText(String.format(Locale.getDefault(), "Gewonnen door %s met %d stemmen", caption.getPerson().getPostalname(), votes)); // TODO string resources TODO (Huidige...) winnaar? Alleen als al gestemd of !current?
            } else {
                holder.voteCount.setText(String.format(Locale.getDefault(), "Gewonnen met %d stemmen", votes)); // TODO string resources TODO (Huidige...) winnaar? Alleen als al gestemd of !current?
            }
        } else if (caption != null) {
            holder.winningCaption.setVisibility(View.GONE);
            holder.voteCount.setText(String.format(Locale.getDefault(), "%d onderschriften delen de eerste plaats met %d stemmen", numberWithVotes, votes)); // TODO string resources
        } else {
            holder.winningCaption.setVisibility(View.GONE);
            holder.voteCount.setText("Deze captioncontest heeft geen onderschriften");
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
