package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Orders;
import com.example.bachelorapp.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrdersActivity extends AppCompatActivity {
    RecyclerView ordersList;
    DatabaseReference ordersRef;
    ImageView btnBack;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        category = getIntent().getStringExtra("category");

        btnBack = findViewById(R.id.imgReturnOrdersBtn);
        ordersList = findViewById(R.id.layoutUserOrderList);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdersActivity.this, HomeActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

                        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Collection.currentUser.getUsername());
                        FirebaseRecyclerOptions<Orders> ordersFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Orders>().setQuery(ordersRef, Orders.class).build();

                        FirebaseRecyclerAdapter<Orders, OrdersActivity.OrdersViewHolder> adapter = new FirebaseRecyclerAdapter<Orders, OrdersActivity.OrdersViewHolder>(ordersFirebaseRecyclerOptions) {
                            @Override
                            protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull final Orders model) {
                                holder.tvUsername.setText("Name: " + model.getName());
                                holder.tvPhone.setText("Phone: " + model.getPhone());
                                holder.tvTotalPrice.setText("Price: " + model.getTotalPrice() + " lei");
                                holder.tvDateTime.setText("Ordered on: " + model.getDateTime());
                                holder.tvAddress.setText("Shipping address: " + model.getAddress());

                                holder.btnShowProducts.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(OrdersActivity.this, OrderedBooksActivity.class);
                                        intent.putExtra("username", model.getName());
                                        intent.putExtra("category", category);
                                        startActivity(intent);
                                    }
                                });


                            }


                            @NonNull
                            @Override
                            public OrdersActivity.OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                                return new OrdersActivity.OrdersViewHolder(view);
                            }
                        };

                        ordersList.setAdapter(adapter);
                        adapter.startListening();

            }


    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUsername, tvPhone, tvTotalPrice, tvDateTime, tvAddress;
        public Button btnShowProducts;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsernameOrder);
            tvPhone = itemView.findViewById(R.id.tvPhoneOrder);
            tvAddress = itemView.findViewById(R.id.tvAddressOrder);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvTotalPrice = itemView.findViewById(R.id.tvPriceOrder);
            btnShowProducts = itemView.findViewById(R.id.btnShowProducts);
        }
    }
}