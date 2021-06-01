package com.example.bachelorapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ConfirmOrderActivity extends AppCompatActivity {

    EditText etName, etPhone, etAddress, etEmail;
    Button btnConfirm;
    String totalPrice, category;
    ImageView btnBack;
    List<String> recommendationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        totalPrice = getIntent().getStringExtra("Total price");
        category = getIntent().getStringExtra("category");
        recommendationList = getIntent().getStringArrayListExtra("recommendationList");
        Toast.makeText(this, "Total price = " + totalPrice + " lei", Toast.LENGTH_SHORT).show();

        etName = findViewById(R.id.etConfirmName);
        etEmail = findViewById(R.id.etConfirmEmail);
        etPhone = findViewById(R.id.etConfirmPhone);
        etAddress = findViewById(R.id.etConfirmAddress);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.imgReturnOrderBtn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmOrderActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateInformation();
            }
        });

    }

    private void validateInformation() {

        if(TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, "Please fill in your full name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(etPhone.getText().toString())) {
            Toast.makeText(this, "Please fill in your phone number", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(etEmail.getText().toString())) {
            Toast.makeText(this, "Please fill in your e-mail", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(etAddress.getText().toString())) {
            Toast.makeText(this, "Please fill in your complete address", Toast.LENGTH_SHORT).show();
        }

        else {
            confirmOrder();
        }
    }

    private void confirmOrder() {

        final String currentDate, currentTime;
        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = dateFormat.format(date.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
        currentTime = timeFormat.format(date.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Collection.currentUser.getUsername()).child(currentDate + " " + currentTime);

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalPrice", totalPrice);
        ordersMap.put("name", etName.getText().toString());
        ordersMap.put("email", etEmail.getText().toString());
        ordersMap.put("phone", etPhone.getText().toString());
        ordersMap.put("address", etAddress.getText().toString().replace(" lei", ""));
        ordersMap.put("dateTime", currentDate + " " +currentTime);
        ordersMap.put("state", "not shipped");

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final DatabaseReference recommendationRef = FirebaseDatabase.getInstance().getReference().child("Recommendation Ids").child(Collection.currentUser.getUsername()).child(currentDate + currentTime);
                HashMap<String, Object> recommendationsMap = new HashMap<>();

                recommendationsMap.put("bookId", recommendationList.toString());
                recommendationsMap.put("orderId",currentDate + currentTime);
                recommendationRef.updateChildren(recommendationsMap);


                if(task.isSuccessful()) {



                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Collection.currentUser.getUsername()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ConfirmOrderActivity.this, "Your order has been placed successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                intent.putExtra("category", category);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}