package nl.uscki.appcki.android.fragments.forum.adapter;

import android.content.Context;
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
import nl.uscki.appcki.android.generated.forum.Topic;

public class ForumTopicAdapter extends BaseItemAdapter<ForumTopicAdapter.ForumTopicViewHolder, Topic> {

    private int forumId;

    public ForumTopicAdapter(List<Topic> items, int forumId) {
        super(items);
        this.forumId = forumId;
    }

    @Override
    public ForumTopicViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_topic_item_layout, parent, false);
        return new ForumTopicViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(ForumTopicViewHolder holder, int position) {
        holder.populateFromTopic(items.get(position));
    }

    public class ForumTopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView sticky;
        private Topic forumTopic;
        private TextView title;
        private ImageView lockedIcon;
        private TextView postedDate;
        private TextView views;

        private TextView lastReplyName;
        private TextView lastReplyDate;

        // Not present in API
//        ImageView authorProfileImage;
//        TextView authorName;
//        TextView replies;

        public ForumTopicViewHolder(@NonNull View itemView) {
            super(itemView);
            sticky = itemView.findViewById(R.id.forum_topic_sticky_image);
            title = itemView.findViewById(R.id.forum_topic_title);
            views = itemView.findViewById(R.id.forum_topic_n_views);
            lockedIcon = itemView.findViewById(R.id.forum_topic_locked_icon);
            postedDate = itemView.findViewById(R.id.forum_topic_posted_date_label);

            lastReplyName = itemView.findViewById(R.id.forum_topic_last_reply_author);
            lastReplyDate = itemView.findViewById(R.id.forum_topic_last_reply_time);

            // Not present in API
//            authorName = itemView.findViewById(R.id.forum_topic_starter_profile_name);
//            authorProfileImage = itemView.findViewById(R.id.forum_topic_starter_profile_image);
//            viewHolder.replies = view.findViewById(R.id.forum_topic_n_replies);
        }

        void populateFromTopic(Topic topic) {
            Context c = itemView.getContext();
            this.forumTopic = topic;
            this.title.setText(topic.getTitle());
            this.postedDate.setText(c.getString(R.string.wilson_media_forum_topic_created_date,
                    topic.getPosted().toString(c.getString(R.string.joda_datetime_format_year_month_day_with_day_names))));
            this.lockedIcon.setVisibility(topic.isLocked() ? View.VISIBLE : View.GONE);
            this.views.setText(c.getString(R.string.wilson_media_forum_topic_num_views, topic.getViews()));
            this.itemView.setOnClickListener(this);
            this.sticky.setVisibility(topic.isSticky() ? View.VISIBLE : View.GONE);

            if (topic.getLastPost() != null) {
                lastReplyName.setVisibility(View.VISIBLE);
                lastReplyName.setText(topic.getLastPost().getPosterName());
                lastReplyDate.setVisibility(View.VISIBLE);
                lastReplyDate.setText(Utils.timestampConversion(topic.getLastPost().getPost_time().getMillis()));
            } else {
                lastReplyName.setVisibility(View.GONE);
                lastReplyDate.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            ForumPostOverviewFragment fragment = new ForumPostOverviewFragment();
            Bundle args = new Bundle();
            args.putInt(ForumTopicOverviewFragment.ARG_FORUM_ID, ForumTopicAdapter.this.forumId);
            args.putParcelable(ForumTopicOverviewFragment.ARG_TOPIC_OBJ, forumTopic);
            EventBus.getDefault().post(new OpenFragmentEvent(fragment, args));
        }
    }
}
