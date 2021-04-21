package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Users;
import com.example.bachelorapp.Admin.AdminCategoryActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    TextView tvSign, tvAdmin, tvNotAdmin, tvForgotPassword;
    EditText etUser, etPassword;
    Button btnLog;
    CheckBox cbRemember;
    ProgressDialog loading;
    String parentDbName ="Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSign = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgot);
        tvAdmin = findViewById(R.id.tvAdmin);
        tvNotAdmin = findViewById(R.id.tvNotAdmin);
        etUser = findViewById(R.id.editTextTextPersonName);
        etPassword = findViewById(R.id.editTextTextPassword);
        btnLog = findViewById(R.id.log_button);
        cbRemember = findViewById(R.id.checkBox);
        loading = new ProgressDialog(this);

        Paper.init(this);

        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        tvAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLog.setText("Admin log in ");
                tvAdmin.setVisibility(View.INVISIBLE);
                tvNotAdmin.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        tvNotAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLog.setText("Log in ");
                tvAdmin.setVisibility(View.VISIBLE);
                tvNotAdmin.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("password", "login");
                startActivity(intent);
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        String username = etUser.getText().toString();

        String password = etPassword.getText().toString();

        if("".equals(username))
        {

            Toast.makeText(this, "Please introduce your username", Toast.LENGTH_LONG).show();
        }



        else if("".equals(password))
        {
            Toast.makeText(this, "Please introduce your password", Toast.LENGTH_LONG).show();
        }
        else{
            loading.setTitle("Logging in");
            loading.setMessage("Please wait until we check the credentials");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            allowAccess(username, password);
        }
    }

    private void allowAccess(final String username, final String password) {
        if(cbRemember.isChecked())
        {
            Paper.book().write(Collection.usernameKey, username);
            Paper.book().write(Collection.passwordKey, password);
        }
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDbName).child(username).exists()){
                    Users data = snapshot.child(parentDbName).child(username).getValue(Users.class);
                    if(data.getUsername().equals(username))
                    {
                        if(data.getPassword().equals(password)){
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loading.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else 
                            {
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                loading.dismiss();

                                Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                                Collection.currentUser = data;
                                startActivity(intent);
                            }

                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "The password is incorrect", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "There is no account with the username "+username, Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}