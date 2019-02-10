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
import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.MainActivity;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.api.models.ActionResponse;
import nl.uscki.appcki.android.events.OpenFragmentEvent;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;
import retrofit2.Response;

public class StoreFragment extends PageableFragment<Pageable<Product>> implements Serializable {

    private static final int PRODUCT_PAGE_SIZE = 6;

    private Integer storeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.currentScreen = MainActivity.Screen.STORE_BUY;

        this.storeId = getArguments().getInt("id", -1);

        if (this.storeId != -1) {
            ShopPreferenceHelper shopPreferenceHelper = new ShopPreferenceHelper(getActivity());
            shopPreferenceHelper.setLastShop(this.storeId);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create an adapter for this fragment
        ProductAdapter adapter = new ProductAdapter(new ArrayList<Product>());
        adapter.setStoreInfo(this, this.storeId);
        setAdapter(adapter);

        // Load initial data
        onSwipeRefresh();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().shopService.getProductsForStore(this.storeId, this.page, getPageSize()).enqueue(this.callback);
    }


    @Override
    public void onScrollRefresh() {
        Services.getInstance().shopService.getProductsForStore(this.storeId, this.page, getPageSize()).enqueue(this.callback);
    }

    @Override
    public String getEmptyText() {
        return getResources().getString(R.string.shop_empty);
    }

    @Override
    protected int getPageSize() {
        return PRODUCT_PAGE_SIZE;
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
        Services.getInstance().shopService.placeOrder(storeId, product.id, amount).enqueue(new Callback<ActionResponse<Integer>>() {

            @Override
            public void onSucces(Response<ActionResponse<Integer>> response) {
                if(response.body() != null && response.body().payload != null) {
                    product.stock -= response.body().payload;
                    ((ProductAdapter) getAdapter()).updateProduct(product);
                    Toast.makeText(
                            context,
                            context.getString(
                                    R.string.shop_msg_confirm_order,
                                    amount,
                                    product.title,
                                    product.price * amount
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
