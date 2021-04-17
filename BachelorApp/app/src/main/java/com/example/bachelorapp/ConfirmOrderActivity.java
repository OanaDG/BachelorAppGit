package com.example.bachelorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfirmOrderActivity extends AppCompatActivity {

    EditText etName, etPhone, etAddress;
    Button btnConfirm;
    String totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        totalPrice = getIntent().getStringExtra("Total price");
        Toast.makeText(this, "Total price = " + totalPrice + " lei", Toast.LENGTH_SHORT).show();

        etName = findViewById(R.id.etConfirmName);
        etPhone = findViewById(R.id.etConfirmPhone);
        etAddress = findViewById(R.id.etConfirmAddress);
        btnConfirm = findViewById(R.id.btnConfirm);
    }
}