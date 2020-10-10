package com.shuppa.viewholders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.shuppa.R;
import com.shuppa.data.model.ProductSlider;

import butterknife.ButterKnife;

public class ProductSliderViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ProductSliderViewHolder";

    private ImageView sliderImage;

    public ProductSliderViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        sliderImage = itemView.findViewById(R.id.slider_image);
    }

    public void bindSlider(ProductSlider slider) {
        Picasso.get()
                .load(slider.getImage())
                .error(R.drawable.ic_baseline_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(sliderImage);
    }
}
