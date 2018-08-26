package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.poll.PollResultFragment;
import nl.uscki.appcki.android.generated.poll.PollItem;

/**
 * Created by peter on 3/7/17.
 */

public class PollAdapter extends BaseItemAdapter<PollAdapter.ViewHolder, PollItem> {
    public PollAdapter(List<PollItem> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_poll, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            PollItem quote = (PollItem) payloads.get(0);
            items.set(position, quote);
            holder.mItem = quote;

            //TODO update item
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(final PollAdapter.ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);

        holder.question.setText(items.get(position).getPoll().getTitle());

        // TODO vervang geplaatst op string met een ding uit strings.xml
        String creation = "Geplaatst op " + items.get(position).getPoll().getCreation().toString("EEEE dd MMMM yyyy");
        holder.time.setText(creation);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String item = new Gson().toJson(holder.mItem);
                bundle.putString("item", item);
                EventBus.getDefault().post(new OpenFragmentEvent(new PollResultFragment(), bundle));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public PollItem mItem;

        @BindView(R.id.poll_item_question)
        TextView question;
        @BindView(R.id.poll_item_time)
        TextView time;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
