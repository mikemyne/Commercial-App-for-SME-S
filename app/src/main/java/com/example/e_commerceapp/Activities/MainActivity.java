package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e_commerceapp.Adapters.ProductAdapter;
import com.example.e_commerceapp.BuildConfig;
import com.example.e_commerceapp.Models.Product;
import com.example.e_commerceapp.R;
import com.example.e_commerceapp.RateTheApp.AppRater;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TextView mNavHeaderName,mNavHeaderEmail,mNavHeaderType;
    private CircleImageView nav_header_user_image;

    private ImageView homeCart, searchFab;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference reference, userRef;
    private RecyclerView recyclerViewId;

    private FloatingActionButton fab;
    private ProgressBar progress_circular;

    private ProductAdapter productAdapter;
    private List<Product> productList;

    private TextView cartItemsNumber;

    List<String> idList;

    private TextView searchForProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        if (!mUser.isEmailVerified()){
            Toast.makeText(this, "Email not verified!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("e-Commerce App");


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        fab = findViewById(R.id.fab);
        cartItemsNumber = findViewById(R.id.cartItemsNumber);
        homeCart = findViewById(R.id.homeCart);

        idList = new ArrayList<>();
        searchFab = findViewById(R.id.searchFab);
        searchForProducts  = findViewById(R.id.searchForProducts);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*if (savedInstanceState ==null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }*/

        navigationView.setNavigationItemSelectedListener(this);

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                navigationView =findViewById(R.id.nav_view);
                Menu nav_Menu = navigationView.getMenu();

                String type = snapshot.child("type").getValue().toString();
                if (type.equals("Admin")){
                    nav_Menu.findItem(R.id.nav_allUsers).setVisible(true);
                    nav_Menu.findItem(R.id.nav_messages).setVisible(true);
                    nav_Menu.findItem(R.id.nav_dashboard).setVisible(true);
                    fab.setVisibility(View.VISIBLE);
                    nav_Menu.findItem(R.id.nav_buyers_orders).setVisible(true);

                    homeCart.setVisibility(View.GONE);
                    cartItemsNumber.setVisibility(View.GONE);

                    nav_Menu.findItem(R.id.nav_orders).setVisible(false);
                    nav_Menu.findItem(R.id.nav_cart).setVisible(false);
                    nav_Menu.findItem(R.id.nav_starred).setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

        mNavHeaderName =navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_name);
        mNavHeaderEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_email);
        nav_header_user_image = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_image);
        mNavHeaderType = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_type);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("fullname").getValue().toString();
                    mNavHeaderName.setText(name);

                    String email = dataSnapshot.child("email").getValue().toString();
                    mNavHeaderEmail.setText(email);

                    String type = dataSnapshot.child("type").getValue().toString();
                    mNavHeaderType.setText("Type: "+type);

                    String image = dataSnapshot.child("profileimageurl").getValue(String.class);
                    Glide.with(getApplication()).load(image).into(nav_header_user_image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        readAllPosts();
        totalNumberOfCartItems(cartItemsNumber);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostProductActivity.class);
                startActivity(intent);
            }
        });

        homeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyCartActivity.class);
                startActivity(intent);
            }
        });

        progress_circular = findViewById(R.id.progress_circular);
        recyclerViewId = findViewById(R.id.recyclerViewId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //recyclerView.setHasFixedSize(true);
        recyclerViewId.setLayoutManager(layoutManager);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(MainActivity.this, productList);
        recyclerViewId.setAdapter(productAdapter);

        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              searchForProducts.setVisibility(View.VISIBLE);
            }

        });

        searchForProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchUsers (String s){
        Query query = FirebaseDatabase.getInstance().getReference("products")
                .orderByChild("productTitle").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAllPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);

                }
                productAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);

                if (productList.isEmpty()){
                    Toast.makeText(MainActivity.this, "No products to show", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_dashboard:
                Intent myFeedIntent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(myFeedIntent);
                break;

            case R.id.nav_orders:
                Intent myOrders = new Intent(MainActivity.this, OrdersActivity.class);
                startActivity(myOrders);
                break;

            case R.id.nav_buyers_orders:
                Intent buyersOrders = new Intent(MainActivity.this, BuyerOrdersActivity.class);
                startActivity(buyersOrders);
                break;

            case R.id.nav_cart:
                Intent myCart = new Intent(MainActivity.this, MyCartActivity.class);
                startActivity(myCart);
                break;


            case R.id.nav_starred:
                Intent starredIntent = new Intent(MainActivity.this, StarredItemsActivity.class);
                startActivity(starredIntent);
                break;

            case R.id.nav_computing:
                Intent computingIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                computingIntent.putExtra("title","Computing");
                startActivity(computingIntent);
                break;

            case R.id.nav_supermarket:
                Intent SupermarketIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                SupermarketIntent.putExtra("title","Household");
                startActivity(SupermarketIntent);
                break;


            case R.id.nav_health:
                Intent healthIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                healthIntent.putExtra("title","Health and Beauty");
                startActivity(healthIntent);
                break;

            case R.id.nav_home:
                Intent homeIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                homeIntent.putExtra("title","Home and Office");
                startActivity(homeIntent);
                break;

            case R.id.nav_phones:
                Intent phonesIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                phonesIntent.putExtra("title","Phones and Tablets");
                startActivity(phonesIntent);
                break;

            case R.id.nav_electronics:
                Intent electronicsIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                electronicsIntent.putExtra("title","Electronics");
                startActivity(electronicsIntent);
                break;
            case R.id.nav_fashion:
                Intent fashionIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                fashionIntent.putExtra("title","Fashion");
                startActivity(fashionIntent);
                break;

            case R.id.nav_gaming:
                Intent gamingIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                gamingIntent.putExtra("title","Gaming");
                startActivity(gamingIntent);
                break;

            case R.id.nav_baby:
                Intent babyIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                babyIntent.putExtra("title","Baby Products");
                startActivity(babyIntent);
                break;

            case R.id.nav_sporting:
                Intent sportingIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                sportingIntent.putExtra("title","Sporting Goods");
                startActivity(sportingIntent);
                break;

            case R.id.nav_garden:
                Intent gardenIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                gardenIntent.putExtra("title","Garden and Outdoors");
                startActivity(gardenIntent);
                break;

            case R.id.nav_literature:
                Intent literatureIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                literatureIntent.putExtra("title","Literature");
                startActivity(literatureIntent);
                break;

            case R.id.nav_automobiles:
                Intent automobilesIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                automobilesIntent.putExtra("title","Automobiles");
                startActivity(automobilesIntent);
                break;

            case R.id.nav_hardware:
                Intent hardwareIntent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                hardwareIntent.putExtra("title","Hardware");
                startActivity(hardwareIntent);
                break;


            case R.id.nav_profile:
                /*SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();*/
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;


            case R.id.nav_notifications:
                Intent notificationsIntent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(notificationsIntent);
                break;


            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
                break;

            case R.id.nav_allUsers:
                Intent usersIntent = new Intent(MainActivity.this, AllAppUsersActivity.class);
                startActivity(usersIntent);
                break;


            case R.id.nav_messages:
                Intent messagesIntent = new Intent(MainActivity.this, AdminMessagesViewActivity.class);
                startActivity(messagesIntent);
                break;

            case R.id.nav_about:
                Intent intent2 = new Intent(MainActivity.this, AboutAppActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_rate:
                launchMarket();
                break;
            case R.id.nav_share:
                shareIt();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void totalNumberOfCartItems(final TextView cartItems){
       DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("cart")
                .child("buyer")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    long numberOfLikes = dataSnapshot.getChildrenCount();
                    int noOfCartItems = (int) numberOfLikes;
                    cartItems.setText(String.valueOf(noOfCartItems));
                }else {
                    cartItems.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*private void getNumberOfCartItems() {
        FirebaseDatabase.getInstance().getReference().child("cart")
                .child("buyer")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("products");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    idList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        idList.add(snapshot.getKey());
                    }
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void launchMarket() {
        AppRater.app_launched(this);
    }


    private void shareIt() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "\nPlease download e-commerce app from this link\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            Toast.makeText(MainActivity.this, "Failed to share "+ e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("e-Commerce app")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}