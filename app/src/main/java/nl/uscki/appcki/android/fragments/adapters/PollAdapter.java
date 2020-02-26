package nl.uscki.appcki.android.fragments.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
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
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_poll, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            PollItem quote = (PollItem) payloads.get(0);
            items.set(position, quote);
            holder.mItem = quote;

            //TODO update item
        }
    }

    @Override
    public void onBindCustomViewHolder(final PollAdapter.ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);

        holder.question.setText(items.get(position).getPoll().getTitle());

        // TODO vervang geplaatst op string met een ding uit strings.xml
        String creation = "Geplaatst op " + items.get(position).getPoll().getCreation().toString("EEEE dd MMMM yyyy");
        holder.time.setText(creation);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(MainActivity.PARAM_POLL_ID, holder.mItem.getId());
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

        TextView question;
        TextView time;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            question = view.findViewById(R.id.poll_item_question);
            time = view.findViewById(R.id.poll_item_time);
        }
    }
}
