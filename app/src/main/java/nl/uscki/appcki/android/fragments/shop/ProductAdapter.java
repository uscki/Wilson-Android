package nl.uscki.appcki.android.fragments.shop;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

public class ProductAdapter extends BaseItemAdapter<ProductAdapter.ViewHolder, Product> {
    public ProductAdapter(List<Product> productList) {
        super(productList);
    }

    public ViewHolder onCreateCustomViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindCustomViewHolder(final ViewHolder holder, int position) {
        final Product product = items.get(position);

        holder.name.setText(product.title);
        holder.stock.setText(String.format(Locale.getDefault(), "%d", product.stock));

        if (product.image != null)
            holder.image.setImageURI(MediaAPI.getMediaUri(product.image));
        else
            holder.image.setImageURI("http://thecatapi.com/api/images/get?format=src");

        holder.product_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //holder.product_order.setImageResource();
                //holder.productOrderLoading.setVisibility(View.VISIBLE);
                Services.getInstance().shopService.placeOrder(product.id, 1).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onSucces(Response<Boolean> response) {
                        if (response.body()) {
                            UserHelper.getInstance().addPreferedProduct(product);
                            holder.stock.setText(String.format(Locale.getDefault(), "%d", product.stock - 1));
                            Toast.makeText(v.getContext(), "Je hebt 1 " + product.title + " besteld!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Bestelling mislukt!", Toast.LENGTH_SHORT).show();
                        }
                        //holder.productOrderLoading.setVisibility(View.GONE);
                    }
                });
            }
        });
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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
