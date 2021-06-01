package com.example.bachelorapp.Admin;

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

import com.example.bachelorapp.Model.Orders;
import com.example.bachelorapp.Model.Users;
import com.example.bachelorapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminManageOrdersActivity extends AppCompatActivity {

    private static final String TAG = AdminManageOrdersActivity.class.getSimpleName();
    RecyclerView ordersList;
    DatabaseReference ordersRef, usersRef;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_orders);

        btnBack = findViewById(R.id.imgReturnAdminManageOrdersBtn);
        ordersList = findViewById(R.id.layoutOrder);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminManageOrdersActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    final Users user = dataSnapshot.getValue(Users.class);
                        try {
                            Log.d(TAG, "onDataChange: " +user.getUsername());
                            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(user.getUsername());
                            FirebaseRecyclerOptions<Orders> ordersFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Orders>().setQuery(ordersRef, Orders.class).build();

                            FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(ordersFirebaseRecyclerOptions) {
                                @Override
                                protected void onBindViewHolder(@NonNull OrdersViewHolder holder, final int position, @NonNull final Orders model) {

                                    holder.tvUsername.setText("Name: " + model.getName());
                                    holder.tvPhone.setText("Phone: " + model.getPhone());
                                    holder.tvTotalPrice.setText("Price: " + model.getTotalPrice() + " lei");
                                    holder.tvDateTime.setText("Ordered on: " + model.getDateTime());
                                    holder.tvAddress.setText("Shipping address: " + model.getAddress());

                                    holder.btnShowProducts.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(AdminManageOrdersActivity.this, AdminOrderProductsActivity.class);
                                            intent.putExtra("username", user.getUsername());
                                            intent.putExtra("id", model.getDateTime());
                                            startActivity(intent);
                                        }
                                    });

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CharSequence options[] = new CharSequence[] {
                                                    "Yes", "No"
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminManageOrdersActivity.this);
                                            builder.setTitle("Is the order shipped?");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if(i == 0) {
                                                        String username = getRef(position).getKey();
                                                        removeOrder(username);
                                                    }
                                                    else {
                                                        finish();
                                                    }
                                                }
                                            }).show();
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                                    return new OrdersViewHolder(view);
                                }
                            };

                            ordersList.setAdapter(adapter);
                            adapter.startListening();
                    } catch (Exception e) {
                            e.printStackTrace();
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void removeOrder(String username) {
        ordersRef.child(username).removeValue();
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