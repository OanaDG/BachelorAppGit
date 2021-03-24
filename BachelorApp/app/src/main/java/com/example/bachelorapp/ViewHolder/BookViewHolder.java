package com.example.bachelorapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bachelorapp.Interfaces.ItemClickListener;
import com.example.bachelorapp.R;

public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvBookTitle, tvBookAuthor, tvBookDescription, tvBookPrice;
    public ImageView imgBook;
    public ItemClickListener listener;
    public BookViewHolder(@NonNull View itemView) {
        super(itemView);

        imgBook = itemView.findViewById(R.id.imgBook);
        tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
        tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
        tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
        tvBookDescription = itemView.findViewById(R.id.tvBookDescription);
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
