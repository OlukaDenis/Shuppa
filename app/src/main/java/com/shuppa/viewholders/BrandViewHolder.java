package com.shuppa.viewholders;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuppa.R;

public class BrandViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "BrandViewHolder";
    public TextView brandName;

    public BrandViewHolder(@NonNull View itemView) {
        super(itemView);
        brandName = itemView.findViewById(R.id.brand_name);
    }

    public void bindBrand(String brand) {
        Log.d(TAG, "bindBrand called...: ");
        brandName.setText(brand);
        Log.d(TAG, "bindBrand: "+brand);
//        brandName.setBackground(context.getResources().getDrawable(R.drawable.varible_bg));
    }
}