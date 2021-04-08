package nl.uscki.appcki.android.fragments.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.forum.adapter.ForumRecentPostsAdapter;
import nl.uscki.appcki.android.generated.forum.RecentTopic;

public class ForumRecentPostsFragment extends PageableFragment<ForumRecentPostsAdapter.ViewHolder, RecentTopic> {

    private static final int PAGE_SIZE = 10;
    private static final String DEFAULT_SORT = "originalPostTime,desc";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(new ForumRecentPostsAdapter(new ArrayList<>()));
        onScrollRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(this.swipeContainer.getLayoutTransition() != null) {
            // animateLayoutChanges interferes with ViewPager2
            this.swipeContainer.getLayoutTransition().setAnimateParentHierarchy(false);
        }
        return view;
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().forumService.getRecent(this.page, getPageSize(), DEFAULT_SORT).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().forumService.getRecent(this.page, getPageSize(), DEFAULT_SORT).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.wilson_media_forum_recent_posts_empty_text);
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }
}
