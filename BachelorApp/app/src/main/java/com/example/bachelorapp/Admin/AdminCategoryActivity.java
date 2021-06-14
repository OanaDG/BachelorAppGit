package com.example.bachelorapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.bachelorapp.Model.Orders;
import com.example.bachelorapp.User.HomeActivity;
import com.example.bachelorapp.User.LoginActivity;
import com.example.bachelorapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class AdminCategoryActivity extends AppCompatActivity {

    private static final String TAG = AdminCategoryActivity.class.getSimpleName();
    ImageView imgBestseller, imgDiscount, imgAll, tvLogout;
    Button btnManageOrders, btnProductMaintenance, btnCustomerPurchasesReport;
    StringBuilder builder = new StringBuilder();
    DatabaseReference ordersRef;

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
        btnCustomerPurchasesReport = findViewById(R.id.btnClientReport);

        builder.append("Customer,Revenue(RON),Number of orders");

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> map = (Map<String, Object>)snapshot.getValue();
                for(Map.Entry<String, Object> pair : map.entrySet()) {
                    String name = pair.getKey();
                    builder.append("\n" + name + ",");
                    DataSnapshot allSnapshots = snapshot.child(name);
                    Iterable<DataSnapshot> orderChildren = allSnapshots.getChildren();
                    double revenue = 0;
                    int noOrders = 0;
                    for (DataSnapshot snap : orderChildren) {
                        Orders order = snap.getValue(Orders.class);
                        revenue += Double.valueOf(order.getTotalPrice());
                        noOrders ++;

                    }
                    builder.append(revenue + "," + noOrders);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


        //to do method for generating csv
        btnCustomerPurchasesReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + builder.toString());
                generateRevenueByClientReport();
            }
        });


    }

    public void generateRevenueByClientReport() {
        try{
            FileOutputStream out = openFileOutput("revenueReport.csv", Context.MODE_PRIVATE);
            out.write((builder.toString()).getBytes());
            out.close();

            Context context = getApplicationContext();
            File file = new File(getFilesDir(), "revenueReport.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.bachelorapp.fileprovider", file);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Revenue brought by each client");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Save report"));

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}