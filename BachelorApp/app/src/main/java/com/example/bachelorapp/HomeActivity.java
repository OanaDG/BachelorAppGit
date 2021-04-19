package com.example.bachelorapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.bachelorapp.Collection.Collection;
import com.example.bachelorapp.Model.Books;
import com.example.bachelorapp.Model.RecommendationIds;
import com.example.bachelorapp.ViewHolder.BookViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = HomeActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference booksRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String category;
    DatabaseReference databaseReference, databaseReferenceRecommendations;
    List<Books> booksList = new ArrayList<>();
    List<Integer> idsList = new ArrayList<>();
    RecyclerView.Adapter adapter;
    ProgressDialog progressDialog;
    int[] setData = new int[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        if(category.equals("Recommendations"))
        {
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle("Search for recommendations");
            progressDialog.setMessage("Please wait a bit until we find the most suitable books for you");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }


        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = headerView.findViewById(R.id.tvUsername);
        CircleImageView imgProfile = headerView.findViewById(R.id.user_profile_image);

        tvUsername.setText(Collection.currentUser.getUsername());
        Picasso.get().load(Collection.currentUser.getImage()).placeholder(R.drawable.profile).into(imgProfile);



        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        databaseReferenceRecommendations = FirebaseDatabase.getInstance().getReference().child("Recommendation Ids").child(Collection.currentUser.getUsername());
        databaseReferenceRecommendations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    RecommendationIds id = dataSnapshot.getValue(RecommendationIds.class);
                    String recommendationId = id.getBookId();
                    String[] allIds = recommendationId.split(", ");
                    for(String s : allIds) {

                        idsList.add(Integer.parseInt(s.replace("[", "").replace("]", "")));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] pyBooks = null;
                if(category.equals("Recommendations")) {

                    if (!Python.isStarted()) {
                        Python.start(new AndroidPlatform(HomeActivity.this));
                    }

                    Python py = Python.getInstance();

                    int[] setData = new int[idsList.size()];


                    for(int i = 0; i<idsList.size(); i++) {
                        setData[i] = idsList.get(i);
                    }

                    PyObject pyObject2 = py.getModule("recsys");
                    PyObject ob = pyObject2.callAttr("computeRec", setData);
                    Log.d(TAG, "recList: " + ob.toString());
                    pyBooks = ob.toString().split(", ");


                }
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Books book = dataSnapshot.getValue(Books.class);
                    if(category.equals("Recommendations")) {
                        for (String s : pyBooks) {
                            s = s.replace("'", "").replace("[", "").replace("]", "");

                            if (s.equals(book.getTitle())) {
                                booksList.add(book);
                                Log.d(TAG, "onDataChange: " + s);
                            }

                        }
                        progressDialog.dismiss();
                    }

                    else if(book.getCategory().equals(category) || category.equals("All Books")){

                        Log.d(TAG, "onDataChange: " +category);
                        booksList.add(book);
                    }

                }
                adapter = new RecyclerViewAdapter(HomeActivity.this, booksList);

                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if(itemId == R.id.nav_cart){
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        }

        if(itemId == R.id.nav_orders){

        }

        if(itemId == R.id.nav_category){
            Intent intent = new Intent(this, CategoryActivity.class);

            startActivity(intent);
        }

        if(itemId == R.id.nav_logout){
            Paper.book().destroy();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        if(itemId == R.id.nav_settings){

            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawerL = findViewById(R.id.drawer_layout);
        drawerL.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder> {

        Context context;
        List<Books> booksList;

        public RecyclerViewAdapter(Context context, List<Books> booksList) {
            this.context = context;
            this.booksList = booksList;
        }

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout, parent, false);

            BookViewHolder viewHolder = new BookViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
            final Books book = booksList.get(position);

            holder.tvBookTitle.setText(book.getTitle());
            holder.tvBookAuthor.setText("by "+book.getAuthor());
            holder.tvBookPrice.setText(book.getPrice());
            // holder.tvBookDescription.setText(book.getDescription());

            Picasso.get().load(book.getImage()).into(holder.imgBook);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, BookDetailsActivity.class);
                    intent.putExtra("id", book.getPid());
                    intent.putExtra("recommendationId", book.getId());
                    intent.putExtra("image", book.getImage());
                    intent.putExtra("category", book.getCategory());
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return booksList.size();
        }
    }

}