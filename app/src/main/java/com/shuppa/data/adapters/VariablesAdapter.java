package com.shuppa.data.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuppa.R;
import com.shuppa.data.model.Cart;
import com.shuppa.data.model.Product;
import com.shuppa.data.model.Variable;
import com.shuppa.utils.AppUtils;
import com.shuppa.viewholders.ProductViewHolder;
import com.shuppa.viewholders.VariableViewHolder;

import java.util.List;

public class VariablesAdapter extends RecyclerView.Adapter<VariableViewHolder> {
    private static final String TAG = "VariablesAdapter";
    private List<Variable> variableList;
    private Activity activity;
    private int index = -1;
    private int modifiedAmount;
    private ProductViewHolder productViewHolder;
    private Product product;

    public VariablesAdapter(List<Variable> variableList, Activity activity, ProductViewHolder productViewHolder, Product product) {
        this.variableList = variableList;
        this.activity = activity;
        this.productViewHolder = productViewHolder;
        this.product = product;
    }

    @NonNull
    @Override
    public VariableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_variable, parent, false);
        return new VariableViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull VariableViewHolder holder, int position) {
        Variable variable = variableList.get(position);

        holder.variableName.setOnClickListener(view -> {
            index = position;
            notifyDataSetChanged();
            calculatePrice(variable);
        });

        if (index == position) {
            holder.variableName.setBackground(activity.getResources().getDrawable(R.drawable.variable_filled_bg));
            holder.variableName.setTextColor(activity.getResources().getColor(R.color.white));
        } else {
            holder.variableName.setBackground(activity.getResources().getDrawable(R.drawable.varible_bg));
            holder.variableName.setTextColor(activity.getResources().getColor(R.color.black));
        }

        if (!product.isSimple()) {
            productViewHolder.addToCart.setOnClickListener(v -> {
                productViewHolder.loading.show();
                int mAmount = modifiedAmount * productViewHolder.value;

                Cart cart = new Cart(
                        product.getCategory_id(),
                        product.getCategory_name(),
                        product.getUuid(),
                        product.getName(),
                        product.getImage(),
                        product.getMrp() * productViewHolder.value,
                        productViewHolder.value,
                        mAmount
                );
                productViewHolder.addProductCart(cart, product);
            });
        }

        if (variable != null) {
            holder.bindVariable(variable);
        }
    }

    @Override
    public int getItemCount() {
        return variableList == null ? 0 : variableList.size();
    }


    private void calculatePrice(Variable model) {
        if (product.isOffer()) {
            int newMrp = model.getPrice() + 1000;
            double discount = (product.getOffer_value() * newMrp) / 100;
            double actual = newMrp - discount;
            int m = (int) actual;
            //update the amount
            modifiedAmount = (int) actual;
            productViewHolder.productPrice.setText(AppUtils.formatCurrency(m));
        } else {
            productViewHolder.productPrice.setText(AppUtils.formatCurrency(model.getPrice()));
            modifiedAmount = model.getPrice();
        }
    }

}
