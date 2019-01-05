package nl.uscki.appcki.android.fragments.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.MediaAPI;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.generated.shop.Store;
import nl.uscki.appcki.android.helpers.ShopPreferenceHelper;

public class ConfirmOrderDialog extends DialogFragment {

    @BindView(R.id.shop_confirm_shop_name)
    TextView storeName;

    @BindView(R.id.shop_confirm_product_name)
    TextView productName;

    @BindView(R.id.shop_item_price)
    TextView itemPrice;

    @BindView(R.id.shop_confirm_product_image)
    SimpleDraweeView productImage;

    @BindView(R.id.shop_confirm_amount)
    NumberPicker amountPicker;

    @BindView(R.id.shop_confirm_checkbox_do_not_show_again)
    CheckBox dontShowAgain;

    @BindView(R.id.shop_confirm_button_confirm)
    Button confirmButton;

    @BindView(R.id.shop_confirm_button_cancel)
    Button cancelButton;

    @BindView(R.id.shop_confirm_total_price)
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
        ButterKnife.bind(this, view);

        confirmButton.setOnClickListener(confirmOrderListener);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmOrderDialog.this.dismiss();
            }
        });

        amountPicker.setMinValue(1);
        amountPicker.setValue(1);
        amountPicker.setMaxValue(product.stock);

        storeName.setText(store.title);
        if(product.image != null) {
            productImage.setImageURI(MediaAPI.getMediaUri(product.image));
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
