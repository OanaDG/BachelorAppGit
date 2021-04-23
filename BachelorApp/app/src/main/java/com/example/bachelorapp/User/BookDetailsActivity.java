package com.example.bachelorapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Books;
import com.example.bachelorapp.Model.RecommendationIds;
import com.example.bachelorapp.R;
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
    String category, bookId, bookImage, bookRecommendationId, orderId;
    RatingBar ratingBar;
    boolean noDuplicates = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookId = getIntent().getStringExtra("id");
        bookRecommendationId = getIntent().getStringExtra("recommendationId");
        bookImage = getIntent().getStringExtra("image");
        Log.d(TAG, "onCreate: " + bookId);
        category = getIntent().getStringExtra("category");

        tvTitle = findViewById(R.id.tvBookDetailsTitle);
        tvDescription = findViewById(R.id.tvBookDetailsDescription);
        tvPrice = findViewById(R.id.tvBookDetailsPrice);
        tvAuthor = findViewById(R.id.tvBookDetailsAuthor);
        btnBack = findViewById(R.id.tvReturnBookDetailsBtn);
        imgBook = findViewById(R.id.imgBookDetails);
        btnAddToCart = findViewById(R.id.btnAddProductToCart);
        btnBookNumber = findViewById(R.id.btnBookNumber);
        ratingBar = findViewById(R.id.ratingBar);



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

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                final DatabaseReference recommendationRef = FirebaseDatabase.getInstance().getReference().child("Recommendation Ids").child(Collection.currentUser.getUsername());

                recommendationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            RecommendationIds rec = dataSnapshot.getValue(RecommendationIds.class);
                            String recommendationId = rec.getBookId();
                            String[] allIds = recommendationId.split(", ");
                            for(String s : allIds) {
                                if(s.replace("[", "").replace("]", "").equals(bookRecommendationId)) {
                                    orderId = rec.getOrderId();
                                    Log.d(TAG, "onRatingChanged: " +orderId);
                                    noDuplicates = false;
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(rating >= 3.5 && noDuplicates) {
                    final String currentDate, currentTime;
                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    currentDate = dateFormat.format(date.getTime());

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
                    currentTime = timeFormat.format(date.getTime());

                    HashMap<String, Object> recommendationsMap = new HashMap<>();

                    recommendationsMap.put("bookId", bookRecommendationId);
                    recommendationRef.child(currentDate + currentTime).updateChildren(recommendationsMap);


                }


            }
        });


        displayBookDetails(bookId);

    }

    private void addToCart() {
        final String currentTime, currentDate;

        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
        currentDate = dateFormat.format(date.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
        currentTime = dateFormat.format(date.getTime());

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("id", bookId);
        cartMap.put("recommendationId", bookRecommendationId);
        cartMap.put("image", bookImage);
        cartMap.put("title", tvTitle.getText().toString());
        cartMap.put("author", tvAuthor.getText().toString());
        cartMap.put("price", tvPrice.getText().toString().replace(" lei", ""));
        cartMap.put("quantity", btnBookNumber.getNumber());
        cartMap.put("date", currentDate);
        cartMap.put("time", currentTime);

        cartRef.child("User View").child(Collection.currentUser.getUsername()).child("Products").child(bookId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(BookDetailsActivity.this, "Added to the cart", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(BookDetailsActivity.this, HomeActivity.class);
                                intent.putExtra("category", category);
                                startActivity(intent);

                                // incearca sa construiesti admin view dupa ce ai toate prod

//                    cartRef.child("Admin View").child(Collection.currentUser.getUsername()).child("Products").child(bookId).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()) {
//                                Toast.makeText(BookDetailsActivity.this, "Added to the cart", Toast.LENGTH_SHORT).show();
//
//                                Intent intent = new Intent(BookDetailsActivity.this, HomeActivity.class);
//                                intent.putExtra("category", category);
//                                startActivity(intent);
//                            }
//                        }
//                    });
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