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
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.generated.shop.Store;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

public class StoreFragment extends PageableFragment<Pageable<Product>> {

    private static final int PRODUCT_LIST_SIZE = 5;

    private Integer storeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.currentScreen = MainActivity.Screen.STORE_BUY;
        setHasOptionsMenu(true);

        this.storeId = getArguments().getInt("id", -1);

        if (this.storeId != -1) {
            Services.getInstance().shopService.getProductsForStore(storeId, page, getPageSize()).enqueue(callback);
        }

        setAdapter(new ProductAdapter(new ArrayList<Product>()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.main, menu);
        inflater.inflate(R.menu.shop_menu, menu);

        menu.findItem(R.id.order_history_menu_item).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                EventBus.getDefault().post(new OpenFragmentEvent(new OrderHistoryFragment(), null));
                return true;
            }
        });
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().shopService.getProductsForStore(this.storeId, page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().shopService.getProductsForStore(this.storeId, page, getPageSize()).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.shop_products_empty);
    }

    @Override
    protected int getPageSize() {
        return PRODUCT_LIST_SIZE;
    }
}
