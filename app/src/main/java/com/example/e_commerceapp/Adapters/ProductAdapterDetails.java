package com.example.e_commerceapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_commerceapp.Models.Product;
import com.example.e_commerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProductAdapterDetails extends RecyclerView.Adapter<ProductAdapterDetails.ViewHolder> {

    public Context mContext;
    public List<Product> mProduct;
    private FirebaseUser firebaseUser;

    int quantity = 0;
    int finalprice = 0;
    int price = 0;

    public ProductAdapterDetails(Context mContext, List<Product> mProduct) {
        this.mContext = mContext;
        this.mProduct = mProduct;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.products_retrieved_details,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Product product = mProduct.get(position);

        if (product.getPublisher() .equals(firebaseUser.getUid())){
            holder.addToCart.setVisibility(View.GONE);
            holder.star.setVisibility(View.GONE);
            holder.incanddeclayout.setVisibility(View.GONE);
        }

        holder.productTitle.setText(product.getProductTitle());
        holder.productShortDescription.setText(product.getProductShortDescription());
        Glide.with(mContext).load(product.getProductImage1()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.productImage1);
        holder.oldPrice.setText("Was KSH.: "+product.getOldPrice());
        holder.newPrice.setText("Now KSH.: "+product.getNewPrice());
        holder.warranty.setText("Available: "+product.getWarranty());
        holder.ratings.setText("Rate: "+product.getRatings()+ "/5");
        holder.available.setText("No. Available: "+product.getAvailable());
        holder.expand_text_view.setText(product.getProductDetails());

        isStarred(product.getProductid(), holder.star);
        isAddedToCart(product.getProductid(), holder.addToCart);


        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.star.getTag().equals("star")){
                    FirebaseDatabase.getInstance().getReference().child("favourites").child(firebaseUser.getUid()).child(product.getProductid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("favourites").child(firebaseUser.getUid()).child(product.getProductid()).removeValue();
                }
            }
        });

        holder.nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(mContext).load(product.getProductImage2()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.productImage1);
                holder.nextImage.setEnabled(false);
                holder.previousImage.setEnabled(true);
            }
        });

        holder.previousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(mContext).load(product.getProductImage1()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.productImage1);
                holder.previousImage.setEnabled(false);
                holder.nextImage.setEnabled(true);
            }
        });



        holder.addquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quantity = Integer.parseInt(holder.quantitycounterproductdetail.getText().toString());
                quantity++;

                holder.quantitycounterproductdetail.setText(String.valueOf(quantity));



                /*finalprice = quantity * price;
                priceview.setText("Total is "  + String.valueOf(quantity) +" x " + String.valueOf(finalprice));

                firestore.collection("Products").document(productid).update("quantity", quantity).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });*/
            }
        });


       holder.subquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quantity = Integer.parseInt(holder.quantitycounterproductdetail.getText().toString());

                if (quantity <=0) {
                    quantity = 0;
                    finalprice = 0;

                } else {
                    quantity--;
                    finalprice = quantity * price;

                    holder.quantitycounterproductdetail.setText(String.valueOf(quantity));
                   /* priceview.setText("Total is "  + String.valueOf(quantity) +" x " + String.valueOf(finalprice));

                    firestore.collection("Products").document(productid).update("quantity", quantity).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });*/
                }
            }
        });

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.addToCart.getTag().equals("add")){

                    int price = Integer.parseInt(product.getNewPrice());
                    int qty = Integer.parseInt(holder.quantitycounterproductdetail.getText().toString());
                    int finalPrice = price * qty;
                    String finalPriceString = String.valueOf(finalPrice);

                    HashMap hashmap = new HashMap();
                    hashmap.put("productId", product.getProductid());
                    hashmap.put("date", DateFormat.getDateInstance().format(new Date()));
                    hashmap.put("productName", product.getProductTitle());
                    hashmap.put("productPrice", finalPriceString);
                    hashmap.put("productQuantity", holder.quantitycounterproductdetail.getText().toString());
                    hashmap.put("productImage", product.getProductImage1());
                    hashmap.put("publisher", firebaseUser.getUid());

                    FirebaseDatabase.getInstance().getReference().child("cart")
                            .child("buyer")
                            .child(firebaseUser.getUid())
                            .child("products")
                            .child(product.getProductid()).setValue(hashmap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mContext, "ADDED TO CART", Toast.LENGTH_SHORT).show();

                                        DatabaseReference adminCartRef = FirebaseDatabase.getInstance().getReference("cart")
                                                .child("AdminCartView").child(firebaseUser.getUid())
                                                .child("products").child(product.getProductid());
                                        adminCartRef.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(mContext,
                                                            "ADMIN CART UPDATED", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }else {
                                        Toast.makeText(mContext, "COULD NOT BE ADDED "+ task.getException().toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("cart")
                            .child("buyer")
                            .child(firebaseUser.getUid())
                            .child("products")
                            .child(product.getProductid()).removeValue().addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(mContext, "REMOVED FROM CART", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(mContext,
                                                "COULD NOT BE REMOVED "+task.getException().toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView productImage1, star, more, previousImage, nextImage;
        public TextView productTitle, productShortDescription,oldPrice, newPrice, warranty, ratings, available, addToCart;
        public ExpandableTextView expand_text_view;

        public TextView quantitycounterproductdetail;
        public Button addquantity, subquantity;

        public LinearLayout incanddeclayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage1 = itemView.findViewById(R.id.productImage1);
            star = itemView.findViewById(R.id.star);
            productShortDescription = itemView.findViewById(R.id.productShortDescription);
            oldPrice = itemView.findViewById(R.id.oldPrice);
            newPrice = itemView.findViewById(R.id.newPrice);
            warranty = itemView.findViewById(R.id.warranty);
            ratings = itemView.findViewById(R.id.ratings);
            available = itemView.findViewById(R.id.available);
            addToCart = itemView.findViewById(R.id.addToCart);
            expand_text_view = itemView.findViewById(R.id.expand_text_view);
            more = itemView.findViewById(R.id.more);
            productTitle = itemView.findViewById(R.id.productTitle);
            previousImage = itemView.findViewById(R.id.previousImage);
            nextImage = itemView.findViewById(R.id.nextImage);

            quantitycounterproductdetail = itemView.findViewById(R.id.quantitycounterproductdetail);
            addquantity = itemView.findViewById(R.id.addquantity);
            subquantity = itemView.findViewById(R.id.subquantity);

            incanddeclayout = itemView.findViewById(R.id.incanddeclayout);

        }
    }

    private void isStarred(String productid, final ImageView star){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("favourites").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(productid).exists()){
                    star.setImageResource(R.drawable.ic_starred);
                    star.setTag("starred");
                }else {
                    star.setImageResource(R.drawable.ic_star);
                    star.setTag("star");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isAddedToCart(final String productid, final TextView addToCart){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("cart")
                .child("buyer")
                .child(firebaseUser.getUid())
                .child("products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(productid).exists()){
                    addToCart.setText("REMOVE FROM CART");
                    addToCart.setBackgroundResource(R.drawable.buttons3);
                    addToCart.setTag("added");
                }else {
                    addToCart.setText("ADD TO CART");
                    addToCart.setBackgroundResource(R.drawable.buttons2);
                    addToCart.setTag("add");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
