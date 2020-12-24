package nl.uscki.appcki.android.fragments.media.captioncontest;

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
import nl.uscki.appcki.android.fragments.media.adapters.CaptionContestHistoryAdapter;
import nl.uscki.appcki.android.generated.captioncontest.CaptionContest;

public class CaptionContestHistoryFragment extends PageableFragment<CaptionContestHistoryAdapter.ViewHolder, CaptionContest> {

    private static final int CAPTION_CONTEST_PAGE_SIZE = 5;
    private String[] sort = new String[]{"startdate,desc"};
    private Menu menu;

    public CaptionContestHistoryFragment() {
        // Required empty public constructor
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

            CaptionContestHistoryFragment.this.refresh();

            return true;
        }
    };

    private static final SparseArray<String[]> sortStrings;
    static
    {
        sortStrings = new SparseArray<>();
        sortStrings.put(R.id.caption_contest_sort_time_desc, new String[] {"startdate,desc"}); //default
        sortStrings.put(R.id.caption_contest_sort_time_asc, new String[] {"startdate,asc"});
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setAdapter(new CaptionContestHistoryAdapter(new ArrayList<>()));
        onScrollRefresh();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        inflater.inflate(R.menu.caption_contest_history_menu, menu);

        for(int i = 0; i < sortStrings.size(); i++) {
            menu.findItem(sortStrings.keyAt(i)).setOnMenuItemClickListener(sortListener);
        }
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().captionContestService.getCaptionContests(this.page, this.getPageSize(), sort).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().captionContestService.getCaptionContests(this.page, this.getPageSize(), sort).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return "Geen captioncontests gevonden"; // TODO string resources
    }

    @Override
    protected int getPageSize() {
        return CAPTION_CONTEST_PAGE_SIZE;
    }
}