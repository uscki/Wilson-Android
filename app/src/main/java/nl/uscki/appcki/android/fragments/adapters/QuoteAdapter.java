package nl.uscki.appcki.android.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.quotes.Quote;
import nl.uscki.appcki.android.helpers.bbparser.Parser;
import nl.uscki.appcki.android.views.BBTextView;
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

            int totalVotes = items.get(position).getNegativeVotes() + items.get(position).getPositiveVotes();
            holder.graph.setMax(totalVotes);
            holder.graph.setProgress(items.get(position).getPositiveVotes());

            if(items.get(position).isHasVoted()) {
                holder.plus.setVisibility(View.GONE);
                holder.minus.setVisibility(View.GONE);
            } else {
                holder.plus.setVisibility(View.VISIBLE);
                holder.minus.setVisibility(View.VISIBLE);
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

        int totalVotes = items.get(position).getNegativeVotes() + items.get(position).getPositiveVotes();
        holder.graph.setMax(totalVotes);
        holder.graph.setProgress(items.get(position).getPositiveVotes());

        if(items.get(position).isHasVoted()) {
            holder.plus.setVisibility(View.GONE);
            holder.minus.setVisibility(View.GONE);
        } else {
            holder.plus.setVisibility(View.VISIBLE);
            holder.minus.setVisibility(View.VISIBLE);
        }

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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final BBTextView quote;
        public final ProgressBar graph;
        public final ImageButton plus;
        public final ImageButton minus;
        public Quote mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            quote = (BBTextView) view.findViewById(R.id.quote_quote);
            graph = (ProgressBar) view.findViewById(R.id.quote_votes_graph);
            plus = (ImageButton) view.findViewById(R.id.quote_vote_plus);
            minus = (ImageButton) view.findViewById(R.id.quote_vote_minus);
        }
    }
}