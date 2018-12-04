package nl.uscki.appcki.android.fragments.shop;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.RefreshableFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

public class StoreFragment extends RefreshableFragment implements Serializable {

    private int scrollToX = 0;

    Callback<Pageable<Product>> callback = new Callback<Pageable<Product>>() {
        @Override
        public void onSucces(Response<Pageable<Product>> response) {
            response.body().getContent().sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    return UserHelper.getInstance().isPreferedProduct(o1, o2);
                }
            });

            getAdapter().update(response.body().getContent());
            StoreFragment.this.swipeContainer.setRefreshing(false);
            swipeContainer.scrollTo(scrollToX, 0);
            scrollToX = 0;
        }
    };

    private Integer storeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.currentScreen = MainActivity.Screen.STORE_BUY;

        this.storeId = getArguments().getInt("id", -1);

        if (this.storeId != -1) {
            ShopPreferenceHelper shopPreferenceHelper = new ShopPreferenceHelper(getActivity());
            Services.getInstance().shopService.getProductsForStore(storeId).enqueue(callback);
            shopPreferenceHelper.setLastShop(storeId);
        }

        setHasOptionsMenu(true);
        ProductAdapter adapter = new ProductAdapter(new ArrayList<Product>());
        adapter.setStoreInfo(this, storeId);
        setAdapter(adapter);
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

    /**
     * Place an order. All orders should be handled here for consistent behavior
     * @param context
     * @param product
     * @param amount
     */
    public void orderProduct(final Context context, final Product product, final int amount) {
        Services.getInstance().shopService.placeOrder(product.id, amount).enqueue(new Callback<Boolean>() {
            @Override
            public void onSucces(Response<Boolean> response) {
                if(response.body()) {
                    UserHelper.getInstance().addPreferedProduct(product);
                    scrollToX = swipeContainer.getScrollX();
                    onSwipeRefresh();
                    Toast.makeText(
                            context,
                            context.getString(
                                    R.string.shop_msg_confirm_order,
                                    amount,
                                    product.title
                                    ),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            context,
                            context.getString(R.string.shop_msg_order_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
