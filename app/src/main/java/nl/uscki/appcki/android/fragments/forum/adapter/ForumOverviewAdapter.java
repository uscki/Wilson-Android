package nl.uscki.appcki.android.fragments.forum.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.forum.ForumTopicOverviewFragment;
import nl.uscki.appcki.android.generated.forum.Forum;

public class ForumOverviewAdapter extends BaseItemAdapter<ForumOverviewAdapter.ForumOverviewViewHolder, Forum> {

    public ForumOverviewAdapter(List<Forum> items) {
        super(items);
    }

    @Override
    public ForumOverviewViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_item_layout, parent, false);
        return new ForumOverviewViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ForumOverviewViewHolder holder, int position) {
        holder.populateFromForum(items.get(position));
    }

    public static class ForumOverviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Forum forum;
        TextView header;
        TextView description;

        public ForumOverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.forum_item_header);
            description = itemView.findViewById(R.id.forum_item_description);
            itemView.setOnClickListener(this);
        }

        public void populateFromForum(Forum forum) {
            this.forum = forum;
            header.setText(forum.getName());
            description.setText(forum.getDescription());
        }

        @Override
        public void onClick(View v) {
            ForumTopicOverviewFragment fragment = new ForumTopicOverviewFragment();
            Bundle args = new Bundle();
            args.putInt(ForumTopicOverviewFragment.ARG_FORUM_ID, this.forum.getId());
            EventBus.getDefault().post(new OpenFragmentEvent(fragment, args));
        }
    }
}
