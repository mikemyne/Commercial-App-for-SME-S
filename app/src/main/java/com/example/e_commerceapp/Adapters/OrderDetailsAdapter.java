package com.example.e_commerceapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_commerceapp.Models.Cart;
import com.example.e_commerceapp.Models.IDOFTHEADMIN;
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

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    public Context mContext;
    public List<Cart> mCart;
    private FirebaseUser firebaseUser;

    public OrderDetailsAdapter(Context mContext, List<Cart> mCart) {
        this.mContext = mContext;
        this.mCart = mCart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_details_layout,parent,false);
        return new OrderDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Cart cart = mCart.get(position);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(IDOFTHEADMIN.getIdOfTheAdmin())){
            holder.cancelOrderBtn.setVisibility(View.GONE);
        }

        holder.productName.setText(cart.getProductName());
        holder.productQuantity.setText("Quantity " +cart.getProductQuantity());
        holder.date.setText(cart.getDate());
        holder.productPrice.setText("KSH. " +cart.getProductPrice());
        Glide.with(mContext).load(cart.getProductImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.productImage1);


        holder.cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("CANCEL ORDER")
                        .setMessage("Are you sure you want to cancel your order?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                DatabaseReference adminReference = FirebaseDatabase.getInstance().getReference("cart")
                                        .child("AdminCartView")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("products")
                                        .child(cart.getProductId());
                                adminReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                      if (task.isSuccessful()){

                                          DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("orders")
                                                  .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                  .child(cart.getProductId());
                                          orderReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {

                                                  if (task.isSuccessful()){{
                                                      Toast.makeText(mContext, "ORDER CANCELLED", Toast.LENGTH_SHORT).show();
                                                  }}

                                                  addNotifications();
                                              }
                                          });

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
        return mCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView productImage1;
        public TextView productName, productQuantity,date, productPrice, removeFromCart, cancelOrderBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage1 = itemView.findViewById(R.id.productImage1);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            date = itemView.findViewById(R.id.date);
            productPrice = itemView.findViewById(R.id.productPrice);
            removeFromCart = itemView.findViewById(R.id.removeFromCart);
            cancelOrderBtn = itemView.findViewById(R.id.cancelOrderBtn);


        }
    }


    private void addNotifications(){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications")
                .child(IDOFTHEADMIN.getIdOfTheAdmin());
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", "Cancelled an order.");
        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("postid", null);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap);
    }
}
