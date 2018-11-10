package nl.uscki.appcki.android.fragments.shop;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
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
    public void onBindCustomViewHolder(ViewHolder holder, int position) {
        Order order = getItems().get(position);

        // TODO figure out a way to get the store name, and set it. Else:
        holder.storeNameContainer.setVisibility(View.GONE);

        if(order.getProduct() != null && !order.getProduct().trim().equals("")) {
            holder.productName.setText(order.getProduct());
        } else {
            holder.productNameContainer.setVisibility(View.GONE);
        }

        // TODO figure out a way to get product image, and set it. If empty:
        holder.productImage.setVisibility(View.GONE);

        holder.price.setText(String.format(
                Locale.getDefault(), "€%.2f", order.getPrice()));

        // TODO if amount becomes something, set it and the total price, otherwise:
        holder.amount.setText("1");
        holder.totalPrice.setText(String.format(
                Locale.getDefault(), "€%.2f", order.getPrice()));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.product_image)
        SimpleDraweeView productImage;

        @BindView(R.id.store_name_container)
        LinearLayout storeNameContainer;

        @BindView(R.id.product_name_container)
        LinearLayout productNameContainer;

        @BindView(R.id.price_container)
        LinearLayout priceContainer;

        @BindView(R.id.amount_container)
        LinearLayout amountContainer;

        @BindView(R.id.total_price_container)
        LinearLayout totalPriceContainer;

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

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
