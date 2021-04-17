package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.ViewHolder.CategoryActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView imgProfileSettings;
    EditText etEmailSettings;
    TextView tvUpdate;
    ImageView tvClose;
    Uri imgUri;
    String myUrl = "";
    StorageReference storageProfilePictureRef;
    String checker = "";
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        imgProfileSettings = findViewById(R.id.imgPhotoSettings);
        etEmailSettings = findViewById(R.id.etSettingsEmail);
        tvClose = findViewById(R.id.tvCloseBtn);
        tvUpdate = findViewById(R.id.tvUpdateAccountBtn);


        userInfoDisplay(imgProfileSettings, etEmailSettings);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        imgProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                CropImage.activity(imgUri).setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("email", etEmailSettings.getText().toString());
        ref.child(Collection.currentUser.getUsername()).updateChildren(userMap);

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra("reload", 1);
        startActivity(intent);
        Toast.makeText(SettingsActivity.this, "Profile info updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(etEmailSettings.getText().toString())){
            Toast.makeText(this, "Email is mandatory", Toast.LENGTH_LONG).show();
        }

        else if(checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update profile");
        progressDialog.setMessage("Please wait while we update your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imgUri != null)
        {
            final StorageReference fileRef = storageProfilePictureRef.child(Collection.currentUser.getUsername() + ".jpg");
            uploadTask = fileRef.putFile(imgUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();

                        userMap.put("email", etEmailSettings.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Collection.currentUser.getUsername()).updateChildren(userMap);

                        progressDialog.dismiss();

                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        intent.putExtra("reload", 1);
                        startActivity(intent);
                        Toast.makeText(SettingsActivity.this, "Profile info updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "There is no image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView imgProfileSettings, final EditText etEmailSettings) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Collection.currentUser.getUsername());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();

                        
                        String email = snapshot.child("email").getValue().toString();

                        Picasso.get().load(image).into(imgProfileSettings);
                        etEmailSettings.setText(email);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();

            imgProfileSettings.setImageURI(imgUri);
        }
        else
        {
            Toast.makeText(this, "Error: Try again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }
}