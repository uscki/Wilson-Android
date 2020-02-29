package nl.uscki.appcki.android.fragments.shop;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.shop.Store;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreSelectionFragment extends RefreshableFragment {
    public StoreSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.currentScreen = MainActivity.Screen.STORE_SELECTION;

        setAdapter(new StoreAdapter(new ArrayList<Store>()));

        Services.getInstance().shopService.getStores().enqueue(new Callback<List<Store>>() {
            @Override
            public void onSucces(Response<List<Store>> response) {
                getAdapter().update(response.body());
                ShopPreferenceHelper shopPreferenceHelper = new ShopPreferenceHelper(getActivity());
                Gson gson = new Gson();
                shopPreferenceHelper.updateShops(gson.toJson(response.body()));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().shopService.getStores().enqueue(new Callback<List<Store>>() {
            @Override
            public void onSucces(Response<List<Store>> response) {
                getAdapter().update(response.body());
            }
        });
    }
}
