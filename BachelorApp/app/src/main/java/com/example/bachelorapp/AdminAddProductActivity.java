package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity {

    String categoryName, description, title, author, id, price, rating, saveCurrentDate, saveCurrentTime, productRandomKey, downloadImageUrl;
    TextView tvClose;
    Button btnAddProduct;
    EditText etTitle, etDescription, etPrice, etId, etAuthor, etRating;
    ImageView imgPhoto;
    private static final int galleryPic = 1;
    Uri imageUri;
    StorageReference productImagesRef;
    DatabaseReference productRef;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        btnAddProduct = findViewById(R.id.add_book_btn);
        etId = findViewById(R.id.etId);
        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etPrice = findViewById(R.id.etPrice);
        etRating = findViewById(R.id.etRating);
        etDescription = findViewById(R.id.etDescription);
        tvClose = findViewById(R.id.tvReturnBtn);

        imgPhoto = findViewById(R.id.imgPhoto);
        loading = new ProgressDialog(this);

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateBookData();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void validateBookData() {
        description = etDescription.getText().toString();
        title = etTitle.getText().toString();
        author = etAuthor.getText().toString();
        id = etId.getText().toString();
        rating = etRating.getText().toString();
        price = etPrice.getText().toString();


        if(imageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_LONG).show();
        }
        else if("".equals(description)){
            Toast.makeText(this, "Please write a product description", Toast.LENGTH_LONG).show();
        }
        else if("".equals(title)){
            Toast.makeText(this, "Please write a book title", Toast.LENGTH_LONG).show();
        }
        else if("".equals(author)){
            Toast.makeText(this, "Please write the author", Toast.LENGTH_LONG).show();
        }
        else if("".equals(price)){
            Toast.makeText(this, "Please write a product price", Toast.LENGTH_LONG).show();
        }
        else if("".equals(rating)){
            Toast.makeText(this, "Please write a product rating", Toast.LENGTH_LONG).show();
        }
        else if("".equals(id)){
            Toast.makeText(this, "Please write a product id", Toast.LENGTH_LONG).show();
        }
        else{
            storeProductInformation();
        }

    }

    private void storeProductInformation() {
        loading.setTitle("Adding a new product");
        loading.setMessage("Please wait while we add the new product");
        loading.setCanceledOnTouchOutside(false);
        loading.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddProductActivity.this, "Error: "+message, Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProductActivity.this, "Product image uploaded successfully ", Toast.LENGTH_LONG).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();

                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddProductActivity.this, "Got the product image url successfully ", Toast.LENGTH_LONG).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("category", categoryName);
        productMap.put("image", downloadImageUrl);
        productMap.put("id", id);
        productMap.put("title", title);
        productMap.put("author", author);
        productMap.put("price", price);
        productMap.put("rating", rating);
        productMap.put("description", description);


        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);
                    loading.dismiss();
                    Toast.makeText(AdminAddProductActivity.this, "The product is added successfully ", Toast.LENGTH_LONG).show();

                }
                else
                {
                    loading.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddProductActivity.this, "Error: "+message, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, galleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == galleryPic && resultCode == RESULT_OK && data !=null)
        {
            imageUri = data.getData();
            imgPhoto.setImageURI(imageUri);
        }

    }
}