package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Books;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

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

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        displayBookDetails(bookId);

    }

    private void addToCart() {
        String currentTime, currentDate;

        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = dateFormat.format(date.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = dateFormat.format(date.getTime());

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("id", bookId);
        cartMap.put("title", tvTitle.getText().toString());
        cartMap.put("author", tvAuthor.getText().toString());
        cartMap.put("price", tvPrice.getText().toString());
        cartMap.put("date", currentDate);

        cartRef.child("User View").child(Collection.currentUser.getUsername()).child("Products").child(bookId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    cartRef.child("Admin View").child(Collection.currentUser.getUsername()).child("Products").child(bookId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(BookDetailsActivity.this, "Added to the cart", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(BookDetailsActivity.this, HomeActivity.class);
                                intent.putExtra("category", category);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });

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