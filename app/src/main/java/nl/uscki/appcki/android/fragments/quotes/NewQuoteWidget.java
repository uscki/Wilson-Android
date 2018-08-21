package nl.uscki.appcki.android.fragments.quotes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.views.ANewPageableItem;
import retrofit2.Call;

public class NewQuoteWidget extends ANewPageableItem {

    @BindView(R.id.quoteTextInput)
    EditText quoteInput;

    @BindView(R.id.confirmNewQuoteButton)
    ImageButton confirmAddQuoteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quote_new_widget, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected EditText getMainTextInput() {
        return quoteInput;
    }

    @Override
    protected ImageButton getConfirmButton() {
        return confirmAddQuoteButton;
    }

    @Override
    protected Call postNewItem() {
        return Services.getInstance()
                .quoteService
                .newQuote(quoteInput.getText().toString(), false);
    }

}
