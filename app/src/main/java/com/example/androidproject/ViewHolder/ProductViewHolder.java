package com.example.androidproject.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public ImageView productImg;
    public TextView productName, productDesc, productPrice, editProd;
    public RelativeLayout relativeLayout;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        productImg = itemView.findViewById(R.id.product_image);
        productName = itemView.findViewById(R.id.product_name);
        productDesc = itemView.findViewById(R.id.product_description);
        productPrice = itemView.findViewById(R.id.product_price);
        relativeLayout = itemView.findViewById(R.id.product_view_click);
        editProd = itemView.findViewById(R.id.product_edit_txt);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userRef = usersRef.child(SignInSingleton.getInstance().getAuthUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String usertype = dataSnapshot.child("usertype").getValue(String.class);

                    if(usertype.equals("CUSTOMER")){
                        editProd.setVisibility(View.GONE);
                    }else{
                        editProd.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

    }

    public void bind(Products product) {
        // Assuming 'product' contains the necessary information, including the image URL

        // Load image using Picasso
        Picasso.get().load(product.getmProductImage()).into(productImg);
    }
}

