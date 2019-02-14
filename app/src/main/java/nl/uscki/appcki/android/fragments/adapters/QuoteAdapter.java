package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.quotes.Quote;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;
import nl.uscki.appcki.android.views.votesgraphview.QuoteVoteGraphView;
import retrofit2.Response;

/**
 * Created by peter on 3/1/17.
 */

public class QuoteAdapter extends BaseItemAdapter<QuoteAdapter.ViewHolder, Quote> {

    public QuoteAdapter(List<Quote> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_quote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            Quote quote = (Quote) payloads.get(0);
            items.set(position, quote);
            holder.mItem = quote;

            holder.votes_graph.setVoteItem(holder.mItem);
            holder.votes_graph.invalidate();

            if(holder.mItem.isHasVoted()) {
                holder.plus.setVisibility(View.INVISIBLE);
                holder.minus.setVisibility(View.INVISIBLE);
            } else {
                holder.plus.setVisibility(View.VISIBLE);
                holder.minus.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBindCustomViewHolder(final ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);

        SpannableStringBuilder text = Parser.parse(items.get(position).getQuote(), true, holder.quote);
        holder.quote.setText(text);
        double toonkans = ((double)holder.mItem.getWeight()/(double)holder.mItem.getTotalWeight()) * 100;
        holder.toonkans.setText(roundOffTo2DecPlaces(toonkans));

        holder.votes_graph.setVoteItem(holder.mItem);
        holder.votes_graph.invalidate();

        if(holder.mItem.isHasVoted()) {
            holder.plus.setVisibility(View.INVISIBLE);
            holder.minus.setVisibility(View.INVISIBLE);
        } else {
            holder.plus.setVisibility(View.VISIBLE);
            holder.minus.setVisibility(View.VISIBLE);

            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Services.getInstance().quoteService.vote(holder.mItem.getId(), true).enqueue(new Callback<ActionResponse<Quote>>() {
                        @Override
                        public void onSucces(Response<ActionResponse<Quote>> response) {
                            notifyItemChanged(holder.getAdapterPosition(), response.body().payload);
                        }
                    });
                }
            });

            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Services.getInstance().quoteService.vote(holder.mItem.getId(), false).enqueue(new Callback<ActionResponse<Quote>>() {
                        @Override
                        public void onSucces(Response<ActionResponse<Quote>> response) {
                            notifyItemChanged(holder.getAdapterPosition(), response.body().payload);
                        }
                    });
                }
            });
        }
    }

    private String roundOffTo2DecPlaces(double val) {
        return String.format(Locale.getDefault(), "toonkans: %.2f", val);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final BBTextView quote;
        public final QuoteVoteGraphView votes_graph;
        public final ImageButton plus;
        public final ImageButton minus;
        public final TextView toonkans;
        public Quote mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            quote = (BBTextView) view.findViewById(R.id.quote_quote);
            votes_graph = (QuoteVoteGraphView) view.findViewById(R.id.quote_graph);
            plus = (ImageButton) view.findViewById(R.id.quote_vote_plus);
            minus = (ImageButton) view.findViewById(R.id.quote_vote_minus);
            toonkans = (TextView) view.findViewById(R.id.quote_toonkans);
        }
    }
}