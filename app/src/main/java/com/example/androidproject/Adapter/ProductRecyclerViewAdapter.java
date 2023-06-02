package com.example.androidproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Activity.ProductActivity;
import com.example.androidproject.Model.Branches;
import com.example.androidproject.Model.ClickedBranch;
import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.example.androidproject.ViewHolder.ProductViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    Context context;
    List<Products> items;
    private DatabaseReference databaseReference;

    public ProductRecyclerViewAdapter(Context context, List<Products> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        //getting product id
        String productId = items.get(position).getmProductID();
        String productPrice = items.get(position).getmProductPrice();
        String branchId = items.get(position).getmProductBranchID();

        holder.nameView.setText(items.get(position).getmProductName());
        holder.emailView.setText(items.get(position).getmProductDescription());
        holder.imageView.setImageResource(items.get(position).getmProductImage());

        final Products data = items.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click event
                showAddToCartDialog(context, productId, productPrice, branchId);
            }
        });
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

    public void showAddToCartDialog(Context context, String productId, String productPrice, String branchId) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("carts");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the layout XML file for the AlertDialog
        View formView = LayoutInflater.from(context).inflate(R.layout.add_to_cart_popup, null);
        builder.setView(formView);

        EditText quantityEditText = formView.findViewById(R.id.txt_qty);
        Button buttonAdd = formView.findViewById(R.id.btnAdd);
        Button buttonCancel = formView.findViewById(R.id.btnCancel);
        ImageButton buttonMinus = formView.findViewById(R.id.buttonMinus);
        ImageButton buttonPlus = formView.findViewById(R.id.buttonPlus);

        buttonMinus.setOnClickListener(new QuantityButtonClickListener(quantityEditText));
        buttonPlus.setOnClickListener(new QuantityButtonClickListener(quantityEditText));

        AlertDialog alertDialog = builder.create();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = quantityEditText.getText().toString();

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("carts")) {
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean branchIdExists = false;
                                    boolean productExists = false;
                                    String childKeyCart = "";

                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        String branchIDfromCarts = childSnapshot.child("cartbranchid").getValue(String.class);
                                        String cartProductId = childSnapshot.child("cartproductid").getValue(String.class);
                                        String cartProductQty = childSnapshot.child("cartqty").getValue(String.class);
                                        childKeyCart = dataSnapshot.getKey();
                                        int newQty = Integer.parseInt(cartProductQty) + Integer.parseInt(quantity);
                                        if(cartProductId == null){
                                            break;
                                        }else{
                                            if (cartProductId.equals(productId)) {
                                                productExists = true;

                                                Map<String, Object> formData = new HashMap<>();
                                                formData.put("cartproductid", productId);
                                                formData.put("cartbranchid", branchId);
                                                formData.put("cartuserid", SignInSingleton.getInstance().getAuthUserId());
                                                formData.put("cartqty", String.valueOf(newQty));

                                                // Get the key of the existing cart entry
                                                String cartKey = childSnapshot.getKey();

                                                // Update the cart entry in the "carts" table
                                                DatabaseReference cartEntryReference = databaseReference.child(cartKey);
                                                cartEntryReference.setValue(formData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Cart update successful
                                                                alertDialog.dismiss();
                                                                Toast.makeText(context, "Successfully updated cart.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Handle update failure
                                                                alertDialog.dismiss();
                                                                Toast.makeText(context, "Failed to update cart.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                break;
                                            }
                                        }

                                        if (branchIDfromCarts.equals(branchId)) {
                                            branchIdExists = true;
                                            break;
                                        }
                                    }

                                    /*if(productExists){
                                        Map<String, Object> formData = new HashMap<>();
                                        formData.put("cartproductid", productId);
                                        formData.put("cartbranchid", branchId);
                                        formData.put("cartuserid", SignInSingleton.getInstance().getAuthUserId());
                                        formData.put("cartqty", quantity);

                                        // Update the data in the Firebase Realtime Database
                                        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("carts").child(childKeyCart).child(productId);
                                        databaseReference3.updateChildren(formData)
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
                                    }*/

                                    if (branchIdExists && !productExists) {
                                        // Branch ID exists in the "carts" table
                                        Map<String, Object> formData = new HashMap<>();
                                        formData.put("cartproductid", productId);
                                        formData.put("cartbranchid", branchId);
                                        formData.put("cartuserid", SignInSingleton.getInstance().getAuthUserId());
                                        formData.put("cartqty", quantity);

                                        // Save the form data to Firebase Realtime Database
                                        databaseReference.push().setValue(formData);
                                        alertDialog.dismiss();

                                        Toast.makeText(context, "Successfully added to cart.", Toast.LENGTH_SHORT).show();
                                    } else if (!branchIdExists && !productExists){
                                        // Branch ID does not exist in the "carts" table
                                        showWarningIfNewBranch(context);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    // Log.w("Firebase", "Failed to read value.", error.toException());
                                }
                            });
                        } else {
                            // "carts" table does not exist
                            Map<String, Object> formData = new HashMap<>();
                            formData.put("cartproductid", productId);
                            formData.put("cartbranchid", branchId);
                            formData.put("cartuserid", SignInSingleton.getInstance().getAuthUserId());
                            formData.put("cartqty", quantity);

                            // Save the form data to Firebase Realtime Database
                            databaseReference.push().setValue(formData);
                            alertDialog.dismiss();

                            Toast.makeText(context, "Successfully added to cart.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle any errors
                    }
                });
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public class QuantityButtonClickListener implements View.OnClickListener {
        private EditText quantityEditText;

        public QuantityButtonClickListener(EditText quantityEditText) {
            this.quantityEditText = quantityEditText;
        }

        @Override
        public void onClick(View v) {
            int quantity = Integer.parseInt(quantityEditText.getText().toString());

            switch (v.getId()) {
                case R.id.buttonMinus:
                    if (quantity > 1) {
                        quantity--;
                    }
                    break;

                case R.id.buttonPlus:
                    quantity++;
                    break;
            }
            quantityEditText.setText(String.valueOf(quantity));
        }
    }

    public void showWarningIfNewBranch(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Warning: You are adding an order from different branch.")
                .setMessage("All your items in cart will be removed, continue?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle OK button click
                        deleteAllCarts();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Cancel button click
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteAllCarts(){
        DatabaseReference cartsReference = FirebaseDatabase.getInstance().getReference().child("carts");
        cartsReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Deletion successful
                        Toast.makeText(context, "All items removed from your cart.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(context, "Failed to remove items from your cart.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
