package nl.uscki.appcki.android.views;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.helpers.WrongTextfieldHelper;
import retrofit2.Call;
import retrofit2.Response;

public abstract class NewPageableItem extends Fragment {
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
                    postNewItem().enqueue(new Callback() {
                        @Override
                        public void onSucces(Response response) {
                            if (parent != null) parent.refresh();
                            if(focusOnCreateView) {
                                hide();
                            } else {
                                getMainTextInput().setText("");
                            }
                        }
                    });
                } else {
                    WrongTextfieldHelper.alertIncorrectViews(getContext(), incorrectViews);
                }
            }
        });

        if(focusOnCreateView) {
            focusNewItemInput();
        }
    }

    @Override
    public void onDestroy() {
        if(parent != null && parent.getView() != null && focusOnCreateView) {
            // New Item fragment deleted. Re-enable FAB
            parent.setFabEnabled(parent.getView(), true);
        } else {
            Log.e(getClass().getSimpleName(), "Can't reenable FAB, since view is already destroyed");
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

    protected abstract EditText getMainTextInput();
    protected abstract ImageButton getConfirmButton();
    protected abstract Call postNewItem();

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

    protected void hide() {
        toggleKeyboard(false);
        if(parent != null) {
            parent.removeNewPageableItemWidget();
            parent.onSwipeRefresh();
        } else {
            Log.e(getClass().getSimpleName(), "Trying to call methods on parent class, which is null");
        }
    }

    public void focusNewItemInput() {
        getMainTextInput().setFocusableInTouchMode(true);
        if(getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            getMainTextInput().requestFocusFromTouch();
        } else {
            getMainTextInput().requestFocus();
            toggleKeyboard(true);
        }
    }

    private void toggleKeyboard(boolean show) {
        Context context = getContext();
        if(context != null) {
            View view = getMainTextInput().getRootView();

            InputMethodManager iim =
                    (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);

            // Avoiding null pointers is better than showing keyboard, so in case if null: ignore
            if(iim != null && view != null) {
                IBinder windowToken = view.getWindowToken();
                if (show) {
                    iim.toggleSoftInputFromWindow(windowToken, InputMethodManager.SHOW_IMPLICIT, 0);
                } else {
                    iim.hideSoftInputFromWindow(windowToken, 0);
                }
            }
        }
    }
}
