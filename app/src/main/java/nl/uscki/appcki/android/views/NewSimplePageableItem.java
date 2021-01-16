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
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public abstract class NewSimplePageableItem<X extends IWilsonBaseItem> extends NewPageableItem<X> {
    BBEditView bbEditView;
    ImageButton confirmPostInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_pageable_item_single_input, container, false);

        confirmPostInput = view.findViewById(R.id.confirmNewQuoteButton);

        Bundle arg = new Bundle();
        arg.putInt(BBEditView.ARG_ALLOWED_TAGS, getTagCollection());

        this.bbEditView = new BBEditView();
        this.bbEditView.setArguments(arg);
        this.bbEditView.setEditBoxLabel(getHint());

        this.bbEditView.registerViewListener(new BBEditView.BBEditViewCreatedListener() {
            @Override
            public void onBBEditViewCreated(BBEditView editView, View view) {
                if(isFocusOnCreateView())
                    Utils.toggleKeyboardForEditBox(getContext(), editView.getEditBox(), true);
                editView.getEditBox().setMinLines(1);
            }

            @Override
            public void onBBEditViewDestroy(BBEditView editView) {
                editView.deregisterViewListener(this);
            }
        });

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.single_item_input_edit_text_placeholder, this.bbEditView)
                .commitNow();

        return view;
    }

    @Override
    protected EditText getMainTextInput() {
        return this.bbEditView == null ? null : this.bbEditView.getEditBox();
    }

    @Override
    protected ImageButton getConfirmButton() {
        return confirmPostInput;
    }

    @NonNull
    @Override
    protected List<View> getIncorrectFields() {
        List<View> incorrect = new ArrayList<>();
        if(!isFieldNotEmpty(this.bbEditView.getEditBox()))
            incorrect.add(this.bbEditView.getEditBox());
        return incorrect;
    }

    /**
     * @return  Resource ID of string containing hint for the single edit box
     */
    protected abstract int getHint();

    /**
     * @return The tag collection in R.arrays to use for this BB edit view
     */
    protected abstract int getTagCollection();
}
