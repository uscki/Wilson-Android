package nl.uscki.appcki.android.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.models.ActionResponse;

public abstract class NewSimplePageableItem<T extends ActionResponse> extends NewPageableItem<T> {
    EditText singleInput;
    ImageButton confirmPostInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_pageable_item_single_input, container, false);

        singleInput = view.findViewById(R.id.quoteTextInput);
        confirmPostInput = view.findViewById(R.id.confirmNewQuoteButton);

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
