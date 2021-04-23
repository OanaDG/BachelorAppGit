package com.example.bachelorapp.User;

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
import com.example.bachelorapp.Model.Users;
import com.example.bachelorapp.R;
import com.example.bachelorapp.ViewHolder.BookViewHolder;
import com.example.bachelorapp.Admin.AdminProductMaintenanceActivity;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = HomeActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference booksRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String category, status = "User", genres;
    DatabaseReference databaseReference, databaseReferenceRecommendations;
    List<Books> booksList = new ArrayList<>();
    List<Integer> idsList = new ArrayList<>();

    RecyclerView.Adapter adapter;
    ProgressDialog progressDialog;
    int[] setData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        if(intent.getStringExtra("Admin") != null) {
            status = intent.getStringExtra("Admin");
        }


        if(category.equals("Recommendations"))
        {
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle("Search for recommendations");
            progressDialog.setMessage("Please wait a bit until we find the most suitable books for you");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


        }


        getRecommendations();

        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!status.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    intent.putExtra("category", category);
                    startActivity(intent);
                }

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


        if(status.equals("User")) {
            tvUsername.setText(Collection.currentUser.getUsername());
            Picasso.get().load(Collection.currentUser.getImage()).placeholder(R.drawable.profile).into(imgProfile);
            genres = Collection.currentUser.getGenres();


            //incearca sa pui id recomandari in tabelul users


        }



        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);





    }

    private void getRecommendations() {
        getRecommendationIds();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d(TAG, "recList: intru verificat");
                String[] pyBooks = null;
                if(category.equals("Recommendations") && idsList.size() != 0) {

                    Log.d(TAG, "recList: intru python");
                    if (!Python.isStarted()) {
                        Python.start(new AndroidPlatform(HomeActivity.this));
                    }

                    Python py = Python.getInstance();

                    Set<Integer> uniqueIdsSet = new HashSet<>(idsList);
                    List<Integer> uniqueIdsList = new ArrayList<>(uniqueIdsSet);

                    setData = new int[uniqueIdsList.size()];


                    for (int i = 0; i < uniqueIdsList.size(); i++) {
                        setData[i] = uniqueIdsList.get(i);
                    }

                    PyObject pyObject2 = py.getModule("recsys");
                    PyObject ob = pyObject2.callAttr("computeRec", setData);
                    Log.d(TAG, "recList: " + ob.toString());
                    pyBooks = ob.toString().split(", ");
                }


                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Books book = dataSnapshot.getValue(Books.class);
                    Log.d(TAG, "recList: intru la books");

                    if(category.equals("Recommendations")) {
                        if(pyBooks != null) {
                            for (String s : pyBooks) {
                                s = s.replace("'", "").replace("[", "").replace("]", "");

                                if (s.equals(book.getTitle())) {
                                    booksList.add(book);
                                    Log.d(TAG, "onDataChange: " + s);
                                }

                            }

                        }
                        else  {
                            String[] bookGenres = book.getGenres().trim().split(" ");
                            List<String> genresList = new ArrayList<>();
                            genresList.addAll(Arrays.asList(bookGenres));
                            for(int i = 0; i<bookGenres.length; i++) {


                                Log.d(TAG, "verif " + bookGenres[i]);

                            }
                            Log.d(TAG, "onDataChangess: asta e genul userului " + genres);

                            for(String s : genresList) {


                                    Log.d(TAG, "onDataChangess: " + s);

                            }


                            for(String s : genresList) {

                                if(genres.contains(s) && !booksList.contains(book))
                                {

                                    booksList.add(book);
                                }
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



    private void getRecommendationIds() {
        if(status.equals("User")) {
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

                    Log.d(TAG, "recList: intru la rec");


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

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

        if(!status.equals("Admin")) {

            if(itemId == R.id.nav_cart){
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }

            if(itemId == R.id.nav_search){

                Intent intent = new Intent(this, SearchBooksActivity.class);

                startActivity(intent);
            }

            if(itemId == R.id.nav_orders){

                Intent intent = new Intent(this, OrdersActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }

            if(itemId == R.id.nav_category){
                Intent intent = new Intent(this, CategoryActivity.class);

                startActivityForResult(intent, 1);
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
                intent.putExtra("category", category);
                startActivity(intent);
            }
        }

        DrawerLayout drawerL = findViewById(R.id.drawer_layout);
        drawerL.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


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


            Picasso.get().load(book.getImage()).into(holder.imgBook);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(status.equals("Admin")) {
                        Intent intent = new Intent(HomeActivity.this, AdminProductMaintenanceActivity.class);
                        intent.putExtra("id", book.getPid());
                        intent.putExtra("image", book.getImage());
                        intent.putExtra("category", book.getCategory());
                        intent.putExtra("Admin", status);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(HomeActivity.this, BookDetailsActivity.class);
                        intent.putExtra("id", book.getPid());
                        intent.putExtra("recommendationId", book.getId());
                        intent.putExtra("image", book.getImage());
                        intent.putExtra("category", category);
                        startActivity(intent);

                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return booksList.size();
        }
    }

}