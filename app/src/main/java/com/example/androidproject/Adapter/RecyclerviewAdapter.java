package com.example.androidproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Activity.ProductActivity;
import com.example.androidproject.Fragments.HomeFragment;
import com.example.androidproject.Model.Branches;
import com.example.androidproject.Model.ClickedBranch;
import com.example.androidproject.R;
import com.example.androidproject.ViewHolder.MyViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecyclerviewAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context context;
    List<Branches> items;
    private LayoutInflater inflater;
    private EditText branch_name, branch_Address;
    public RecyclerviewAdapter(Context context, List<Branches> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
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

                /*ClickedBranch clickedBranch = new ClickedBranch();
                if(clickedBranch.getClickedBranchID() == null){
                    clickedBranch.setClickedBranchID(items.get(position).getmBranchID());
                }*/

                //Getting the clicked branch ID
                ClickedBranch clickedBranch = new ClickedBranch();
                clickedBranch.setClickedBranchID(items.get(position).getmBranchID());


            }
        });

        //This is the button of 'EDIT THIS BRANCH' sa Branch List Recyclerview
        holder.txteditBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "Clicked: " + data, Toast.LENGTH_SHORT).show();
                String branchId = items.get(position).getmBranchID();
                editBranchHere(context, branchId);
            }
        });


    }

    public void editBranchHere(Context context, String branchId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("branches").child(branchId);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Branches");

        // Inflate the form layout XML file
        View formView = LayoutInflater.from(context).inflate(R.layout.branch_edit, null);
        builder.setView(formView);

        EditText branch_name = formView.findViewById(R.id.txt_branchname);
        EditText branch_Address = formView.findViewById(R.id.txt_branchaddress);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the specific fields you want to display in the AlertDialog
                    String bName = dataSnapshot.child("branchname").getValue(String.class);
                    String bAddress = dataSnapshot.child("branchaddress").getValue(String.class);

                    branch_name.setText(bName);
                    branch_Address.setText(bAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the entered form data
                String bname = branch_name.getText().toString();
                String baddress = branch_Address.getText().toString();

                // Create a data object or HashMap to hold the updated form data
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("branchname", bname);
                updatedData.put("branchaddress", baddress);

                // Update the data in the Firebase Realtime Database
                databaseReference.updateChildren(updatedData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Branch updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to update branch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle positive button click if needed
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle negative button click if needed
            }
        });
        builder.show();
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