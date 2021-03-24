package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    Button btnCreate;
    EditText etEmail, etName, etPassword, etConfirm;
    ProgressDialog loading;
    String parentDbName ="Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnCreate = findViewById(R.id.sign_button);
        etName = findViewById(R.id.sign_email);
        etEmail = findViewById(R.id.sign_name);
        etPassword = findViewById(R.id.sign_password);
        etConfirm = findViewById(R.id.sign_confirm_password);
        loading = new ProgressDialog(this);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String username = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();
        if("".equals(username))
        {

            Toast.makeText(SignUpActivity.this, "Please introduce your username", Toast.LENGTH_LONG).show();
        }

        else if("".equals(email))
        {
            Toast.makeText(SignUpActivity.this, "Please introduce your email", Toast.LENGTH_LONG).show();
        }

       else if("".equals(password))
        {
            Toast.makeText(SignUpActivity.this, "Please introduce your password", Toast.LENGTH_LONG).show();
        }

       else if("".equals(confirm))
        {
            Toast.makeText(SignUpActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(confirm))
        {
            Toast.makeText(SignUpActivity.this, "The two passwords don't match", Toast.LENGTH_LONG).show();
        }
        else{
            loading.setTitle("Create account");
            loading.setMessage("Please wait until we check the credentials");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            validateEmail( username, email,password);
        }


    }

    private void validateEmail( final String username, final String email,final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child(parentDbName).child(username).exists())){
                    HashMap<String, Object> userdata = new HashMap<>();
                    userdata.put("username", username);
                    userdata.put("email", email);

                    userdata.put("password", password);

                    rootRef.child("Users").child(username).updateChildren(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(SignUpActivity.this, "Your account has been created", Toast.LENGTH_LONG).show();
                                loading.dismiss();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Network error: Please try again", Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "The username " + username + " is already used", Toast.LENGTH_LONG).show();
                    loading.dismiss();

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}