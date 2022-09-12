package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class MessageAdminActivity extends AppCompatActivity {

    private Toolbar adminMessageToolbar;
    private EditText messageBox;
    private Button sendMessage;
    private TextView messageAdminBackTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference messageRef,usersRef;
    private String userid, senderName,senderProfilePictureUrl;

    private String publisherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message_admin);

        if (getIntent().getExtras()!= null){
            publisherId =getIntent().getStringExtra("publisherId");
        }


        adminMessageToolbar = findViewById(R.id.adminMessageToolbar);
        //setSupportActionBar(adminMessageToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Message the Admin");

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        messageRef = FirebaseDatabase.getInstance().getReference("adminmessages");
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(userid);

        sendMessage = findViewById(R.id.sendAdminMessageBtn);
        messageBox = findViewById(R.id.messageBox);
        messageAdminBackTextView = findViewById(R.id.messageAdminBackTextView);


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderName = snapshot.child("fullname").getValue().toString();
                senderProfilePictureUrl = snapshot.child("profileimageurl").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        messageAdminBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageBox.getText().toString();
                String date = DateFormat.getDateInstance().format(new Date());
                String messageid = messageRef.push().getKey();
                if (TextUtils.isEmpty(message)){
                    messageBox.setError("Cannot be empty");
                }
                else {
                    HashMap<String, Object> messageMap = new HashMap<>();
                    messageMap.put("message", message);
                    messageMap.put("senderid", userid);
                    messageMap.put("date", date);
                    messageMap.put("messageid", messageid);
                    messageMap.put("profileimageurl", senderProfilePictureUrl);
                    messageMap.put("sendername", senderName);

                    messageRef.child(messageid).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MessageAdminActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                addNotifications();
                                finish();
                            }else {
                                Toast.makeText(MessageAdminActivity.this, "Failed to send message "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
    }

    private void addNotifications(){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications").child(publisherId);
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", "Sent you a message");
        hashMap.put("userid", userid);
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