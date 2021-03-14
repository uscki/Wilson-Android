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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.events.DetailItemUpdatedEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.forum.adapter.ForumPostAdapter;
import nl.uscki.appcki.android.generated.forum.Post;
import nl.uscki.appcki.android.generated.forum.Topic;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

public class ForumPostOverviewFragment extends PageableFragment<ForumPostAdapter.ForumPostViewHolder, Post> {

    private static final int PAGE_SIZE = 5;

    private Topic topic;
    private int topicId;

    // A notification only contains the topic ID. However, we abuse a feature (not a bug) in the
    // API where the forumID is ignored in most API calls in that case.
    private int forumId = -1;

    // Dealing with read status of individual posts and topic as a whole
    private Integer isReadingPost = null;
    private Timer markAsReadTimer = new Timer();
    private TimerTask markAsReadTask = null;
    private static final int MARK_AS_READ_DELAY = 1500;

    private Menu menu;
    private String[] sort = sortStrings.get(R.id.forum_posts_sort_time_asc);

    private static final SparseArray<String[]> sortStrings;
    static
    {
        sortStrings = new SparseArray<>();
        sortStrings.put(R.id.forum_posts_sort_time_desc, new String[] {"originalPostTime,desc"});
        sortStrings.put(R.id.forum_posts_sort_time_asc, new String[] {"originalPostTime,asc"});
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

        if (this.topic != null && !this.topic.isRead()) {
            // If this topic contains unread posts, monitor of user is reading those posts

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager layout = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layout == null) return;

                    if(isLastReadVisible(layout)) {
                        Post markRead = getPostAt(layout.findFirstCompletelyVisibleItemPosition());
                        if(markRead != null && !markRead.getId().equals(isReadingPost) && !topic.isRead(markRead)) {
                            isReadingPost = markRead.getId();
                            startReadTimer(markRead);
                        }
                    } else  {
                        cancelReadTimer();
                    }
                }
            });
        }

        return view;
    }

    private @Nullable Post getPostAt(int index) {
        ForumPostAdapter.ForumPostViewHolder h = getViewHolderAt(index);
        if (h == null) return null;
        return h.getPost();
    }

    private @Nullable ForumPostAdapter.ForumPostViewHolder getViewHolderAt(int index) {
        if(index == RecyclerView.NO_POSITION) return null;
        View child = recyclerView.getChildAt(index);
        if (child == null) return null;
        return (ForumPostAdapter.ForumPostViewHolder) recyclerView.getChildViewHolder(child);
    }

    private boolean isLastReadVisible(LinearLayoutManager layoutManager) {
        if(topic != null) {
            if(topic.getLastRead() == null) {
                Post lastPost = getAdapter().getItems().get(getAdapter().getItemCount() - 1);
                if(lastPost != null) {
                    Post lastVisible = getPostAt(layoutManager.findLastCompletelyVisibleItemPosition());
                    return lastPost.equals(lastVisible);
                }
            } else {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    Post post = getPostAt(i);
                    if (post != null && post.getId().equals(topic.getLastRead().getId())) {
                        View lastReadView = layoutManager.getChildAt(i);
                        if (lastReadView != null) {
                            return layoutManager.isViewPartiallyVisible(lastReadView, false, true) ||
                                    layoutManager.isViewPartiallyVisible(lastReadView, true, true);
                        }
                    }
                }
            }
        }
        return false;
    }

    private void cancelReadTimer() {
        if(this.markAsReadTask != null) {
            this.markAsReadTask.cancel();
            this.markAsReadTask = null;
        }
        isReadingPost = null;
    }

    private void startReadTimer(Post post) {
        cancelReadTimer();
        markAsReadTask = new TimerTask() {
            @Override
            public void run() {
                topic.setLastRead(post);
                updateReadStatus(true);
                if(post.equals(getAdapter().getItems().get(0))) {
                    markTopicAsRead();
                }
            }
        };
        markAsReadTimer.schedule(markAsReadTask, MARK_AS_READ_DELAY);
    }

    private void markTopicAsRead() {
        Services.getInstance().forumService.markRead(forumId, topicId).enqueue(new Callback<ActionResponse<Boolean>>() {
            @Override
            public void onSucces(Response<ActionResponse<Boolean>> response) {
                if(response != null && response.body() != null && response.body().payload) {
                    updateReadStatus(false);
                }
            }
        });
    }

    private void updateReadStatus(boolean tentative) {
        for(int i = 0; i < recyclerView.getChildCount(); i++) {
            ForumPostAdapter.ForumPostViewHolder vh = getViewHolderAt(i);
            if(vh != null) {
                vh.updateRead(tentative);
            }
        }
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

        menu.findItem(R.id.forum_post_mark_topic_as_read).setOnMenuItemClickListener(item -> {
            markTopicAsRead();
            if(topic != null) {
                topic.setLastRead(getAdapter().getItems().get(0));
            }
            return false;
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

    @Override
    public void onPause() {
        super.onPause();
        cancelReadTimer();
    }
}
