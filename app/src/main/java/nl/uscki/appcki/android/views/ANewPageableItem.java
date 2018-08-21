package nl.uscki.appcki.android.views;

import android.content.Context;
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
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.helpers.WrongTextfieldHelper;
import retrofit2.Call;
import retrofit2.Response;

public abstract class ANewPageableItem extends Fragment {
    protected PageableFragment parent;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set confirm button behavior
        getConfirmButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getMainTextInput().getText().toString().trim().isEmpty()) {
                    WrongTextfieldHelper.alertEmptyTextfield(getContext(), getMainTextInput());
                } else {
                    postNewItem().enqueue(new Callback() {
                        @Override
                        public void onSucces(Response response) {
                            if (parent != null) parent.refresh();
                            hide();
                        }
                    });
                }
            }
        });

        focusNewItemInput();
    }

    @Override
    public void onDestroy() {
        if(parent != null && parent.getView() != null) {
            // New Item fragment deleted. Re-enable FAB
            parent.setFabEnabled(parent.getView(), true);
        } else {
            Log.e(getClass().getSimpleName(), "Can't reenable FAB, since view is already destroyed");
        }
        super.onDestroy();
    }

    public void setParent(PageableFragment parent) {
        this.parent = parent;
    }

    protected abstract EditText getMainTextInput();
    protected abstract ImageButton getConfirmButton();
    protected abstract Call postNewItem();

    protected void hide() {
        toggleKeyboard(false);
        if(parent != null) {
            parent.removeNewPageableItemWidget();
            parent.onSwipeRefresh();
        } else {
            Log.e(getClass().getSimpleName(), "Trying to call methods on parent class, which is null");
        }
    }

    private void focusNewItemInput() {
        getMainTextInput().setFocusableInTouchMode(true);
        getMainTextInput().requestFocus();
        getMainTextInput().requestFocusFromTouch();
        toggleKeyboard(true);
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
