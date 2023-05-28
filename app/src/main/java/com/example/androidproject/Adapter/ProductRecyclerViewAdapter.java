package com.example.androidproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.example.androidproject.ViewHolder.ProductViewHolder;

import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductViewHolder> {


    Context context;
    List<Products> items;

    public ProductRecyclerViewAdapter(Context context, List<Products> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.nameView.setText(items.get(position).getmProductName());
        holder.emailView.setText(items.get(position).getmProductDescription());
        holder.imageView.setImageResource(items.get(position).getmProductImage());
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.product_view,parent,false));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
