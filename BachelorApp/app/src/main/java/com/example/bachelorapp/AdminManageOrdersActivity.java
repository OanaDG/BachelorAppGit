package com.example.bachelorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Orders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminManageOrdersActivity extends AppCompatActivity {

    RecyclerView ordersList;
    DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Collection.currentUser.getUsername());

        ordersList = findViewById(R.id.layoutOrder);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> ordersFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Orders>().setQuery(ordersRef, Orders.class).build();

        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter = new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(ordersFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull Orders model) {

                holder.tvUsername.setText("Name: " + model.getName());
                holder.tvPhone.setText("Phone: " + model.getPhone());
                holder.tvTotalPrice.setText("Price: " + model.getTotalPrice() + " lei");
                holder.tvDateTime.setText("Ordered on: " + model.getDate() + " " + model.getTime());
                holder.tvAddress.setText("Shipping address: " + model.getAddress());
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