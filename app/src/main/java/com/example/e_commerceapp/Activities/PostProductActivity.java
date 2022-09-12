package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class PostProductActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private EditText productTitle, productShortDescription, oldPrice, newPrice, warranty, available, productDetails;
    private ImageView productImage1, productImage2;
    private Spinner spinner;
    private Uri image1Uri = null, image2Uri = null;

    private Button BtnCancel, postProductButton;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;

    private String myUrl ="";

    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_product);

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
        postProductButton = findViewById(R.id.postProductButton);
        spinner = findViewById(R.id.spinner);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("product images");

        productImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        productImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        postProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performProductValidations();
            }
        });

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        productDetails.setText(prefs.getString("autoSave", ""));

        productDetails.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            @Override
            public void afterTextChanged(Editable s){
                prefs.edit().putString("autoSave", s.toString()).commit();
            }

        });
    }


    String getShortDescription(){
        return productShortDescription.getText().toString();
    }
    String getProductDetails(){
        return productDetails.getText().toString();
    }
    String getProductTitle(){
        return productTitle.getText().toString();
    }
    String getOldPrice(){
        return oldPrice.getText().toString();
    }
    String getNewPrice(){
        return newPrice.getText().toString();
    }
    String getWarranty(){
        return warranty.getText().toString();
    }
    String getAvailability(){
        return available.getText().toString();
    }
    String getSelectedCategory(){
        return spinner.getSelectedItem().toString();
    }

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("products");
    final String productid = reference.push().getKey();

    private void performProductValidations() {

        if (!getShortDescription().isEmpty() && !getProductDetails().isEmpty() && !getProductTitle().isEmpty()
                &&!getOldPrice().isEmpty() &&! getNewPrice().isEmpty() &&!getWarranty().isEmpty()
                 && !getAvailability().isEmpty()
                &&!getSelectedCategory().equals("Select category") && image1Uri !=null && image2Uri!=null){
            uploadTheProduct();
        }

        else {
            Toast.makeText(this, "Check Missing Fields", Toast.LENGTH_LONG).show();
        }
    }

    private void startLoader(){
        loader.setMessage("Posting Product. Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadTheProduct() {
        startLoader();

        Date date = new java.util.Date();

        HashMap<String, Object> hashMap =   new HashMap<>();
        hashMap.put("productid", productid);
        hashMap.put("productShortDescription", getShortDescription());
        hashMap.put("productDetails",getProductDetails());
        hashMap.put("productTitle", getProductTitle());
        hashMap.put("newPrice", getNewPrice());
        hashMap.put("oldPrice", getOldPrice());
        hashMap.put("warranty", getWarranty());
        hashMap.put("ratings", "0");
        hashMap.put("available", getAvailability());
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("category", getSelectedCategory());
        hashMap.put("dateCreated", date.toString());
        hashMap.put("totalRatings", "0");
        hashMap.put("peopleRated", "0");

        assert productid != null;
        reference.child(productid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PostProductActivity.this, "Details Uploaded successfully.", Toast.LENGTH_SHORT).show();
                    uploadPostImage1();

                }else {
                    Toast.makeText(PostProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void uploadPostImage1() {
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(image1Uri));
        uploadTask = fileReference.putFile(image1Uri);
        uploadTask.continueWithTask(new Continuation(){
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    myUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("productImage1", myUrl);

                    reference.child(productid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(PostProductActivity.this, "Image 1 added successfully", Toast.LENGTH_SHORT).show();
                                uploadImage2();
                            }

                        }
                    });
                    // finish();

                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(PostProductActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostProductActivity.this, "Image 1 could not be added."+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadImage2() {
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(image2Uri));
        uploadTask = fileReference.putFile(image2Uri);
        uploadTask.continueWithTask(new Continuation(){
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    myUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("productImage2", myUrl);

                    reference.child(productid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(PostProductActivity.this, "Image 2 added successfully", Toast.LENGTH_SHORT).show();

                            }
                            loader.dismiss();
                            finish();
                        }
                    });


                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(PostProductActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostProductActivity.this, "Image 2 could not be added." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data !=null){
            image1Uri = data.getData();
            productImage1.setImageURI(image1Uri);
        }
        else if (requestCode == 2 && resultCode == RESULT_OK && data !=null){
            image2Uri = data.getData();
            productImage2.setImageURI(image2Uri);
        }else {
            Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();

        }
    }



    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Product Creation")
                .setMessage("Are you sure you want to leave?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PostProductActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}