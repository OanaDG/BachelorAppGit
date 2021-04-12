package com.example.bachelorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BookDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDescription, tvPrice;
    ImageView imgBook, btnBack;
    FloatingActionButton btnAddToCart;
    ElegantNumberButton btnBookNumber;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        tvTitle = findViewById(R.id.tvBookDetailsTitle);
        tvDescription = findViewById(R.id.tvBookDetailsDescription);
        tvPrice = findViewById(R.id.tvBookDetailsPrice);
        btnBack = findViewById(R.id.tvReturnBookDetailsBtn);
        imgBook = findViewById(R.id.imgBookDetails);
        btnAddToCart = findViewById(R.id.btnAddProductToCart);
        btnBookNumber = findViewById(R.id.btnBooksNumber);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, HomeActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

    }
}