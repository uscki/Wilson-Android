package nl.uscki.appcki.android.fragments.quotes;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.generated.quotes.Quote;
import nl.uscki.appcki.android.views.NewSimplePageableItem;
import retrofit2.Call;

public class NewQuoteWidget extends NewSimplePageableItem<Quote> {

    @Override
    protected Call<ActionResponse<Quote>> postNewItem() {
        return Services.getInstance()
                .quoteService
                .newQuote(getMainTextInput().getText().toString());
    }

    @Override
    protected int getHint() {
        return R.string.quote;
    }

    @Override
    protected int getTagCollection() {
        return R.array.tag_collection_inline_tags;
    }
}
