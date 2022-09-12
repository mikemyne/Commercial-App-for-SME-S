package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapp.Adapters.CheckoutAdapter;
import com.example.e_commerceapp.Models.Checkout;
import com.example.e_commerceapp.Models.IDOFTHEADMIN;
import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView totalAmountTv;
    private TextInputEditText firstName, lastName, streetAddress, city, province, postalCode;
    private RadioGroup radioGroup;
    private RadioButton radioSelectedPay;

    private Button placeOrder, backBtn;

    private RecyclerView recyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<Checkout> checkoutList;

    private ProgressBar progress_circular;
    private int cartMoney = 0;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_checkout);

        toolbar = findViewById(R.id.food_toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Checkout Page");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        totalAmountTv = findViewById(R.id.totalAmountTv);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        streetAddress = findViewById(R.id.streetAddress);
        city = findViewById(R.id.city);
        province = findViewById(R.id.province);
        postalCode = findViewById(R.id.postalCode);
        radioGroup = findViewById(R.id.radioGroup);
        progress_circular = findViewById(R.id.food_progress_circular);
        placeOrder = findViewById(R.id.placeOrder);
        backBtn = findViewById(R.id.backBtn);

        loader = new ProgressDialog(this);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performValidations();

            }
        });

        recyclerView = findViewById(R.id.foodRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        checkoutList = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(CheckoutActivity.this, checkoutList);
        recyclerView.setAdapter(checkoutAdapter);

        readAllPosts();
        calculateCartMoney();

    }

    String getFirstName(){
        return firstName.getText().toString();
    }
    String getLastName(){
        return lastName.getText().toString();
    }
    String getStreetAddress(){
        return streetAddress.getText().toString();
    }
    String getCity(){
        return city.getText().toString();
    }
    String getProvince(){
        return province.getText().toString();
    }
    String getPostalCode(){
        return postalCode.getText().toString();
    }
    String getSelectedPaymentMethod(){
        int selectedId=radioGroup.getCheckedRadioButtonId();
        radioSelectedPay=(RadioButton)findViewById(selectedId);
        return radioSelectedPay.getText().toString();
    }

    private void performValidations(){
        if (!getFirstName().isEmpty() && !getLastName().isEmpty()
                && !getStreetAddress().isEmpty() && !getCity().isEmpty()
                && !getProvince().isEmpty() && !getPostalCode().isEmpty()
                && !getSelectedPaymentMethod().isEmpty()
        ){
           placeOrder();
        }else {
            Toast.makeText(this, "Please fill the missing fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void placeOrder() {

        new AlertDialog.Builder(CheckoutActivity.this)
                .setTitle("PLACE ORDER")
                .setMessage("Do you want to make this order?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        loader.setMessage("Placing your order, please wait...");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();

                        DatabaseReference adminOrderList = FirebaseDatabase.getInstance().getReference("AdminOrderList");
                        String orderId = adminOrderList.push().getKey().toString();

                        HashMap hashMap = new HashMap();
                        hashMap.put("firstName", getFirstName());
                        hashMap.put("lastName", getLastName());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("date", DateFormat.getDateInstance().format(new Date()));
                        hashMap.put("amount", String.valueOf(cartMoney));
                        hashMap.put("streetAddress", getStreetAddress());
                        hashMap.put("city", getCity());
                        hashMap.put("province", getProvince());
                        hashMap.put("postalCode", getPostalCode());
                        hashMap.put("status", "notDelivered");
                        hashMap.put("orderId", orderId);
                        hashMap.put("paymentMethod", getSelectedPaymentMethod());


                        adminOrderList.child(orderId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cart")
                                            .child("AdminCartView")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("products");

                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            orderRef.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                   /* if (task.isSuccessful()){
                                        Toast.makeText(CheckoutActivity.this,
                                                "ORDER PLACED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(CheckoutActivity.this,
                                                "YOUR CART IS CLEARED", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }else {
                                        Toast.makeText(CheckoutActivity.this,
                                                "ERROR OCCURED WHEN CLEARING CART", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }*/

                                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("cart")
                                                            .child("buyer")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("products");

                                                    reference1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                dialog.dismiss();
                                                                startAlertDialog();
                                                                Toast.makeText(CheckoutActivity.this,
                                                                        "ORDER PLACED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(CheckoutActivity.this,
                                                                        "YOUR CART IS CLEARED", Toast.LENGTH_SHORT).show();
                                                                addNotifications();

                                                            }else {
                                                                Toast.makeText(CheckoutActivity.this,
                                                                        "ERROR OCCURED WHEN CLEARING CART", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }else {
                                    Toast.makeText(CheckoutActivity.this,
                                            "COULD NOT ADD HASHMAP DATA", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });

                    }
                })
                .setNegativeButton("No", null)
                .show();

    }


    private void readAllPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cart")
                .child("buyer")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                checkoutList.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Checkout checkout = snapshot.getValue(Checkout.class);
                    checkoutList.add(checkout);

                }
                checkoutAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);

                if (checkoutList.isEmpty()){
                    Toast.makeText(CheckoutActivity.this, "No Items in Cart", Toast.LENGTH_SHORT).show();
                    progress_circular.setVisibility(View.GONE);
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
                    totalAmountTv.setText("Total amount KSH. "+ cartMoney);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startAlertDialog() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(CheckoutActivity.this);
        LayoutInflater inflater = LayoutInflater.from(CheckoutActivity.this);
        View myView = inflater.inflate(R.layout.success_layout, null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        Button closeButton = myView.findViewById(R.id.closeButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        try{
            dialog.show();
        }
        catch(WindowManager.BadTokenException e) {

        }
        //dialog.show();



    }

    private void addNotifications(){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications")
                .child(IDOFTHEADMIN.getIdOfTheAdmin());
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", "Made an order, check it out.");
        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("postid", null);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap);
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