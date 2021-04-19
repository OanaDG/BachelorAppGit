package com.example.bachelorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CategoryActivity extends AppCompatActivity {

    ImageView imgRecommendation, imgBestseller, imgDiscount, imgAll, imgLogoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        imgAll = findViewById(R.id.imgCategoryAll);
        imgBestseller = findViewById(R.id.imgCategoryBestseller);
        imgDiscount = findViewById(R.id.imgCategoryDiscount);
        imgRecommendation = findViewById(R.id.imgCategoryRecommender);
        imgLogoutBtn = findViewById(R.id.imgLogoutBtn);



        imgAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                intent.putExtra("category", "All Books");
                startActivity(intent);
            }
        });

        imgRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                intent.putExtra("category", "Recommendations");
                startActivity(intent);
            }
        });

        imgDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                intent.putExtra("category", "Discounted");
                startActivity(intent);
            }
        });

        imgBestseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                intent.putExtra("category", "Bestsellers");
                startActivity(intent);
            }
        });

        imgLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}