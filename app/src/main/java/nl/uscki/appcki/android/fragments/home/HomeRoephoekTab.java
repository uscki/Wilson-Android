package nl.uscki.appcki.android.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.ConnectException;
import java.util.ArrayList;

import nl.uscki.appcki.android.MainActivity;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.error.ConnectionError;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.adapters.RoephoekItemAdapter;
import nl.uscki.appcki.android.generated.roephoek.Roephoek;
import nl.uscki.appcki.android.generated.roephoek.RoephoekItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by peter on 11/23/16.
 */

public class HomeRoephoekTab extends PageableFragment {
    private final int ROEPHOEK_PAGE_SIZE = 7;
    private final int ROEPHOEK_CONSTANT = 100000000;

    private Callback<Roephoek> roephoekCallback = new Callback<Roephoek>() {
        @Override
        public void onResponse(Call<Roephoek> call, Response<Roephoek> response) {
            swipeContainer.setRefreshing(false);
            if(loading) {
                loading = false;
                if (getAdapter() instanceof RoephoekItemAdapter) {
                    getAdapter().addItems(response.body().getContent());
                }
            } else {
                if (getAdapter() instanceof RoephoekItemAdapter) {
                    if (response.body() != null) {
                        getAdapter().update(response.body().getContent());
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<Roephoek> call, Throwable t) {
            if (t instanceof ConnectException) {
                new ConnectionError(t); // handle connection error in MainActivity
            } else {
                throw new RuntimeException(t);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        setAdapter(new RoephoekItemAdapter(new ArrayList<RoephoekItem>()));
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE, ROEPHOEK_CONSTANT).enqueue(roephoekCallback);
        MainActivity.currentScreen = MainActivity.Screen.ROEPHOEK;

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.roephoek_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE, ROEPHOEK_CONSTANT).enqueue(roephoekCallback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().shoutboxService.older(page, ROEPHOEK_PAGE_SIZE, ROEPHOEK_CONSTANT).enqueue(roephoekCallback);
    }

}
