package nl.uscki.appcki.android.fragments.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.PageableFragment;
import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.shop.Order;

public class OrderHistoryFragment extends PageableFragment<Pageable<Order>> {

    private final int ORDERS_PAGE_SIZE = 10;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setAdapter(new OrderHistoryAdapter(new ArrayList<Order>()));
        Services.getInstance().shopService.getOrders(page, getPageSize()).enqueue(callback);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSwipeRefresh() {
        Services.getInstance().shopService.getOrders(page, getPageSize()).enqueue(callback);
    }

    @Override
    public void onScrollRefresh() {
        Services.getInstance().shopService.getOrders(page, getPageSize()).enqueue(callback);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.shop_order_history_empty);
    }

    @Override
    protected int getPageSize() {
        return ORDERS_PAGE_SIZE;
    }


}
