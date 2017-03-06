package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.quotes.Quote;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;
import nl.uscki.appcki.android.views.VotesGraphView;
import retrofit2.Response;

/**
 * Created by peter on 3/1/17.
 */

public class QuoteAdapter extends BaseItemAdapter<QuoteAdapter.ViewHolder, Quote> {

    public QuoteAdapter(List<Quote> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_quote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            Quote quote = (Quote) payloads.get(0);
            items.set(position, quote);
            holder.mItem = quote;

            int positive = items.get(position).getPositiveVotes();
            int negative = items.get(position).getNegativeVotes();
            holder.votes_negative.setVotes(negative);
            holder.votes_negative.setVotesTotal(negative+positive);

            holder.votes_positive.setVotes(positive);
            holder.votes_positive.setVotesTotal(negative+positive);

            holder.votes_negative.invalidate();
            holder.votes_positive.invalidate();
            holder.votes_negative.requestLayout();
            holder.votes_positive.requestLayout();

            if(holder.mItem.isHasVoted()) {
                holder.plus.setAlpha(0.5f);
                holder.minus.setAlpha(0.5f);
            } else {
                holder.plus.setAlpha(1.0f);
                holder.minus.setAlpha(1.0f);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);

        SpannableStringBuilder text = Parser.parse(items.get(position).getQuoteJSON(), true, holder.quote);
        holder.quote.setText(text);
        double toonkans = ((double)holder.mItem.getWeight()/(double)holder.mItem.getTotalWeight()) * 100;
        holder.toonkans.setText(roundOffTo2DecPlaces(toonkans));

        int positive = items.get(position).getPositiveVotes();
        int negative = items.get(position).getNegativeVotes();
        holder.votes_negative.setVotes(negative);
        holder.votes_negative.setVotesTotal(negative+positive);

        holder.votes_positive.setVotes(positive);
        holder.votes_positive.setVotesTotal(negative+positive);

        // update measurements and view
        holder.votes_negative.requestLayout();
        holder.votes_positive.requestLayout();

        if(holder.mItem.isHasVoted()) { // don't allow to vote again
            holder.plus.setAlpha(0.3f);
            holder.minus.setAlpha(0.3f);
        } else {
            holder.plus.setAlpha(1.0f);
            holder.minus.setAlpha(1.0f);

            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Services.getInstance().quoteService.vote(holder.mItem.getId(), true).enqueue(new Callback<Quote>() {
                        @Override
                        public void onSucces(Response<Quote> response) {
                            notifyItemChanged(holder.getAdapterPosition(), response.body());
                        }
                    });
                }
            });

            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Services.getInstance().quoteService.vote(holder.mItem.getId(), false).enqueue(new Callback<Quote>() {
                        @Override
                        public void onSucces(Response<Quote> response) {
                            notifyItemChanged(holder.getAdapterPosition(), response.body());
                        }
                    });
                }
            });
        }
    }

    private String roundOffTo2DecPlaces(double val) {
        return String.format("toonkans: %.2f", val);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final BBTextView quote;
        public final VotesGraphView votes_positive;
        public final VotesGraphView votes_negative;
        public final ImageButton plus;
        public final ImageButton minus;
        public final TextView toonkans;
        public Quote mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            quote = (BBTextView) view.findViewById(R.id.quote_quote);
            votes_positive = (VotesGraphView) view.findViewById(R.id.votes_positive);
            votes_negative = (VotesGraphView) view.findViewById(R.id.votes_negative);
            plus = (ImageButton) view.findViewById(R.id.quote_vote_plus);
            minus = (ImageButton) view.findViewById(R.id.quote_vote_minus);
            toonkans = (TextView) view.findViewById(R.id.quote_toonkans);
        }
    }
}