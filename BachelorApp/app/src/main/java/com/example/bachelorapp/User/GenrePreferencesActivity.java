package com.example.bachelorapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.HashMap;

import io.paperdb.Paper;

public class GenrePreferencesActivity extends AppCompatActivity {

    CheckBox cbAction, cbAdventure, cbBiography, cbChildren, cbComedy, cbCrime, cbDevelopment, cbDrama, cbFantasy, cbMystery, cbRomance, cbSciFi, cbThriller, cbWestern;
    Button btnSendAnswers;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_preferences);

        username = getIntent().getStringExtra("username");

        cbAction = findViewById(R.id.cbAction);
        cbAdventure = findViewById(R.id.cbAdventure);
        cbBiography = findViewById(R.id.cbBiography);
        cbChildren = findViewById(R.id.cbChildren);
        cbComedy = findViewById(R.id.cbComedy);
        cbCrime = findViewById(R.id.cbCrime);
        cbDevelopment = findViewById(R.id.cbDevelopment);
        cbDrama = findViewById(R.id.cbDrama);
        cbFantasy = findViewById(R.id.cbFantasy);
        cbMystery = findViewById(R.id.cbMystery);
        cbRomance = findViewById(R.id.cbRomance);
        cbSciFi = findViewById(R.id.cbSciFi);
        cbThriller = findViewById(R.id.cbThriller);
        cbWestern = findViewById(R.id.cbWestern);
        btnSendAnswers = findViewById(R.id.btnFinishSignup);

        btnSendAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder builder = new StringBuilder();

                if(cbAction.isChecked()) {
                    builder.append("action ");
                }

                if(cbAdventure.isChecked()) {
                    builder.append("adventure ");
                }

                if(cbBiography.isChecked()) {
                    builder.append("biography ");
                }
                if(cbChildren.isChecked()) {
                    builder.append("children ");
                }
                if(cbComedy.isChecked()) {
                    builder.append("comedy ");
                }
                if(cbCrime.isChecked()) {
                    builder.append("crime ");
                }
                if(cbDevelopment.isChecked()) {
                    builder.append("development ");
                }
                if(cbDrama.isChecked()) {
                    builder.append("drama ");
                }
                if(cbFantasy.isChecked()) {
                    builder.append("fantasy ");
                }
                if(cbMystery.isChecked()) {
                    builder.append("mystery ");
                }
                if(cbRomance.isChecked()) {
                    builder.append("romance ");
                }
                if(cbSciFi.isChecked()) {
                    builder.append("sci-fi ");
                }
                if(cbThriller.isChecked()) {
                    builder.append("thriller ");
                }
                if(cbWestern.isChecked()) {
                    builder.append("western ");
                }

                if(builder.toString().equals("")) {
                    Toast.makeText(GenrePreferencesActivity.this, "Please check at least one category", Toast.LENGTH_LONG).show();
                }
                else {

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                    HashMap<String, Object> userdata = new HashMap<>();
                    userdata.put("genres", builder.toString());
                    Paper.book().write(Collection.genresKey, builder.toString());

                    userRef.updateChildren(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(GenrePreferencesActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }

                        }
                    });


                }

            }
        });


    }
}