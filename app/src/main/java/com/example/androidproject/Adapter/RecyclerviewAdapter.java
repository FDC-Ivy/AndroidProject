package com.example.androidproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Activity.ProductActivity;
import com.example.androidproject.Model.Branches;
import com.example.androidproject.R;
import com.example.androidproject.ViewHolder.MyViewHolder;

public class RecyclerviewAdapter extends RecyclerView.Adapter<MyViewHolder> {


    Context context;
    List<Branches> items;

    public RecyclerviewAdapter(Context context, List<Branches> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.branch_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.nameView.setText(items.get(position).getmBranchID());
        holder.txtBranchName.setText(items.get(position).getmBranchName());
        holder.txtBranchAddress.setText(items.get(position).getmBranchAddress());
        holder.imgBranchImage.setImageResource(items.get(position).getmBranchImage());

        final Branches data = items.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click event
                //Toast.makeText(view.getContext(), "Clicked: " + data, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("key_branch_id", items.get(position).getmBranchID());
                // Create an Intent and put the Bundle as extra
                Intent intent = new Intent(context.getApplicationContext(), ProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Declare your views for each item in the RecyclerView
        // Customize this class according to your view requirements

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize your views here
        }
    }
}