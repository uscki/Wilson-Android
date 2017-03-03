package nl.uscki.appcki.android.fragments.quotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.QUOTE_OVERVIEW;

        setAdapter(new QuoteAdapter(new ArrayList<Quote>()));
        Services.getInstance().quoteService.older(page, getPageSize()).enqueue(callback);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getPageSize() {
        return QUOTES_PAGE_SIZE;
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().quoteService.older(page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().quoteService.older(page, getPageSize()).enqueue(callback);
    }
}
