package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.e_commerceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView fullName, email, areaofresidence, phonenumber, idnumber;
    private TextView  typeOfUser;
    private Button backBtn;

    private DatabaseReference userDatabaseRef;

    private Toolbar settingsToolbar;

    private ProgressDialog loader;

    String userid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_details);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        //setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Full User Details");

        profileImage = findViewById(R.id.profileImage);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        areaofresidence = findViewById(R.id.areaofresidence);
        phonenumber = findViewById(R.id.phonenumber);
        idnumber = findViewById(R.id.idnumber);
        backBtn = findViewById(R.id.backButton);
        typeOfUser = findViewById(R.id.typeOfUser);
        loader = new ProgressDialog(this);

        if(getIntent() != null) {
            userid = getIntent().getStringExtra("userid").toString();
            userDatabaseRef  = FirebaseDatabase.getInstance().getReference("users").child(userid);

            getUserInformation();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getUserInformation() {
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    if (snapshot.hasChild("areaofresidence")){
                        areaofresidence.setText("Residence: "+snapshot.child("areaofresidence").getValue().toString());
                    }else {
                        areaofresidence.setVisibility(View.GONE);
                    }

                    if (snapshot.hasChild("email")){
                        email.setText("Email: "+snapshot.child("email").getValue().toString());
                    }else {
                        email.setVisibility(View.GONE);
                    }

                    if (snapshot.hasChild("fullname")){
                        fullName.setText("Full name: "+snapshot.child("fullname").getValue().toString());
                    }else {
                        fullName.setVisibility(View.GONE);
                    }

                    if (snapshot.hasChild("idnumber")){
                        idnumber.setText("ID number: "+snapshot.child("idnumber").getValue().toString());
                    }else {
                        idnumber.setVisibility(View.GONE);
                    }

                    if (snapshot.hasChild("phonenumber")){
                        phonenumber.setText("Tel no.: "+snapshot.child("phonenumber").getValue().toString());
                    }else {
                        phonenumber.setVisibility(View.GONE);
                    }

                    if (snapshot.hasChild("type")){
                        typeOfUser.setText("Type: "+ snapshot.child("type").getValue().toString());

                    }else {
                        typeOfUser.setVisibility(View.GONE);
                    }

                    /*IMAGES RETRIEVAL*/
                    if (snapshot.hasChild("profileimageurl")){
                        Glide.with(getApplicationContext()).load(snapshot.child("profileimageurl")
                                .getValue().toString()).into(profileImage);
                    }else {
                        profileImage.setVisibility(View.GONE);
                    }

                }
                else {
                    Toast.makeText(UserDetailsActivity.this, "Child does not exist", Toast.LENGTH_SHORT).show();
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