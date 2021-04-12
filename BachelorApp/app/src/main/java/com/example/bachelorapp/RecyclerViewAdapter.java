package com.example.bachelorapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bachelorapp.Model.Books;
import com.example.bachelorapp.ViewHolder.BookViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder> {

    Context context;
    List<Books> booksList;

    public RecyclerViewAdapter(Context context, List<Books> booksList) {
        this.context = context;
        this.booksList = booksList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout, parent, false);

        BookViewHolder viewHolder = new BookViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Books book = booksList.get(position);

        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText("by "+book.getAuthor());
        holder.tvBookPrice.setText(book.getPrice());
       // holder.tvBookDescription.setText(book.getDescription());

        Picasso.get().load(book.getImage()).into(holder.imgBook);

    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }
}
