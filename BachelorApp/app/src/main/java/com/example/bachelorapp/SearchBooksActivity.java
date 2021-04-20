package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.bachelorapp.Model.Books;
import com.example.bachelorapp.ViewHolder.BookViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchBooksActivity extends AppCompatActivity {

    private static final String TAG = SearchBooksActivity.class.getSimpleName();
    Button btnSearch;
    EditText etBookTitle;
    RecyclerView searchList;
    String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        btnSearch = findViewById(R.id.btnSearchBook);
        etBookTitle = findViewById(R.id.etSearchBook);
        searchList = findViewById(R.id.layoutSearchList);
        searchList.setLayoutManager(new LinearLayoutManager(SearchBooksActivity.this));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchInput = etBookTitle.getText().toString();
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("Products");
            FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(bookRef.orderByChild("title").startAt(searchInput.toUpperCase()).endAt(searchInput.toLowerCase() + "\uf8ff"), Books.class).build();
            FirebaseRecyclerAdapter<Books, BookViewHolder> adapter = new FirebaseRecyclerAdapter<Books, BookViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull final Books model) {


                    holder.tvBookTitle.setText(model.getTitle());
                    holder.tvBookAuthor.setText("by "+model.getAuthor());
                    holder.tvBookPrice.setText(model.getPrice());


                    Picasso.get().load(model.getImage()).into(holder.imgBook);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SearchBooksActivity.this, BookDetailsActivity.class);
                            intent.putExtra("id", model.getPid());
                            intent.putExtra("recommendationId", model.getId());
                            intent.putExtra("image", model.getImage());
                            intent.putExtra("category", model.getCategory());
                            startActivity(intent);

                        }
                    });
                }

                @NonNull
                @Override
                public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout, parent, false);

                    BookViewHolder viewHolder = new BookViewHolder(view);

                    return viewHolder;
                }
            };

            searchList.setAdapter(adapter);
            adapter.startListening();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}