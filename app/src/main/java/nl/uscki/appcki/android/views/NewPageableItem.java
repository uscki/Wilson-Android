package nl.uscki.appcki.android.views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.helpers.WrongTextfieldHelper;
import retrofit2.Call;
import retrofit2.Response;

public abstract class NewPageableItem<T extends ActionResponse> extends Fragment {
    protected PageableFragment parent;

    private boolean focusOnCreateView = false;

    public void setFocusOnCreateView(boolean focusOnCreateView) {
        this.focusOnCreateView = focusOnCreateView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set confirm button behavior
        getConfirmButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<View> incorrectViews = getIncorrectFields();
                if(incorrectViews.isEmpty()) {
                    postNewItem().enqueue(getPostNewItemCallback());
                } else {
                    WrongTextfieldHelper.alertIncorrectViews(getContext(), incorrectViews);
                }
            }
        });

        if(focusOnCreateView) {
            Utils.toggleKeyboardForEditBox(getContext(), getMainTextInput(), true);
        }
    }

    @Override
    public void onDestroy() {
        if(parent != null && parent.getView() != null && focusOnCreateView) {
            // New Item fragment deleted. Re-enable FAB
            parent.setFabEnabled(parent.getView(), true);
        }
        super.onDestroy();
    }

    /**
     * Set a reference to the PageableFragment, which is the parent of this
     * class
     * @param parent Reference to the PageableFragment to which this class belongs
     */
    public void setParent(PageableFragment parent) {
        this.parent = parent;
    }

    protected Callback<T> getPostNewItemCallback() {
        return new Callback<T>() {
            @Override
            public void onSucces(Response response) {
                if (parent != null) parent.refresh();
                cleanupAfterPost();
            }
        };
    }

    protected abstract EditText getMainTextInput();
    protected abstract ImageButton getConfirmButton();
    protected abstract Call<T> postNewItem();

    /**
     * Obtain a list of input fragments in this fragment for which
     * the user-set value is incorrect
     *
     * @return List of fragments that are invalid, or empty if entire
     * input is accepted
     */
    protected abstract @NonNull List<View> getIncorrectFields();

    /**
     * Check if an edit text field contains at least some value
     * @param textField     Edit Text field to check
     * @return              Boolean, true iff not empty
     */
    protected boolean isFieldNotEmpty(EditText textField) {
        return !textField.getText().toString().trim().isEmpty();
    }

    protected void cleanupAfterPost() {
        Utils.toggleKeyboardForEditBox(getContext(), getMainTextInput(), false);
        if(focusOnCreateView) {
            hide();
        } else {
            getMainTextInput().setText("");
        }
    }

    protected void hide() {
        if(parent != null) {
            parent.removeNewPageableItemWidget();
            parent.onSwipeRefresh();
        } else {
            Log.e(getClass().getSimpleName(), "Trying to call methods on parent class, which is null");
        }
    }
}
