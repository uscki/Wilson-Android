package nl.uscki.appcki.android.fragments.quotes;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.QuoteAdapter;
import nl.uscki.appcki.android.generated.quotes.Quote;
import nl.uscki.appcki.android.generated.quotes.QuotesPage;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class QuoteFragment extends PageableFragment<QuotesPage> {
    private final int QUOTES_PAGE_SIZE = 5;
    private String[] sort = new String[]{""};
    private Menu menu;

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

            QuoteFragment.this.refresh();

            return true;
        }
    };

    private static final SparseArray<String[]> sortStrings;
    static
    {
        sortStrings = new SparseArray<>();
        sortStrings.put(R.id.quote_sort_time_desc, new String[] {""}); //default
        sortStrings.put(R.id.quote_sort_time_asc, new String[] {"timestamp,asc"});
        sortStrings.put(R.id.quote_sort_toonkans_desc, new String[] {"weight,desc"});
        sortStrings.put(R.id.quote_sort_toonkans_asc, new String[] {"weight,asc"});
        sortStrings.put(R.id.quote_sort_positives_votes_desc, new String[] {"pos,desc", "neg,asc"});
        sortStrings.put(R.id.quote_sort_negative_votes_asc, new String[] {"neg,asc", "pos,desc"});
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        MainActivity.currentScreen = MainActivity.Screen.QUOTE_OVERVIEW;

        setAdapter(new QuoteAdapter(new ArrayList<Quote>()));
        Services.getInstance().quoteService.getQuotesCollection(page, getPageSize()).enqueue(callback);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = setFabEnabled(view, true);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewPageableItemWidget(new NewQuoteWidget(), true);
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.main, menu);
        inflater.inflate(R.menu.quote_menu, menu);

        for(int i = 0; i < sortStrings.size(); i++) {
            menu.findItem(sortStrings.keyAt(i)).setOnMenuItemClickListener(sortListener);
        }

        this.menu = menu;

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected int getPageSize() {
        return QUOTES_PAGE_SIZE;
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.no_quotes_found);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().quoteService.getQuotesCollection(page, getPageSize(), sort).enqueue(callback);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().quoteService.getQuotesCollection(page, getPageSize(), sort).enqueue(callback);
    }
}
