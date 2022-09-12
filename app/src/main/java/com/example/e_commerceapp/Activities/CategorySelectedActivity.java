package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.e_commerceapp.Adapters.ProductAdapter;
import com.example.e_commerceapp.Models.Product;
import com.example.e_commerceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectedActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progress_circular;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    private ImageView search_error_image;

    public String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_category_selected);


        toolbar = findViewById(R.id.food_toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        search_error_image = findViewById(R.id.search_error_image);

        progress_circular = findViewById(R.id.food_progress_circular);

        recyclerView = findViewById(R.id.foodRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(CategorySelectedActivity.this, productList);
        recyclerView.setAdapter(productAdapter);


        if (getIntent().getExtras()!= null){
            title =getIntent().getStringExtra("title");
            getSupportActionBar().setTitle(title);
            readPosts();
        }
    }

    private void readPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products");
        Query query = reference.orderByChild("category").equalTo(title);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);

                if (productList.isEmpty()){
                    search_error_image.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}