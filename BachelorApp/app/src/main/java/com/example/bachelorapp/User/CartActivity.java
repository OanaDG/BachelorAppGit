package com.example.bachelorapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Cart;
import com.example.bachelorapp.R;
import com.example.bachelorapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = CartActivity.class.getSimpleName();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button btnNext;
    ImageView btnBack;
    String category;
    int totalOrderPrice = 0;
    TextView tvTotalPrice;
    List<String> recommendationIdList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        category = getIntent().getStringExtra("category");

        recyclerView = findViewById(R.id.layoutCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnNext = findViewById(R.id.btnNext);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnBack = findViewById(R.id.imgReturnBookCartBtn);



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(totalOrderPrice > 0) {
                    Intent intent = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                    intent.putExtra("Total price", String.valueOf(totalOrderPrice));
                    intent.putExtra("category", category);
                    intent.putStringArrayListExtra("recommendationList", (ArrayList<String>) recommendationIdList);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(CartActivity.this, "The shopping cart is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartRef.child("User View").child(Collection.currentUser.getUsername()).child("Products"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.tvTitle.setText(model.getTitle());
                holder.tvAuthor.setText(model.getAuthor());
                holder.tvPrice.setText(model.getPrice() + " lei");
                holder.tvQuantity.setText(model.getQuantity());
                Picasso.get().load(model.getImage()).into(holder.imgBook);

                recommendationIdList.add(model.getRecommendationId());

                float totalBookPrice = Float.valueOf(model.getPrice()) *  Float.valueOf(model.getQuantity());
                totalOrderPrice += totalBookPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[] {
                                "  Edit",
                                "  Delete"
                        };

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CartActivity.this).setTitle("Choose option");
                        alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i == 0) {
                                    Intent intent = new Intent(CartActivity.this, BookDetailsActivity.class);
                                    intent.putExtra("id", model.getId());
                                    intent.putExtra("category", category);
                                    intent.putExtra("image", model.getImage());
                                    startActivity(intent);
                                }

                                else {
                                    alertDialogBuilder.setMessage("   Do you want to delete this product?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            cartRef.child("User View").child(Collection.currentUser.getUsername()).child("Products").child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "The item was removed", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });

                                        }
                                    }).setNegativeButton("No", null).show();
                                }
                            }
                        });

                        alertDialogBuilder.show();

                    }
                });

                tvTotalPrice.setText("Total price = " + String.valueOf(totalOrderPrice) + " lei");

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}