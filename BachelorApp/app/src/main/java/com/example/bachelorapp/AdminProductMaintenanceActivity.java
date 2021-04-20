package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminProductMaintenanceActivity extends AppCompatActivity {

    Button btnApplyChanges;
    ImageView btnBack;
    EditText etPrice, etDescription;
    Spinner categorySpinner;
    ImageView imgBook;
    String category, bookImage, bookId;
    DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_maintenance);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        bookImage = intent.getStringExtra("image");
        bookId = intent.getStringExtra("id");

        booksRef = FirebaseDatabase.getInstance().getReference().child("Products").child(bookId);

        btnApplyChanges = findViewById(R.id.btnApplyChanges);
        btnBack = findViewById(R.id.imgReturnBookMaintenanceBtn);
        etPrice = findViewById(R.id.etBookPriceMaintenance);
        etDescription = findViewById(R.id.etBookDescriptionMaintenance);
        categorySpinner = findViewById(R.id.spinner);
        imgBook = findViewById(R.id.imgBookMaintenance);

        displayBookInfo();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProductMaintenanceActivity.this, HomeActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        btnApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });

}

    private void applyChanges() {
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();
        String categoryChanged = categorySpinner.getSelectedItem().toString();

        if(price.equals("")) {
            Toast.makeText(AdminProductMaintenanceActivity.this, "Add a price please", Toast.LENGTH_SHORT).show();
        }
        else if(description.equals("")) {
            Toast.makeText(AdminProductMaintenanceActivity.this, "Add a description please", Toast.LENGTH_SHORT).show();
        }

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", bookId);
        productMap.put("category", categoryChanged);
        productMap.put("image", bookImage);
        productMap.put("price", price);
        productMap.put("description", description);

        booksRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(AdminProductMaintenanceActivity.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminProductMaintenanceActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void displayBookInfo() {
        for(int i=0; i<categorySpinner.getCount(); i++)
        {
            if(categorySpinner.getItemAtPosition(i).equals(category))
                categorySpinner.setSelection(i);
        }
        Picasso.get().load(bookImage).into(imgBook);

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String price = snapshot.child("price").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();

                    etPrice.setText(price);
                    etDescription.setText(description);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}