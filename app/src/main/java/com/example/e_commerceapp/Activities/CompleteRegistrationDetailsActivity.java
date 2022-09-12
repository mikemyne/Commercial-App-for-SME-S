package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteRegistrationDetailsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextInputEditText fullName, phone,idNumber,residence;
    private Button CompleteRegistrationBtn;




    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private Uri profileImageUri;

    private ProgressDialog loader;


    StorageTask uploadTask;
    StorageReference storageReference;
    private String imageSaveLocationUrl ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_complete_registration_details);

        profileImage = findViewById(R.id.profileImage);
        fullName = findViewById(R.id.fullName);
        phone = findViewById(R.id.phone);
        idNumber = findViewById(R.id.idNumber);
        residence = findViewById(R.id.residence);
        CompleteRegistrationBtn = findViewById(R.id.CompleteRegistrationBtn);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference("users details images");


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        CompleteRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performValidations();
            }
        });

    }
    String getFullName(){
        return fullName.getText().toString();
    }
    String getPhoneNumber(){
        return phone.getText().toString();
    }
    String getIdNumber(){
        return idNumber.getText().toString();
    }
    String getAreaOfResidence(){
        return residence.getText().toString();
    }

    private void performValidations(){
        if (!getFullName().isEmpty() && !getPhoneNumber().isEmpty()
                && !getIdNumber().isEmpty() && !getAreaOfResidence().isEmpty()
                && profileImageUri!=null){
            uploadRegistrationDetails();
        }else {
            Toast.makeText(this, "Please fill the missing fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoader(){
        loader.setMessage("Saving registration details. Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /*UPLOADING REGISTRATION DETAILS*/
    private void uploadRegistrationDetails() {
        startLoader();

        HashMap<String, Object> hashMap =   new HashMap<>();
        hashMap.put("fullname", getFullName());
        hashMap.put("phonenumber", getPhoneNumber());
        hashMap.put("idnumber",getIdNumber());
        hashMap.put("areaofresidence", getAreaOfResidence());

        usersRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CompleteRegistrationDetailsActivity.this, "Details Uploaded successfully", Toast.LENGTH_SHORT).show();
                    uploadProfilePicture();
                }else {
                    Toast.makeText(CompleteRegistrationDetailsActivity.this, "Failed to upload details" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void uploadProfilePicture() {
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(profileImageUri));
        uploadTask = fileReference.putFile(profileImageUri);
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
                    imageSaveLocationUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("profileimageurl", imageSaveLocationUrl);

                    usersRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CompleteRegistrationDetailsActivity.this, "profile image added successfully", Toast.LENGTH_SHORT).show();
                            directUserToLoginActivity();
                        }
                    });
                    loader.dismiss();
                    finish();

                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(CompleteRegistrationDetailsActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompleteRegistrationDetailsActivity.this, "Profile image could not be added."+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void directUserToLoginActivity(){
        Intent intent = new Intent(CompleteRegistrationDetailsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            profileImageUri = data.getData();
            profileImage.setImageURI(profileImageUri);
        } else {
            Toast.makeText(this, "Nothing selected!", Toast.LENGTH_SHORT).show();
        }
    }
}