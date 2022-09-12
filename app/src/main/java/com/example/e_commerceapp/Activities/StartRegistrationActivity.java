package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StartRegistrationActivity extends AppCompatActivity {

    private TextInputEditText regEmail, regPassword;
    private Button regBtn;
    private TextView regPageQuestion;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference reference;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_registration);

        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regBtn = findViewById(R.id.regBtn);
        regPageQuestion = findViewById(R.id.regPageQuestion);
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        regPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performValidations();
            }
        });
    }

    private void performValidations() {
        final String email = regEmail.getText().toString();
        String password = regPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            regEmail.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(password)){
            regPassword.setError("Password is required!");
            return;
        }else {
            loader.setMessage("Registration in progress, please wait...");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mUser = mAuth.getCurrentUser();
                        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(StartRegistrationActivity.this, "Please check your email for verification before you can log in", Toast.LENGTH_LONG).show();
                                    reference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("type", "buyer");
                                    hashMap.put("userid", mUser.getUid());
                                    hashMap.put("email", email);
                                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(StartRegistrationActivity.this, "user data added successfully", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(StartRegistrationActivity.this, "user data could not be added "+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Intent intent = new Intent(StartRegistrationActivity.this, CompleteRegistrationDetailsActivity.class);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(StartRegistrationActivity.this, "Error sending verification code, please try again "+task.getException(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    }else {
                        Toast.makeText(StartRegistrationActivity.this, "Registration Error "+ task.getException(), Toast.LENGTH_LONG).show();
                    }
                    loader.dismiss();
                }
            });
        }
    }
}