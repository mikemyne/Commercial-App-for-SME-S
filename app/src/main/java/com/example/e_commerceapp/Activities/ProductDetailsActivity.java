package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.e_commerceapp.Adapters.ProductAdapterDetails;
import com.example.e_commerceapp.Models.Product;
import com.example.e_commerceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    private Toolbar post_detail_toolbar;
    private RecyclerView post_detail_recycler_view;

    String productid;
    private ProductAdapterDetails productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product_details);

        SharedPreferences prefs = ProductDetailsActivity.this.getSharedPreferences("PREFS", MODE_PRIVATE);
        productid = prefs.getString("productid", "none");

        post_detail_toolbar = findViewById(R.id.post_detail_toolbar);
        //setSupportActionBar(post_detail_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Product Details");

        post_detail_recycler_view = findViewById(R.id.post_detail_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //recyclerView.setHasFixedSize(true);
        post_detail_recycler_view.setLayoutManager(layoutManager);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapterDetails(ProductDetailsActivity.this, productList);
        post_detail_recycler_view.setAdapter(productAdapter);

        readPosts();
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products").child(productid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                Product product = dataSnapshot.getValue(Product.class);
                productList.add(product);

                productAdapter.notifyDataSetChanged();
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