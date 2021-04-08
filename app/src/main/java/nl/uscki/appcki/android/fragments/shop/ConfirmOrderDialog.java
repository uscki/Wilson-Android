package nl.uscki.appcki.android.fragments.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.generated.shop.Store;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;

public class ConfirmOrderDialog extends DialogFragment {

    TextView storeName;
    TextView productName;
    TextView itemPrice;
    ImageView productImage;
    NumberPicker amountPicker;
    CheckBox dontShowAgain;
    Button confirmButton;
    Button cancelButton;
    TextView totalPrice;

    Store store;
    Product product;
    StoreFragment storeFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopPreferenceHelper shopPreferenceHelper = new ShopPreferenceHelper(getActivity());
        if(getArguments() != null) {
            storeFragment = (StoreFragment) getArguments().getSerializable("storeFragment");
            product = (Product) getArguments().getSerializable("product");
            store = shopPreferenceHelper.getStore(getArguments().getInt("storeId"));
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop_confirm_order_dialog, container);

        storeName = view.findViewById(R.id.shop_confirm_shop_name);
        productName = view.findViewById(R.id.shop_confirm_product_name);
        itemPrice = view.findViewById(R.id.shop_item_price);
        productImage = view.findViewById(R.id.shop_confirm_product_image);
        amountPicker = view.findViewById(R.id.shop_confirm_amount);
        dontShowAgain = view.findViewById(R.id.shop_confirm_checkbox_do_not_show_again);
        confirmButton = view.findViewById(R.id.shop_confirm_button_confirm);
        cancelButton = view.findViewById(R.id.shop_confirm_button_cancel);
        totalPrice = view.findViewById(R.id.shop_confirm_total_price);

        confirmButton.setOnClickListener(confirmOrderListener);
        cancelButton.setOnClickListener(view1 -> ConfirmOrderDialog.this.dismiss());

        amountPicker.setMinValue(1);
        amountPicker.setValue(1);
        amountPicker.setMaxValue(product.stock);

        storeName.setText(store.title);
        if(product.image != null) {
            Glide.with(this)
                    .load(MediaAPI.getMediaUri(product.image))
                    .into(productImage);
            productImage.setVisibility(View.VISIBLE);
        }
        productName.setText(product.title);
        itemPrice.setText(getResources().getString(R.string.shop_price_format_simple, product.price));

        totalPrice.setText(getResources().getString(R.string.shop_price_format_simple, amountPicker.getValue() * product.price));

        amountPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                totalPrice.setText(getResources().getString(R.string.shop_price_format_simple, i1 * product.price));
            }
        });

        return view;
    }

    View.OnClickListener confirmOrderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(dontShowAgain.isChecked()) {
                ShopPreferenceHelper shopPreferenceHelper = new ShopPreferenceHelper(getActivity());
                shopPreferenceHelper.setShowConfirm(false);
            }
            storeFragment.orderProduct(getActivity(), product, amountPicker.getValue());
            ConfirmOrderDialog.this.dismiss();
        }
    };
}
