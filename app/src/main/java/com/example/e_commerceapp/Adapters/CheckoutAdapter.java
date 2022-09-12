package com.example.e_commerceapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapp.Models.Checkout;
import com.example.e_commerceapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    public Context mContext;
    public List<Checkout> mCheckout;
    private FirebaseUser firebaseUser;

    public CheckoutAdapter(Context mContext, List<Checkout> mCheckout) {
        this.mContext = mContext;
        this.mCheckout = mCheckout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.checkout_summary_layout,parent,false);
        return new CheckoutAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Checkout checkout = mCheckout.get(position);

        holder.productName.setText(checkout.getProductName());
        holder.productQuantity.setText("Quantity: " +checkout.getProductQuantity());
        holder.productPrice.setText("Price: KSH. " +checkout.getProductPrice());

    }

    @Override
    public int getItemCount() {
        return mCheckout.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView productName, productQuantity, productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productPrice = itemView.findViewById(R.id.productPrice);


        }
    }
}
