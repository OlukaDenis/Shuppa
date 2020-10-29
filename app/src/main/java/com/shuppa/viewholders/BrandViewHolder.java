package com.shuppa.viewholders;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuppa.R;
import com.shuppa.data.model.Brand;
import com.shuppa.data.model.Product;

public class BrandViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "BrandViewHolder";
    public TextView brandName;

    public BrandViewHolder(@NonNull View itemView) {
        super(itemView);
        brandName = itemView.findViewById(R.id.brand_name);
    }

    public void bindBrand(Brand brand) {
        Log.d(TAG, "bindBrand called...: ");
        brandName.setText(brand.getName());
        Log.d(TAG, "bindBrand: "+brand);
//        brandName.setBackground(context.getResources().getDrawable(R.drawable.varible_bg));
    }
}