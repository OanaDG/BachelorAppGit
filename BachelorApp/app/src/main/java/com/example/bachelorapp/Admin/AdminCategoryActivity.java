package com.example.bachelorapp.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.bachelorapp.HomeActivity;
import com.example.bachelorapp.LoginActivity;
import com.example.bachelorapp.R;

public class AdminCategoryActivity extends AppCompatActivity {

    ImageView imgBestseller, imgDiscount, imgAll, tvLogout;
    Button btnManageOrders, btnProductMaintenance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        imgBestseller = findViewById(R.id.imgBestseller);
        tvLogout = findViewById(R.id.tvLogoutBtn);
        imgDiscount = findViewById(R.id.imgDiscount);
        imgAll = findViewById(R.id.imgAll);
        btnManageOrders = findViewById(R.id.btnCheckOrders);
        btnProductMaintenance = findViewById(R.id.btnMaintenance);

        imgBestseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "Bestsellers");
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

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnManageOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminManageOrdersActivity.class);
                startActivity(intent);
            }
        });

        btnProductMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                intent.putExtra("category", "All Books");
                startActivity(intent);
            }
        });
    }
}