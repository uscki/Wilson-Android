package nl.uscki.appcki.android.fragments.forum;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.forum.adapter.ForumPostAdapter;
import nl.uscki.appcki.android.generated.forum.Post;
import nl.uscki.appcki.android.generated.forum.Topic;
import nl.uscki.appcki.android.helpers.UserHelper;

public class ForumPostOverviewFragment extends PageableFragment<ForumPostAdapter.ForumPostViewHolder, Post> {

    private static final int PAGE_SIZE = 5;

    private Topic topic;
    private int topicId;

    // A notification only contains the topic ID. However, we abuse a feature (not a bug) in the
    // API where the forumID is ignored in most API calls in that case.
    private int forumId = -1;

    private Menu menu;
    private String[] sort = sortStrings.get(R.id.forum_posts_sort_time_asc);

    private static final SparseArray<String[]> sortStrings;
    static
    {
        sortStrings = new SparseArray<>();
        sortStrings.put(R.id.forum_posts_sort_time_desc, new String[] {"postTime,desc"});
        sortStrings.put(R.id.forum_posts_sort_time_asc, new String[] {"postTime,asc"});
    }

    public ForumPostOverviewFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ForumTopicOverviewFragment.ARG_TOPIC_OBJ)) {
            this.forumId = getArguments().getInt(ForumTopicOverviewFragment.ARG_FORUM_ID, -1);
            this.topic = getArguments().getParcelable(ForumTopicOverviewFragment.ARG_TOPIC_OBJ);
            this.topicId = topic.getId();
        } else if (getArguments() != null && getArguments().containsKey(ForumTopicOverviewFragment.ARG_TOPIC_ID)) {
            this.topicId = getArguments().getInt(ForumTopicOverviewFragment.ARG_TOPIC_ID);
        }

        if(UserHelper.getInstance().getCurrentUser().isForum_new_posts_first()) {
            this.sort = sortStrings.get(R.id.forum_posts_sort_time_desc);
        }
        setAdapter(new ForumPostAdapter(new ArrayList<>(), this, (BasicActivity) getActivity(), topic));
        setHasOptionsMenu(true);

        onScrollRefresh();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if((topic == null || !topic.isLocked()) && view != null) {
            FloatingActionButton fab = setFabEnabled(true);
            fab.setOnClickListener(v -> {
                showNewPostDialog(null);
            });
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forum_posts_sort_menu, menu);
        this.menu = menu;

        for(int i = 0; i < sortStrings.size(); i++) {
            menu.findItem(sortStrings.keyAt(i)).setOnMenuItemClickListener(sortListener);
        }

        if(UserHelper.getInstance().getCurrentUser().isForum_new_posts_first()) {
            menu.findItem(R.id.forum_posts_sort_time_desc).setIcon(R.drawable.check);
            menu.findItem(R.id.forum_posts_sort_time_asc).setIcon(null);
        }

        menu.findItem(R.id.forum_posts_share_topic_action).setOnMenuItemClickListener(item -> {
            String url = getResources().getString(R.string.incognito_website_forum_topic, topicId);
            String shareText = topic == null ? url : getResources().getString(
                    R.string.wilson_media_forum_topic_share_intent_text_extra,
                    topic.getTitle(),
                    url
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TITLE, shareText);
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            intent.setType("text/*");
            getContext().startActivity(Intent.createChooser(intent,
                    getContext().getString(R.string.app_general_action_share_intent_text)));

            return true;
        });
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().forumService.getPosts(this.forumId, this.topicId, this.page, getPageSize(), sort).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().forumService.getPosts(this.forumId, this.topicId, this.page, getPageSize(), sort).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.wilson_media_forum_posts_empty_text);
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }

    private MenuItem.OnMenuItemClickListener sortListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(menu != null) {
                for (int i = 0; i < sortStrings.size(); i++) {
                    menu.findItem(sortStrings.keyAt(i)).setIcon(null);
                }
            }
            sort = sortStrings.get(menuItem.getItemId());
            menuItem.setIcon(R.drawable.check);

            ForumPostOverviewFragment.this.refresh();

            return true;
        }
    };

    public void onEventMainThread(DetailItemUpdatedEvent<Post> event) {
        this.refresh();
    }

    public void showNewPostDialog(String content) {
        AddForumPostFragment dialog = new AddForumPostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ForumTopicOverviewFragment.ARG_TOPIC_ID, this.topicId);
        bundle.putString(AddForumPostFragment.ARG_INITIAL_CONTENT, content);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "AddForumPostDialog");
        dialog.setCancelable(true);
    }
}
