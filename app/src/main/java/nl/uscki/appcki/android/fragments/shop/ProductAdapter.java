package nl.uscki.appcki.android.fragments.shop;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;
import java.util.Random;

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

        Drawable placeholder = AppCompatResources.getDrawable(holder.mView.getContext(), R.drawable.animated_uscki_logo_black);
        if(placeholder instanceof Animatable)
            ((Animatable) placeholder).start();

        String imageUri = product.image == null ? "" : MediaAPI.getMediaUrl(product.image);
        Glide.with(holder.mView)
                .load(imageUri)
                .placeholder(placeholder)
                .error(Glide.with(holder.mView)
                        .load("https://thecatapi.com/api/images/get?format=src&rnd=" + product.getRnd())
                        .placeholder(placeholder))
                .into(holder.image);

        if(product.stock == null || product.stock < 1) {
            // Disabling FABs is hard
            holder.product_order.setBackgroundTintList(ColorStateList.valueOf(holder.mView.getResources().getColor(R.color.lb_grey)));
            holder.product_order.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
            holder.product_order.setOnClickListener(null);
        } else {
            holder.product_order.setOnClickListener(v -> {
                ShopPreferenceHelper shopPreferenceHelper =
                        new ShopPreferenceHelper(holder.mView.getContext());
                if (shopPreferenceHelper.getShowConfirm()) {
                    showConfirmDialog(product, holder);
                } else {
                    placeOrder(product, holder);
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

        TextView name;
        ImageView image;
        TextView stock;
        FloatingActionButton product_order;
        TextView price;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            name = view.findViewById(R.id.product_name);
            image = view.findViewById(R.id.product_image);
            stock = view.findViewById(R.id.product_stock);
            product_order = view.findViewById(R.id.product_order);
            price = view.findViewById(R.id.priceText);
        }
    }
}
