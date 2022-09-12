package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private CircleImageView settingsProfileImage;
    private TextInputEditText fullName, phone,idNumber,residence;

    private Button settingsUpdateDetailsButton,settingsBackButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private String userID;

    private ProgressDialog loader;

    private String mName = "";
    private String mIdNumber = "";
    private String mPhoneNumber = "";
    private String mProfilePicture = "";
    private String mResidence = "";

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.settingsToolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        settingsProfileImage = findViewById(R.id.settingsProfileImage);
        fullName = findViewById(R.id.fullName);
        phone = findViewById(R.id.phone);
        idNumber = findViewById(R.id.idNumber);
        residence = findViewById(R.id.residence);
        settingsUpdateDetailsButton = findViewById(R.id.settingsUpdateDetailsButton);
        settingsBackButton = findViewById(R.id.settingsBackButton);

        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);

        getUserInfo();


        settingsProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        settingsUpdateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInformation();
            }
        });


        settingsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyGoingBack();
            }
        });

    }

    private void getUserInfo(){
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("fullname") !=null){
                        mName = map.get("fullname").toString();
                        fullName.setText(mName);
                        fullName.setSelection(mName.length());
                    }

                    if (map.get("idnumber") !=null){
                        mIdNumber = map.get("idnumber").toString();
                        idNumber.setText(mIdNumber);
                        idNumber.setSelection(mIdNumber.length());
                    }

                    if (map.get("phonenumber") !=null){
                        mPhoneNumber = map.get("phonenumber").toString();
                        phone.setText(mPhoneNumber);
                        phone.setSelection(mPhoneNumber.length());
                    }

                    if (map.get("areaofresidence") !=null){
                        mResidence = map.get("areaofresidence").toString();
                        residence.setText(mResidence);
                        residence.setSelection(mResidence.length());
                    }

                    if (map.get("profileimageurl") !=null){
                        mProfilePicture = map.get("profileimageurl").toString();
                        Glide.with(getApplication()).load(mProfilePicture).into(settingsProfileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void saveUserInformation() {
        final String name = fullName.getText().toString();
        final String phoneno = phone.getText().toString();
        final String idno = idNumber.getText().toString();
        final String areaofres = residence.getText().toString();

        if (TextUtils.isEmpty(name)){
            fullName.setError("Name is required!");
            return;
        }

        if (TextUtils.isEmpty(phoneno)){
            phone.setError("Phone Number Required");
        }

        if (TextUtils.isEmpty(idno)){
            idNumber.setError("Id Number Required");
        }
        if (TextUtils.isEmpty(areaofres)){
            residence.setError("Residence Required");
        }

        if (resultUri==null){
            Toast.makeText(this, "Profile Image required", Toast.LENGTH_SHORT).show();
        }

        else {
            loader.setMessage("Uploading details");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            Map userInfo = new HashMap();
            userInfo.put("fullname",name);
            userInfo.put("phonenumber", phoneno);
            userInfo.put("idnumber", idno);
            userInfo.put("areaofresidence", areaofres);

            userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        String error = task.getException().toString();
                        Toast.makeText(ProfileActivity.this, "Update Failed: "+ error, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    loader.dismiss();
                }
            });
        }

        if (resultUri !=null){
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("users details images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20,byteArrayOutputStStream);
            byte[] data = byteArrayOutputStStream.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Map newImageMap = new HashMap();
                                    newImageMap.put("profileimageurl", imageUrl);
                                    userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProfileActivity.this, "successful", Toast.LENGTH_SHORT).show();
                                            }else {
                                                String error = task.getException().toString();
                                                Toast.makeText(ProfileActivity.this, "Process failed "+ error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    finish();
                                }
                            });
                        }
                    }
                }
            });
        }else {
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            settingsProfileImage.setImageURI(resultUri);
        }
    }



    private void verifyGoingBack() {
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    finish();
                }else {
                    Toast.makeText(ProfileActivity.this, "You Must Update Your Profile First", Toast.LENGTH_LONG).show();
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