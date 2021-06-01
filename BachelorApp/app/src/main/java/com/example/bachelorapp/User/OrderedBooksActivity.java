package com.example.bachelorapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bachelorapp.Model.Cart;
import com.example.bachelorapp.Model.RecommendationIds;
import com.example.bachelorapp.R;
import com.example.bachelorapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OrderedBooksActivity extends AppCompatActivity {

    private static final String TAG = OrderedBooksActivity.class.getSimpleName();
    RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference productsRef;
    String username, category, order, id;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_books);

        username = getIntent().getStringExtra("username");
        category = getIntent().getStringExtra("category");
        order = getIntent().getStringExtra("order");
        id = getIntent().getStringExtra("id");




        btnBack = findViewById(R.id.imgReturnAdminProductsOrdersBtn);
        productsList = findViewById(R.id.layoutProductList);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);

        productsRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(username).child(id).child("Products");
        Log.d(TAG, "onCreate: " + username + " " +id);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderedBooksActivity.this, OrdersActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Cart rec = dataSnapshot.getValue(Cart.class);
                    Log.d(TAG, "onDataChange: " + rec.getAuthor());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(productsRef, Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.tvTitle.setText(model.getTitle());
                holder.tvAuthor.setText(model.getAuthor());
                holder.tvPrice.setText(model.getPrice() + " lei");
                holder.tvQuantity.setText(model.getQuantity());
                Picasso.get().load(model.getImage()).into(holder.imgBook);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}