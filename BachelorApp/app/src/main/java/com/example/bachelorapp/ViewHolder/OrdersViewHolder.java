package com.example.bachelorapp.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bachelorapp.Interfaces.ItemClickListener;
import com.example.bachelorapp.R;

public class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvUsername, tvPhone, tvTotalPrice, tvDateTime, tvAddress;
    public Button btnShowProducts;
    public ItemClickListener listener;

    public OrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        tvUsername = itemView.findViewById(R.id.tvUsernameOrder);
        tvPhone = itemView.findViewById(R.id.tvPhoneOrder);
        tvAddress = itemView.findViewById(R.id.tvAddressOrder);
        tvDateTime = itemView.findViewById(R.id.tvDateTime);
        tvTotalPrice = itemView.findViewById(R.id.tvPriceOrder);
        btnShowProducts = itemView.findViewById(R.id.btnShowProducts);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }


    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
