package com.example.e_commerceapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_commerceapp.Activities.UserDetailsActivity;
import com.example.e_commerceapp.Models.Notification;
import com.example.e_commerceapp.Models.User;
import com.example.e_commerceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = mNotification.get(position);

        holder.notification_comment.setText(notification.getText());
        holder.notification_date.setText("On: "+notification.getDate());

        getUserInfo(holder.notification_image_profile, holder.notification_username, notification.getUserid());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserDetailsActivity.class);
                    intent.putExtra("userid", notification.getUserid());
                    mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public CircleImageView notification_image_profile;
        private TextView notification_username, notification_comment,notification_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_image_profile = itemView.findViewById(R.id.notification_image_profile);
            notification_username = itemView.findViewById(R.id.notification_username);
            notification_comment = itemView.findViewById(R.id.notification_comment);
            notification_date = itemView.findViewById(R.id.notification_date);
        }
    }

    private void getUserInfo(final CircleImageView notification_image_profile, final TextView notification_username, String publisherid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimageurl()).into(notification_image_profile);
                notification_username.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
