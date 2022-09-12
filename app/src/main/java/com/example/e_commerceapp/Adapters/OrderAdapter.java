package com.example.e_commerceapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapp.Activities.OrderDetailsActivity;
import com.example.e_commerceapp.Models.IDOFTHEADMIN;
import com.example.e_commerceapp.Models.Order;
import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public Context mContext;
    public List<Order> mOrder;
    private FirebaseUser firebaseUser;

    public OrderAdapter(Context mContext, List<Order> mOrder) {
        this.mContext = mContext;
        this.mOrder = mOrder;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_list_layout,parent,false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Order order = mOrder.get(position);

        holder.firstandlastnames.setText(order.getFirstName()+ " " + order.getLastName());
        holder.orderDate.setText("Ordered on: " +order.getDate());
        holder.amount.setText("Total Amount: KSH." +order.getAmount());
        holder.streetAddress.setText("Street Address: " +order.getStreetAddress());
        holder.paymentMtd.setText("Pays by: " +order.getPaymentMethod());
        holder.postalCode.setText("From: "+ order.getCity() + ", " + order.getProvince() + ", " + order.getPostalCode());


        holder.showOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                intent.putExtra("publisher", order.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.markAsDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(mContext)
                        .setTitle("MARK AS PAID AND DELIVERED")
                        .setMessage("Are you sure you want mark as paid/delivered?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AdminOrderList")
                                        .child(order.getOrderId());

                                HashMap hashMap = new HashMap();
                                hashMap.put("status", "delivered");

                                ref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            addNotifications(order.getPublisher());
                                            Toast.makeText(mContext, "COMPLETED", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mOrder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView firstandlastnames, orderDate,amount, postalCode, streetAddress, paymentMtd;
        public Button showOrderDetails, markAsDelivered;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            firstandlastnames = itemView.findViewById(R.id.firstandlastnames);
            orderDate = itemView.findViewById(R.id.orderDate);
            amount = itemView.findViewById(R.id.amount);
            postalCode = itemView.findViewById(R.id.postalCode);
            streetAddress = itemView.findViewById(R.id.streetAddress);
            paymentMtd = itemView.findViewById(R.id.paymentMtd);
            showOrderDetails = itemView.findViewById(R.id.showOrderDetails);
            markAsDelivered = itemView.findViewById(R.id.markAsDelivered);


        }
    }

    private void addNotifications(String publisherId){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications")
                .child(publisherId);
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", "Your order was delivered!");
        hashMap.put("userid", IDOFTHEADMIN.getIdOfTheAdmin().toString());
        hashMap.put("postid", null);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap);
    }
}
