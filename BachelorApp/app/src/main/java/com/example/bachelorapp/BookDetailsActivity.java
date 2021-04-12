package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.bachelorapp.Model.Books;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BookDetailsActivity extends AppCompatActivity {

    private static final String TAG = BookDetailsActivity.class.getSimpleName();
    TextView tvTitle, tvDescription, tvPrice, tvAuthor;
    ImageView imgBook, btnBack;
    FloatingActionButton btnAddToCart;
    ElegantNumberButton btnBookNumber;
    String category, bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookId = getIntent().getStringExtra("id");
        Log.d(TAG, "onCreate: " + bookId);
        category = getIntent().getStringExtra("category");

        tvTitle = findViewById(R.id.tvBookDetailsTitle);
        tvDescription = findViewById(R.id.tvBookDetailsDescription);
        tvPrice = findViewById(R.id.tvBookDetailsPrice);
        tvAuthor = findViewById(R.id.tvBookDetailsAuthor);
        btnBack = findViewById(R.id.tvReturnBookDetailsBtn);
        imgBook = findViewById(R.id.imgBookDetails);
        btnAddToCart = findViewById(R.id.btnAddProductToCart);



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, HomeActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        displayBookDetails(bookId);

    }

    private void displayBookDetails(String bookId) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("Products");
        bookRef.child(bookId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    Books book = snapshot.getValue(Books.class);

                    tvTitle.setText(book.getTitle());
                    tvPrice.setText(book.getPrice() + " lei");
                    tvDescription.setText(book.getDescription());
                    tvAuthor.setText("by " + book.getAuthor());
                    Picasso.get().load(book.getImage()).into(imgBook);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}