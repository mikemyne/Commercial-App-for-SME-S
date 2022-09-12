package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private EditText productTitle, productShortDescription, oldPrice, newPrice, warranty, available, productDetails;
    private ImageView productImage1, productImage2;
    private Uri image1Uri = null, image2Uri = null;

    private Button BtnCancel, editProductBtn;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;

    private DatabaseReference productsDatabaseRef;
    private String productId;

    private String mAvailable = "";
    private String mNewPrice = "";
    private String mOldPrice = "";
    private String mProductDetails = "";
    private String mProductImage1 = "";
    private String mProductImage2 = "";
    private String mProductShortDescription = "";
    private String mProductTitle = "";
    private String mWarranty = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_product);

        if (getIntent().getExtras()!= null){
            productId =getIntent().getStringExtra("productId");
        }


        settingsToolbar = findViewById(R.id.settingsToolbar);
        //setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Product Creation");

        productTitle = findViewById(R.id.productTitle);
        productShortDescription = findViewById(R.id.productShortDescription);
        oldPrice = findViewById(R.id.oldPrice);
        newPrice = findViewById(R.id.newPrice);
        warranty = findViewById(R.id.warranty);
        available = findViewById(R.id.available);
        productDetails = findViewById(R.id.productDetails);
        productImage1 = findViewById(R.id.productImage1);
        productImage2 = findViewById(R.id.productImage2);
        BtnCancel = findViewById(R.id.BtnCancel);
        editProductBtn = findViewById(R.id.editProductBtn);

        productsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("products").child(productId);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getProductInfo();

        editProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveProductInformation();
            }
        });
    }


    private void getProductInfo(){
        productsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("available") !=null){
                        mAvailable = map.get("available").toString();
                        available.setText(mAvailable);
                        available.setSelection(mAvailable.length());
                    }

                    if (map.get("newPrice") !=null){
                        mNewPrice = map.get("newPrice").toString();
                        newPrice.setText(mNewPrice);
                        newPrice.setSelection(mNewPrice.length());
                    }

                    if (map.get("oldPrice") !=null){
                        mOldPrice = map.get("oldPrice").toString();
                        oldPrice.setText(mOldPrice);
                        oldPrice.setSelection(mOldPrice.length());
                    }

                    if (map.get("productDetails") !=null){
                        mProductDetails = map.get("productDetails").toString();
                        productDetails.setText(mProductDetails);
                        productDetails.setSelection(mProductDetails.length());
                    }

                    if (map.get("productImage1") !=null){
                        mProductImage1 = map.get("productImage1").toString();
                        Glide.with(getApplication()).load(mProductImage1).into(productImage1);
                    }

                    if (map.get("productImage2") !=null){
                        mProductImage2 = map.get("productImage2").toString();
                        Glide.with(getApplication()).load(mProductImage2).into(productImage2);
                    }

                    if (map.get("productShortDescription") !=null){
                        mProductShortDescription = map.get("productShortDescription").toString();
                        productShortDescription.setText(mProductShortDescription);
                        productShortDescription.setSelection(mProductShortDescription.length());
                    }
                    if (map.get("productTitle") !=null){
                        mProductTitle = map.get("productTitle").toString();
                        productTitle.setText(mProductTitle);
                        productTitle.setSelection(mProductTitle.length());
                    }

                    if (map.get("warranty") !=null){
                        mWarranty = map.get("warranty").toString();
                        warranty.setText(mWarranty);
                        warranty.setSelection(mWarranty.length());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void saveProductInformation() {
        final String available1 = available.getText().toString();
        final String newPrice1 = newPrice.getText().toString();
        final String oldPrice1 = oldPrice.getText().toString();
        final String productDetails1 = productDetails.getText().toString();
        final String shortDescription1 = productShortDescription.getText().toString();
        final String title1 = productTitle.getText().toString();
        final String waranty1 = warranty.getText().toString();


        if (TextUtils.isEmpty(available1)){
            available.setError("Add products available!");
            return;
        }

        if (TextUtils.isEmpty(newPrice1)){
            newPrice.setError("New price Required");
        }

        if (TextUtils.isEmpty(oldPrice1)){
            oldPrice.setError("Old Price Required");
        }
        if (TextUtils.isEmpty(productDetails1)){
            productDetails.setError("product details Required");
        }

        if (TextUtils.isEmpty(shortDescription1)){
            productShortDescription.setError("short description Required");
        }
        if (TextUtils.isEmpty(title1)){
            productTitle.setError("product title Required");
        }
        if (TextUtils.isEmpty(waranty1)){
            warranty.setError("waranty Required");
        }

        else {
            loader.setMessage("Uploading details... please wait");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            Map userInfo = new HashMap();
            userInfo.put("available",available1);
            userInfo.put("newPrice", newPrice1);
            userInfo.put("oldPrice", oldPrice1);
            userInfo.put("productDetails", productDetails1);
            userInfo.put("productShortDescription",shortDescription1);
            userInfo.put("productTitle", title1);
            userInfo.put("warranty", waranty1);

            productsDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(EditProductActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        String error = task.getException().toString();
                        Toast.makeText(EditProductActivity.this, "Update Failed: "+ error, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    loader.dismiss();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Product Editing")
                .setMessage("Are you sure you want to leave?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditProductActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}