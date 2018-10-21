package nl.uscki.appcki.android.fragments.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.generated.shop.Store;
import retrofit2.Response;

public class StoreFragment extends RefreshableFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(new ProductAdapter(new ArrayList<Product>()));

        Services.getInstance().shopService.getProductsForStore(1).enqueue(new Callback<Pageable<Product>>() {
            @Override
            public void onSucces(Response<Pageable<Product>> response) {
                getAdapter().update(response.body().getContent());
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
        Services.getInstance().shopService.getProductsForStore(1).enqueue(new Callback<Pageable<Product>>() {
            @Override
            public void onSucces(Response<Pageable<Product>> response) {
                getAdapter().update(response.body().getContent());
            }
        });
    }
}
