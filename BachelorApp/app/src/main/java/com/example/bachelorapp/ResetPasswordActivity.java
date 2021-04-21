package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bachelorapp.Collection.Collection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    String purpose, category;
    ImageView btnClose;
    EditText etUsername, etQuestion1, etQuestion2;
    TextView tvResetPasswordTitle, tvQuestions;
    Button btnCheckAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Intent intent = getIntent();
        purpose = intent.getStringExtra("password");
        category = intent.getStringExtra("category");

        tvResetPasswordTitle = findViewById(R.id.tvResetPassword);
        btnClose = findViewById(R.id.imgReturnSettingsBtn);
        etUsername = findViewById(R.id.etFindUsername);
        etQuestion1 = findViewById(R.id.etQuestion1);
        etQuestion2 = findViewById(R.id.etQuestion2);
        tvQuestions = findViewById(R.id.tvSecurityQuestions);
        btnCheckAnswers = findViewById(R.id.btnCheckOQuestions);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordActivity.this, SettingsActivity.class);
                startActivity(intent);
                intent.putExtra("category", category);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        etUsername.setVisibility(View.INVISIBLE);


        if(purpose.equals("settings")) {

            tvResetPasswordTitle.setText("Set security questions");
            tvQuestions.setText("Set the answers for the security questions please");

            btnCheckAnswers.setText("Set");
            displayAnswers();
            btnCheckAnswers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   setAnswers();

                }
            });
        }
        else {
            etUsername.setVisibility(View.VISIBLE);


        }
    }

    private void setAnswers() {
        String answer1 = etQuestion1.getText().toString().toLowerCase();
        String answer2 = etQuestion2.getText().toString().toLowerCase();

        if(etQuestion1.equals("") && etQuestion2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please answer both questions", Toast.LENGTH_SHORT).show();
        }

        else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Collection.currentUser.getUsername());
            HashMap<String, Object> userdata = new HashMap<>();
            userdata.put("answer1", answer1);
            userdata.put("answer2", answer2);

            reference.child("securityQuestions").updateChildren(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "The answers were registered", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, SettingsActivity.class);
                        intent.putExtra("category", category);
                        startActivity(intent);
                    }

                }
            });
        }
    }

    private void displayAnswers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Collection.currentUser.getUsername());
        reference.child("securityQuestions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String answer1 = snapshot.child("answer1").getValue().toString();
                    String answer2 = snapshot.child("answer2").getValue().toString();

                    etQuestion1.setText(answer1);
                    etQuestion2.setText(answer2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}