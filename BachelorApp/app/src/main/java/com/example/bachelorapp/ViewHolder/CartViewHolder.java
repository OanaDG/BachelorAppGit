package com.example.bachelorapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bachelorapp.Interfaces.ItemClickListener;
import com.example.bachelorapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvTitle, tvAuthor, tvPrice;
    public ImageView imgBook;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.tvBookCartTitle);
        tvAuthor = itemView.findViewById(R.id.tvBookCartAuthor);
        tvPrice = itemView.findViewById(R.id.tvBookCartPrice);
        imgBook = itemView.findViewById(R.id.imgBookCart);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
