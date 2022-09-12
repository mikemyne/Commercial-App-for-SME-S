package com.example.e_commerceapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_commerceapp.Activities.EditProductActivity;
import com.example.e_commerceapp.Activities.MessageAdminActivity;
import com.example.e_commerceapp.Activities.ProductDetailsActivity;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public Context mContext;
    public List<Product> mProduct;
    private FirebaseUser firebaseUser;

    public ProductAdapter(Context mContext, List<Product> mProduct) {
        this.mContext = mContext;
        this.mProduct = mProduct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.products_retrieved_layout,parent,false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Product product = mProduct.get(position);

        if (product.getPublisher() .equals(firebaseUser.getUid())){
            holder.addToCart.setVisibility(View.GONE);
            holder.star.setVisibility(View.GONE);
        }

        holder.productTitle.setText(product.getProductTitle());
        holder.productShortDescription.setText(product.getProductShortDescription());
        Glide.with(mContext).load(product.getProductImage1())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.productImage1);
        holder.oldPrice.setText("Was KSH.: "+product.getOldPrice());
        holder.newPrice.setText("Now KSH.: "+product.getNewPrice());
        holder.warranty.setText("Available: "+product.getWarranty());
        holder.ratings.setText("Rate: "+product.getRatings() + "/5");
        holder.available.setText("No. Available: "+product.getAvailable());


        isStarred(product.getProductid(), holder.star);
        isAddedToCart(product.getProductid(), holder.addToCart);

        //DISPLAYING THE 'NEW' TAG ON PRODUCTS
        DateFormat df = new SimpleDateFormat("hh:mm:ss_yyyy.MM.dd");
        Date currentDate = new java.util.Date();
        String date = product.getDateCreated().toString();
        Date date2 = null;
        try {
            date2 = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = 0;
        if (date2 != null) {
            diff = currentDate.getTime() - date2.getTime();
        }

        long newTime =  diff / 1000 / 60 / 60 / 24;
        if (newTime >7){
            holder.newProductAlert.setVisibility(View.GONE);
        }


      //  --------------------------


        holder.productImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("productid", product.getProductid());
                editor.apply();

                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                mContext.startActivity(intent);
            }
        });

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

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.addToCart.getTag().equals("add")){
                    HashMap hashmap = new HashMap();
                    hashmap.put("productId", product.getProductid());
                    hashmap.put("date", DateFormat.getDateInstance().format(new Date()));
                    hashmap.put("productName", product.getProductTitle());
                    hashmap.put("productPrice", product.getNewPrice());
                    hashmap.put("productQuantity", "1");
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

        holder.ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View mView = inflater.inflate(R.layout.rate_product_layout, null);

                myDialog.setView(mView);

                final AlertDialog dialog = myDialog.create();

                final RatingBar ratingBar = mView.findViewById(R.id.ratingBar);
                final Button submitRatingBtn  = mView.findViewById(R.id.submitRatingBtn);

                submitRatingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        float getRating = ratingBar.getRating();


                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products")
                                .child(product.getProductid());
                        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                               int peopleRated = Integer.parseInt(snapshot.child("peopleRated").getValue().toString());
                               int totalRatings = Integer.parseInt(snapshot.child("totalRatings").getValue().toString());

                               int newPeopleTotal = peopleRated +1;
                               int newTotalRatings = totalRatings + Math.round(getRating);

                               int newProductRating = newTotalRatings/newPeopleTotal;

                               HashMap hashMap = new HashMap();
                               hashMap.put("totalRatings", String.valueOf(newTotalRatings));
                               hashMap.put("peopleRated",String.valueOf(newPeopleTotal));
                               hashMap.put("ratings",String.valueOf(newProductRating));


                                productRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                       if (task.isSuccessful()){
                                           Toast.makeText(mContext, "RATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                       }else {
                                           Toast.makeText(mContext, "RATING FAILED "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                       }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });




        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:

                                Intent editIntent = new Intent(mContext, EditProductActivity.class);
                                editIntent.putExtra("productId", product.getProductid());
                                mContext.startActivity(editIntent);

                                return true;
                            case R.id.delete:
                                new androidx.appcompat.app.AlertDialog.Builder(mContext)
                                        .setTitle("Product Deletion")
                                        .setMessage("Are you sure you want to delete this product?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                FirebaseDatabase.getInstance().getReference("products")
                                                        .child(product.getProductid()).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(mContext, "product deleted successfully", Toast.LENGTH_SHORT).show();
                                                                }else {
                                                                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                return true;

                            case R.id.message_admin:
                                Intent intent1 = new Intent(mContext, MessageAdminActivity.class);
                                intent1.putExtra("publisherId", product.getPublisher());
                                mContext.startActivity(intent1);
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                popupMenu.inflate(R.menu.post_menu);
                if (product.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.message_admin).setVisible(false);
                }
               else {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.message_admin).setVisible(true);
                }

                popupMenu.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return mProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView productImage1, star, more, newProductAlert;
        public TextView productTitle, productShortDescription,oldPrice, newPrice, warranty, ratings, available, addToCart;

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
            more = itemView.findViewById(R.id.more);
            productTitle = itemView.findViewById(R.id.productTitle);
            newProductAlert = itemView.findViewById(R.id.newProductAlert);

        }
    }


    private void isStarred(final String productid, final ImageView star){
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
