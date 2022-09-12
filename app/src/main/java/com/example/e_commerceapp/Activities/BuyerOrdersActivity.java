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
import android.widget.Toast;

import com.example.e_commerceapp.Adapters.OrderAdapter;
import com.example.e_commerceapp.Models.Order;
import com.example.e_commerceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuyerOrdersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progress_circular;

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    private ImageView search_error_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buyer_orders);

        toolbar = findViewById(R.id.food_toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Orders Made");
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

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(BuyerOrdersActivity.this, orderList);
        recyclerView.setAdapter(orderAdapter);

        readAllPosts();
    }

    private void readAllPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AdminOrderList");
        Query query = reference.orderByChild("status").equalTo("notDelivered");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    orderList.clear();
                    for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                        Order order = snapshot.getValue(Order.class);
                        orderList.add(order);

                    }
                    orderAdapter.notifyDataSetChanged();
                    progress_circular.setVisibility(View.GONE);

                    if (orderList.isEmpty()){
                        Toast.makeText(BuyerOrdersActivity.this, "NO ORDERS YET", Toast.LENGTH_SHORT).show();
                        progress_circular.setVisibility(View.GONE);
                        search_error_image.setVisibility(View.VISIBLE);

                    }

                } else {
                    Toast.makeText(BuyerOrdersActivity.this, "NOTHING TO SHOW", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
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