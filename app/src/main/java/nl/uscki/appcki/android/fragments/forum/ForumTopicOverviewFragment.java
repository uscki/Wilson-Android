package nl.uscki.appcki.android.fragments.forum;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.forum.adapter.ForumTopicAdapter;
import nl.uscki.appcki.android.generated.forum.Topic;
import nl.uscki.appcki.android.helpers.UserHelper;

public class ForumTopicOverviewFragment extends PageableFragment<ForumTopicAdapter.ForumTopicViewHolder, Topic> {

    private static final int PAGE_SIZE = 5;
    public static final String ARG_FORUM_ID = "WILSON_FORUM_TOPIC_OVERVIEW_FRAGMENT_ARG_FORUM_ID";
    public static final String ARG_TOPIC_OBJ = "WILSON_FORUM_TOPIC_OVERVIEW_FRAGMENT_ARG_TOPIC";
    public static final String ARG_TOPIC_ID = "WILSON_FORUM_TOPIC_OVERVIEW_FRAGMENT_ARG_TOPIC_ID";

    private static final String SORT_ORDER_ASC = "asc";
    private static final String SORT_ORDER_DESC = "desc";
    private static final String SORT_KEY_TIME = "publishtimestamp";
    private static final String SORT_KEY_VIEWS = "views";
    private static final String SORT_KEY_LOCKED = "locked";
    private static final int CHECKED_MENU_ITEM_ICON = R.drawable.check;

    private int forumId = -1;
    private Menu menu;

    private String baseSort = SORT_ORDER_ASC;
    private String viewsSort = null;
    private String lockedSort = null;

    private static final SparseArray<String[]> sortStrings;
    static
    {
        sortStrings = new SparseArray<>();
        sortStrings.put(R.id.forum_topics_sort_posteddate_desc, new String[] {"posted,desc"}); //default
        sortStrings.put(R.id.forum_topics_sort_posteddate_asc, new String[] {"posted,asc"});
        sortStrings.put(R.id.forum_topics_sort_views_asc, new String[] {"views,asc"});
        sortStrings.put(R.id.forum_topics_sort_views_desc, new String[] {"views,desc"});
        sortStrings.put(R.id.forum_topics_sort_locked_asc, new String[] {"locked,asc"});
        sortStrings.put(R.id.forum_topics_sort_locked_desc, new String[] {"locked,desc"});
    }

    private MenuItem.OnMenuItemClickListener sortListener = menuItem -> {

        switch (menuItem.getItemId()) {
            case R.id.forum_topics_sort_posteddate_desc:
                baseSort = updateSortGroup(false, SORT_ORDER_DESC, baseSort, menuItem, R.id.forum_topics_sort_posteddate_asc);
                break;
            case R.id.forum_topics_sort_posteddate_asc:
                baseSort = updateSortGroup(false, SORT_ORDER_ASC, baseSort, menuItem, R.id.forum_topics_sort_posteddate_desc);
                break;
            case R.id.forum_topics_sort_views_asc:
                viewsSort = updateSortGroup(true, SORT_ORDER_ASC, viewsSort, menuItem, R.id.forum_topics_sort_views_desc);
                break;
            case R.id.forum_topics_sort_views_desc:
                viewsSort = updateSortGroup(true, SORT_ORDER_DESC, viewsSort, menuItem, R.id.forum_topics_sort_views_asc);
                break;
            case R.id.forum_topics_sort_locked_asc:
                lockedSort = updateSortGroup(true, SORT_ORDER_ASC, lockedSort, menuItem, R.id.forum_topics_sort_locked_desc);
                break;
            case R.id.forum_topics_sort_locked_desc:
                lockedSort = updateSortGroup(true, SORT_ORDER_DESC, lockedSort, menuItem, R.id.forum_topics_sort_locked_asc);
                break;
        }

        ForumTopicOverviewFragment.this.refresh();
        return true;
    };

    private String[] getSortString() {
        String[] sort = new String[4];
        sort[0] = "sticky,desc";
        sort[1] = lockedSort != null ? SORT_KEY_LOCKED + "," + lockedSort : null;
        sort[2] = viewsSort != null ? SORT_KEY_VIEWS + "," + viewsSort : null;
        sort[3] = SORT_KEY_TIME + "," + baseSort;
        return sort;
    }

    private String updateSortGroup(boolean canNull, String targetSortString, String currentSortString, MenuItem target, int... others) {
        for(int other : others) {
            menu.findItem(other).setIcon(null);
        }
        String sort = null;
        if(canNull && targetSortString.equals(currentSortString)) {
            target.setIcon(null);
        } else {
            target.setIcon(CHECKED_MENU_ITEM_ICON);
            sort = targetSortString;
        }
        return sort;
    }

    public ForumTopicOverviewFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setAdapter(new ForumTopicAdapter(new ArrayList<>(), this.forumId));
        if(getArguments() != null) {
            this.forumId = getArguments().getInt(ARG_FORUM_ID, -1);
        }
        if(UserHelper.getInstance().getCurrentUser().isForum_new_posts_first()) {
            baseSort = SORT_ORDER_DESC;
        }
        setHasOptionsMenu(true);
        onScrollRefresh();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forum_topics_sort_menu, menu);

        for(int i = 0; i < sortStrings.size(); i++) {
            menu.findItem(sortStrings.keyAt(i)).setOnMenuItemClickListener(sortListener);
        }

        if(UserHelper.getInstance().getCurrentUser().isForum_new_posts_first()) {
            menu.findItem(R.id.forum_topics_sort_posteddate_asc).setIcon(null);
            menu.findItem(R.id.forum_topics_sort_posteddate_desc).setIcon(CHECKED_MENU_ITEM_ICON);
        }

        this.menu = menu;
    }

    @Override
    public void onSwipeRefresh() {
        if(forumId > 0) {
            Services.getInstance().forumService.getTopics(this.forumId, this.page, this.getPageSize(), getSortString()).enqueue(callback);
        }
    }

    @Override
    public void onScrollRefresh() {
        if(forumId > 0) {
            Services.getInstance().forumService.getTopics(this.forumId, this.page, this.getPageSize(), getSortString()).enqueue(callback);
        }
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.wilson_media_forum_topic_empty_text);
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }
}
