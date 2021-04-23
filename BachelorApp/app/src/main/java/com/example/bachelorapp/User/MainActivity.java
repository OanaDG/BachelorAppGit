package com.example.bachelorapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Users;
import com.example.bachelorapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    String parentDbName ="Users";
    ProgressDialog loading;
    Button btnLog;
    int reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Paper.init(this);
        btnLog = findViewById(R.id.main_log);
        loading = new ProgressDialog(this);

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        String usernameKey = Paper.book().read(Collection.usernameKey);
        String passwordKey = Paper.book().read(Collection.passwordKey);
        if(usernameKey != "" && passwordKey != ""){
            if(!TextUtils.isEmpty(usernameKey) && !TextUtils.isEmpty(usernameKey))
            {
                loading.setTitle("Already logged in");
                loading.setMessage("Please wait...");
                loading.setCanceledOnTouchOutside(false);
                loading.show();
                allowAccessRemember(usernameKey, passwordKey);
            }

        }


    }

    private void allowAccessRemember(final String username, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        Intent intent1 = getIntent();
        reload = intent1.getIntExtra("reload", -1);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDbName).child(username).exists()){
                    Users data = snapshot.child(parentDbName).child(username).getValue(Users.class);
                    if(data.getUsername().equals(username))
                    {
                        if(data.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                            Intent intent;
                            if(reload == 1)
                            {
                                 intent = new Intent(MainActivity.this, HomeActivity.class);

                            }
                            else{
                                 intent = new Intent(MainActivity.this, CategoryActivity.class);
                            }

                            Collection.currentUser = data;
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "The password is incorrect", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "There is no account with the username "+username, Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    }
