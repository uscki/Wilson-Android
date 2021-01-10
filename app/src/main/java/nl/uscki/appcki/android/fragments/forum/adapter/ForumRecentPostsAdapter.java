package nl.uscki.appcki.android.fragments.forum.adapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.forum.ForumPostOverviewFragment;
import nl.uscki.appcki.android.fragments.forum.ForumTopicOverviewFragment;
import nl.uscki.appcki.android.generated.forum.RecentTopic;

public class ForumRecentPostsAdapter extends BaseItemAdapter<ForumRecentPostsAdapter.ViewHolder, RecentTopic> {

    public ForumRecentPostsAdapter(List<RecentTopic> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_recent_post_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        holder.setupView(items.get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RecentTopic recentTopic;
        private TextView topicTitle;
        private ImageView unreadIcon;
        private TextView lastUpdatedDate;
        private TextView authorAndForum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.topicTitle = itemView.findViewById(R.id.recent_post_topic_title);
            this.unreadIcon = itemView.findViewById(R.id.recent_post_unread_icon);
            this.lastUpdatedDate = itemView.findViewById(R.id.recent_post_last_updated_date);
            this.authorAndForum = itemView.findViewById(R.id.recent_post_author_and_forum);
            this.itemView.setOnClickListener(this);
        }

        void setupView(RecentTopic recentTopic) {
            this.recentTopic = recentTopic;
            this.topicTitle.setText(recentTopic.getTitle());
            this.lastUpdatedDate.setText(Utils.timestampConversion(recentTopic.getLastPost().getPost_time().getMillis(), false));
            this.authorAndForum.setText(itemView.getContext().getString(
                    R.string.wilson_media_forum_recent_post_author_and_forum,
                    recentTopic.getLastPost().getPosterName(),
                    recentTopic.getForum().getName()
            ));

            if(recentTopic.isRead()) {
                this.topicTitle.setTypeface(null, Typeface.NORMAL);
                this.unreadIcon.setVisibility(View.GONE);
            } else {
                this.topicTitle.setTypeface(null, Typeface.BOLD);
                this.unreadIcon.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Bundle arg = new Bundle();
            arg.putParcelable(ForumTopicOverviewFragment.ARG_TOPIC_OBJ, recentTopic);
            arg.putInt(ForumTopicOverviewFragment.ARG_FORUM_ID, recentTopic.getForum().getId());
            ForumPostOverviewFragment forumPostOverviewFragment = new ForumPostOverviewFragment();
            EventBus.getDefault().post(new OpenFragmentEvent(forumPostOverviewFragment, arg));
        }
    }
}
