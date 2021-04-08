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
import nl.uscki.appcki.android.fragments.forum.adapter.ForumOverviewAdapter;
import nl.uscki.appcki.android.generated.forum.Forum;

public class ForumOverviewFragment extends PageableFragment<ForumOverviewAdapter.ForumOverviewViewHolder, Forum> {

    private static final int FORUM_PAGE_SIZE = 10;

    public ForumOverviewFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(new ForumOverviewAdapter(new ArrayList<>()));
        onSwipeRefresh();
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
        Services.getInstance().forumService.getFora(page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().forumService.getFora(page, getPageSize()).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.wilson_media_forum_fora_empty_text);
    }

    @Override
    protected int getPageSize() {
        return FORUM_PAGE_SIZE;
    }
}
