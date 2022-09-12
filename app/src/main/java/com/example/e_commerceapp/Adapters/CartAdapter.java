package com.example.e_commerceapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_commerceapp.Models.Cart;
import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public Context mContext;
    public List<Cart> mCart;
    private FirebaseUser firebaseUser;

    public CartAdapter(Context mContext, List<Cart> mCart) {
        this.mContext = mContext;
        this.mCart = mCart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_items_layout,parent,false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Cart cart = mCart.get(position);

        holder.productName.setText(cart.getProductName());
        holder.productQuantity.setText("Quantity " +cart.getProductQuantity());
        holder.date.setText(cart.getDate());
        holder.productPrice.setText("KSH. " +cart.getProductPrice());
        Glide.with(mContext).load(cart.getProductImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.productImage1);

        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference removeFromCrtRef = FirebaseDatabase.getInstance().getReference("cart")
                        .child("buyer").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("products")
                        .child(cart.getProductId());
                removeFromCrtRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           DatabaseReference removeFromCrtRef2 = FirebaseDatabase.getInstance().getReference("cart")
                                   .child("AdminCartView").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .child("products")
                                   .child(cart.getProductId());
                           removeFromCrtRef2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()){
                                      Toast.makeText(mContext, "REMOVED FROM CART", Toast.LENGTH_SHORT).show();
                                  }
                               }
                           });

                       }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return mCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView productImage1;
        public TextView productName, productQuantity,date, productPrice, removeFromCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage1 = itemView.findViewById(R.id.productImage1);
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            date = itemView.findViewById(R.id.date);
            productPrice = itemView.findViewById(R.id.productPrice);
            removeFromCart = itemView.findViewById(R.id.removeFromCart);


        }
    }
}
