package com.example.androidproject.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.AddToCart;
import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.squareup.picasso.Picasso;

public class CartViewHolder extends RecyclerView.ViewHolder{
    public ImageView cartImage;
    public TextView cartProductName, cartProductPrice, cartProductQty, deleteProduct;
    public RelativeLayout cartClick;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        cartImage = itemView.findViewById(R.id.cart_product_image);
        cartProductName = itemView.findViewById(R.id.cart_product_name);
        cartProductPrice = itemView.findViewById(R.id.cart_product_price);
        cartProductQty = itemView.findViewById(R.id.cart_product_qty);
        cartClick = itemView.findViewById(R.id.cart_view_click);
        deleteProduct = itemView.findViewById(R.id.cart_product_delete_txt);
    }

    public void bind(AddToCart cart) {
        // Assuming 'product' contains the necessary information, including the image URL

        // Load image using Picasso
        Picasso.get().load(cart.getCartProductImage()).into(cartImage);
    }
}
