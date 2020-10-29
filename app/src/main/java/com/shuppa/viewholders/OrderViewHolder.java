package com.shuppa.viewholders;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuppa.R;
import com.shuppa.data.interfaces.CustomItemClickListener;
import com.shuppa.data.model.Order;
import com.shuppa.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "OrderViewHolder";

    @BindView(R.id.tv_order_name)
    TextView orderName;

    @BindView(R.id.tv_order_amount)
    TextView orderAmount;

    @BindView(R.id.tv_order_date)
    TextView orderDate;

    @BindView(R.id.order_status)
    TextView orderStatus;

    private CustomItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindOrderTo(Order order) {
        Log.d(TAG, "bindOrderTo: "+order.getDateAdded());
        orderAmount.setText(AppUtils.formatCurrency(order.getTotal()));
        orderName.setText(order.getOrder_number());
        orderDate.setText(order.getDateAdded());
        orderStatus.setText(order.getStatus());
    }


    public void setItemClickListener(CustomItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(v, getAdapterPosition());
    }
}