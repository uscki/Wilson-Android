package nl.uscki.appcki.android.views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import nl.uscki.appcki.android.activities.BasicActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.generated.IWilsonBaseItem;
import retrofit2.Call;
import retrofit2.Response;

public abstract class ANewPageableItem extends Fragment {
    protected PageableFragment parent;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set cancel button behavior
        getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

        // Set confirm button behavior
        getConfirmButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postNewItem().enqueue(new Callback() {
                    @Override
                    public void onSucces(Response response) {
                        parent.refresh();
                        hide();
                    }
                });
            }
        });

        focusNewItemInput();

        /* TODO remove this fragment if back pressed? Idea's?
        Method:
            1) Add public method to pageableFragment that keeps track of wether a add thingy is open
            2) OnBackPressed from the base activity, call a function on pageablefragment that returns
                true if it can close something false otherwise
            3) If false is returned, continue normal backpressed procedure, otherwise, cancel

        TODO Wait until feature/better-back-navigation is merged, cause this will clash
         */


    }

    public void setParent(PageableFragment parent) {
        this.parent = parent;
    }

    protected abstract EditText getMainTextInput();
    protected abstract Button getConfirmButton();
    protected abstract Button getCancelButton();
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
