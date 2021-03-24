package com.example.bachelorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    ImageView imgBestseller, imgRecommender, imgDiscount, imgAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        imgBestseller = findViewById(R.id.imgBestseller);
        imgRecommender = findViewById(R.id.imgRecommender);
        imgDiscount = findViewById(R.id.imgDiscount);
        imgAll = findViewById(R.id.imgAll);

        imgBestseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "Bestsellers");
                startActivity(intent);
            }
        });

        imgRecommender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "Recommendations");
                startActivity(intent);
            }
        });

        imgDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "Discounted");
                startActivity(intent);
            }
        });

        imgAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "All Books");
                startActivity(intent);
            }
        });
    }
}