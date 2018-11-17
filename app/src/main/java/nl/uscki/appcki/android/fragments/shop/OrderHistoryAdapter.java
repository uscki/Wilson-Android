package nl.uscki.appcki.android.fragments.shop;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.Utils;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.shop.Order;

public class OrderHistoryAdapter extends BaseItemAdapter<OrderHistoryAdapter.ViewHolder, Order> {

    public OrderHistoryAdapter(List<Order> orderList) {super(orderList);}

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void update(List<Order> items) {
        this.items.clear();

        if(items.isEmpty()) return;

        Order last = items.get(0);

        for(int i = 1; i < items.size(); i++) {
            if(last.equals(items.get(i))) {
                last.increaseAmount();
            } else {
                this.items.add(last);
                last = items.get(i);
            }
        }

        this.items.add(last);
        notifyDataSetChanged();
    }

    @Override
    public void addItems(List<Order> items) {
        if(items.size() == 0) return;

        if(this.items.isEmpty()) {
            update(items);
            return;
        }

        Order last = items.remove(items.size() - 1);

        for(Order item : items) {
            if(last.equals(item)) {
                last.increaseAmount();
            } else {
                this.items.add(last);
                last = item;
            }
        }

        this.items.add(last);
        notifyDataSetChanged();
    }


    @Override
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        Order order = getItems().get(position);

        // TODO figure out a way to get the store name, and set it. Else:
        holder.storeName.setVisibility(View.GONE);

        holder.productName.setText(order.getProduct());

        // TODO figure out a way to get product image, and set it. If empty:
        holder.productImage.setVisibility(View.GONE);

        holder.price.setText(String.format(
                Locale.getDefault(), "€%.2f", order.getPrice()));

        holder.amount.setText(String.valueOf(order.getAmount()));
        holder.totalPrice.setText(String.format(
                Locale.getDefault(), "€%.2f", order.getPrice() * order.getAmount()));

        holder.date.setText(Utils.timestampConversion(order.getDate()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.order_history_product_image)
        SimpleDraweeView productImage;

        @BindView(R.id.store_name)
        TextView storeName;

        @BindView(R.id.product_name)
        TextView productName;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.amount)
        TextView amount;

        @BindView(R.id.total_price)
        TextView totalPrice;

        @BindView(R.id.order_date)
        TextView date;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
