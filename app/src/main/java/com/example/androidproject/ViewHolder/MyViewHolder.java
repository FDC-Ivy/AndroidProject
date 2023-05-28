package com.example.androidproject.ViewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgBranchImage;
    public TextView txtBranchName;
    public TextView txtBranchAddress;
    public RelativeLayout relativeLayout;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imgBranchImage = itemView.findViewById(R.id.branch_image);
        txtBranchName = itemView.findViewById(R.id.branch_name);
        txtBranchAddress = itemView.findViewById(R.id.branch_address);
        relativeLayout = itemView.findViewById(R.id.branch_button);
    }
}
