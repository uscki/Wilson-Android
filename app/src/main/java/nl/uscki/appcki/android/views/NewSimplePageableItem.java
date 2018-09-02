package nl.uscki.appcki.android.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public abstract class NewSimplePageableItem<T extends IWilsonBaseItem> extends NewPageableItem<T> {
    @BindView(R.id.quoteTextInput)
    EditText singleInput;

    @BindView(R.id.confirmNewQuoteButton)
    ImageButton confirmPostInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_pageable_item_single_input, container, false);
        ButterKnife.bind(this, view);
        singleInput.setHint(getHint());
        return view;
    }

    @Override
    protected EditText getMainTextInput() {
        return singleInput;
    }

    @Override
    protected ImageButton getConfirmButton() {
        return confirmPostInput;
    }

    @NonNull
    @Override
    protected List<View> getIncorrectFields() {
        List<View> incorrect = new ArrayList<>();
        if(!isFieldNotEmpty(singleInput))
            incorrect.add(singleInput);
        return incorrect;
    }

    /**
     * @return  Resource ID of string containing hint for the single edit box
     */
    protected abstract int getHint();
}
