package nl.uscki.appcki.android.fragments.shop;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.generated.shop.Store;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Response;

import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.fragments.adapters.BaseItemAdapter;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;

public class ProductAdapter extends BaseItemAdapter<ProductAdapter.ViewHolder, Product> {

    private StoreFragment storeFragment;
    private int storeId;

    public ProductAdapter(List<Product> productList) {
        super(productList);
    }

    @Override
    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_product, parent, false);
        return new ViewHolder(view);
    }

    public void updateProduct(Product product) {
        int position = this.items.indexOf(product);
        this.items.set(position, product);
        this.notifyDataSetChanged();
    }

    public void setStoreInfo(StoreFragment storeFragment, int storeId) {
        this.storeFragment = storeFragment;
        this.storeId = storeId;
    }

    @Override
    public void onBindCustomViewHolder(final ViewHolder holder, int position) {
        final Product product = items.get(position);

        holder.name.setText(product.title);
        holder.stock.setText(String.format(Locale.getDefault(), "%d", product.stock));
        holder.price.setText(holder.mView.getResources().getString(R.string.shop_price_format, product.price));

        if (product.image != null)
            holder.image.setImageURI(MediaAPI.getMediaUri(product.image));
        else
            holder.image.setImageURI("http://thecatapi.com/api/images/get?format=src");

        if(product.stock == null || product.stock < 1) {
            // Disabling FABs is hard
            holder.product_order.setBackgroundTintList(ColorStateList.valueOf(holder.mView.getResources().getColor(R.color.lb_grey)));
            holder.product_order.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
            holder.product_order.setOnClickListener(null);
        } else {
            holder.product_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    ShopPreferenceHelper shopPreferenceHelper =
                            new ShopPreferenceHelper(holder.mView.getContext());
                    if (shopPreferenceHelper.getShowConfirm()) {
                        showConfirmDialog(product, holder);
                    } else {
                        placeOrder(product, holder);
                    }
                }
            });
        }
    }

    private void placeOrder(final Product product, final ViewHolder holder) {
        storeFragment.orderProduct(holder.mView.getContext(), product, 1);
    }

    private void showConfirmDialog(final Product product, final ViewHolder holder) {
        // Find fragment manager
        FragmentActivity activity = (FragmentActivity)holder.mView.getContext();
        FragmentManager manager = activity.getSupportFragmentManager();

        // Create confirm dialog
        ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog();

        // Create argument bundle
        Bundle confirmDialogArguments = new Bundle();
        confirmDialogArguments.putSerializable("storeFragment", storeFragment);
        confirmDialogArguments.putInt("storeId", storeId);
        confirmDialogArguments.putSerializable("product", product);

        // Set arguments on dialog, and show
        confirmOrderDialog.setArguments(confirmDialogArguments);
        confirmOrderDialog.show(manager, "confirm_order_dialog");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.product_name)
        TextView name;
        @BindView(R.id.product_image)
        SimpleDraweeView image;
        @BindView(R.id.product_stock)
        TextView stock;
        @BindView(R.id.product_order)
        FloatingActionButton product_order;
        @BindView(R.id.priceText)
        TextView price;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}