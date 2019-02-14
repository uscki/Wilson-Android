package nl.uscki.appcki.android.fragments.quotes;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.views.NewSimplePageableItem;
import retrofit2.Call;

public class NewQuoteWidget extends NewSimplePageableItem {

    @Override
    protected Call postNewItem() {
        return Services.getInstance()
                .quoteService
                .newQuote(getMainTextInput().getText().toString());
    }

    @Override
    protected int getHint() {
        return R.string.quote;
    }
}
