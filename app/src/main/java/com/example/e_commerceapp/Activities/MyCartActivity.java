package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapp.Adapters.CartAdapter;
import com.example.e_commerceapp.Models.Cart;
import com.example.e_commerceapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyCartActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progress_circular;

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Cart> cartList;

    private ImageView search_error_image;


    private TextView cartAmountTv;

    private int cartMoney = 0;

    private Button proceedToCheckoutBtn, continueShoppingBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_cart);

        toolbar = findViewById(R.id.food_toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        search_error_image = findViewById(R.id.search_error_image);
        cartAmountTv = findViewById(R.id.cartAmount);
        proceedToCheckoutBtn  = findViewById(R.id.proceedToCheckoutBtn);
        continueShoppingBtn = findViewById(R.id.continueShoppingBtn);

        progress_circular = findViewById(R.id.food_progress_circular);

        recyclerView = findViewById(R.id.foodRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(MyCartActivity.this, cartList);
        recyclerView.setAdapter(cartAdapter);

        readAllPosts();
        calculateCartMoney();

        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        proceedToCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void readAllPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cart")
                .child("buyer")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Cart cart = snapshot.getValue(Cart.class);
                    cartList.add(cart);

                }
                cartAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);

                if (cartList.isEmpty()){
                    Toast.makeText(MyCartActivity.this, "No Items in Cart", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
                    proceedToCheckoutBtn.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calculateCartMoney() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cart")
                .child("buyer")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds :  snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("productPrice");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    cartMoney+=pTotal;
                    cartAmountTv.setText("Total amount KSH. "+ cartMoney);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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