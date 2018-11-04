package nl.uscki.appcki.android.fragments.shop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.generated.shop.Store;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

public class StoreFragment extends RefreshableFragment {
    Callback<Pageable<Product>> callback = new Callback<Pageable<Product>>() {
        @Override
        public void onSucces(Response<Pageable<Product>> response) {
            Log.e("storefragment", "test");
            response.body().getContent().sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    return UserHelper.getInstance().isPreferedProduct(o1, o2);
                }
            });
            for (Product product : response.body().getContent()) {
                Log.e("storeFragment", product.title);
            }
            getAdapter().update(response.body().getContent());
            StoreFragment.this.swipeContainer.setRefreshing(false);
        }
    };

    private Integer storeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.currentScreen = MainActivity.Screen.STORE_BUY;

        this.storeId = getArguments().getInt("id", -1);

        if (this.storeId != -1) {
            Services.getInstance().shopService.getProductsForStore(storeId).enqueue(callback);
            ShopPreferenceHelper shopPreferenceHelper = new ShopPreferenceHelper(getActivity());
            shopPreferenceHelper.setLastShop(storeId);
        }

        setHasOptionsMenu(true);

        setAdapter(new ProductAdapter(new ArrayList<Product>()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().shopService.getProductsForStore(this.storeId).enqueue(callback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        inflater.inflate(R.menu.menu_shops, menu);

        MenuItem changeShop = menu.findItem(R.id.shops_change_shop);
        changeShop.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                EventBus.getDefault().post(new OpenFragmentEvent(new StoreSelectionFragment(), null));
                return true;
            }
        });

    }
}
