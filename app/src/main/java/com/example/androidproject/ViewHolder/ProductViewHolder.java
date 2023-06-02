package com.example.androidproject.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView nameView;
    public TextView emailView;
    public RelativeLayout relativeLayout;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imageview);
        nameView = itemView.findViewById(R.id.name);
        emailView = itemView.findViewById(R.id.email);
        relativeLayout = itemView.findViewById(R.id.product_view_click);
    }
}

