package com.shuppa.viewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.shuppa.R;
import com.shuppa.data.model.Cart;
import com.shuppa.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "CartViewHolder";
    private LinearLayout plusMinusButton;
    public TextView plusButton;
    public TextView minusButton;
    public TextView total;

    @BindView(R.id.cart_image)
    ImageView cartImage;

    @BindView(R.id.cart_name)
    TextView cartName;

    @BindView(R.id.cart_category)
    TextView cartCategory;

    @BindView(R.id.cart_price)
    TextView cartPrice;

    public ImageButton removeCart;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        plusButton = itemView.findViewById(R.id.plus_btn);
        minusButton = itemView.findViewById(R.id.minus_btn);
        total = itemView.findViewById(R.id.counter_total);
        plusMinusButton = itemView.findViewById(R.id.plus_minus_button);
        removeCart = itemView.findViewById(R.id.remove_cart);
    }

    public void bindCart(Cart cart) {
        cartName.setText(cart.getProduct_name());
        cartCategory.setText(cart.getCategory_name());
        cartPrice.setText(AppUtils.formatCurrency(cart.getAmount()));
        total.setText(String.valueOf(cart.getQuantity()));
        Picasso.get()
                .load(cart.getProduct_image())
                .error(R.drawable.ic_baseline_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(cartImage);

    }


}

