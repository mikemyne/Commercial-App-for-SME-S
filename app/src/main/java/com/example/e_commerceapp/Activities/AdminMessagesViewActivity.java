package com.example.e_commerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.e_commerceapp.Models.AdminMessages;
import com.example.e_commerceapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminMessagesViewActivity extends AppCompatActivity {

    private DatabaseReference messagesRef;
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    String name = "";
    String profilePic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_messages_view);

        messagesRef = FirebaseDatabase.getInstance().getReference("adminmessages");

        toolbar = findViewById(R.id.mToolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("User Messages");

        recyclerView = findViewById(R.id.RecyclerViewAdminMsgs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<AdminMessages> options = new FirebaseRecyclerOptions.Builder<AdminMessages>()
                .setQuery(messagesRef, AdminMessages.class)
                .build();

        FirebaseRecyclerAdapter<AdminMessages, MyViewHolder> adapter = new FirebaseRecyclerAdapter<AdminMessages, AdminMessagesViewActivity.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminMessagesViewActivity.MyViewHolder holder, int position, @NonNull AdminMessages model) {

                holder.userName.setText(model.getSendername());
                holder.usermessage.setText(model.getMessage());
                Glide.with(AdminMessagesViewActivity.this).load(model.getProfileimageurl()).into(holder.profileImage);
                holder.date.setText("Sent on: "+model.getDate());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AdminMessagesViewActivity.this, UserDetailsActivity.class);
                        intent.putExtra("userid", model.getSenderid());
                        AdminMessagesViewActivity.this.startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public AdminMessagesViewActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_admin_messages,parent,false);
                return new AdminMessagesViewActivity.MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public  static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userName, usermessage,date;
        CircleImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            usermessage = itemView.findViewById(R.id.user_message);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            date = itemView.findViewById(R.id.date);

        }
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