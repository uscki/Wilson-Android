package nl.uscki.appcki.android.fragments.shop;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.fragments.agenda.AgendaDeelnemersAdapter;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipant;
import nl.uscki.appcki.android.generated.shop.Store;
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

        Services.getInstance().shopService.getStores().enqueue(new Callback<List<Store>>() {
            @Override
            public void onSucces(Response<List<Store>> response) {
                getAdapter().update(response.body());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof AgendaActivity) {
            StoreAdapter adapter = new StoreAdapter(new ArrayList<Store>());
            setAdapter(adapter);
        }
        super.onAttach(context);
    }

    @Override
    public void onSwipeRefresh() {

    }
}
