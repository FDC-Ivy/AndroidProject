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
import android.widget.TextView;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
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

        holder.productName.setText(items.get(position).getmProductName());
        holder.productDesc.setText(items.get(position).getmProductDescription());
        holder.productPrice.setText("Price: P"+items.get(position).getmProductPrice());
        //holder.productImg.setImageResource(items.get(position).getmProductImage());

        Products product = items.get(position);
        holder.bind(product);

        final Products data = items.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click event
                showAddToCartDialog(context, productId);
            }
        });

        holder.editProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Clicked: " , Toast.LENGTH_SHORT).show();
                productEditDialog(productId);
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

    public void showAddToCartDialog(Context context, String productId) {

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

        //Button add to cart
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = quantityEditText.getText().toString();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean isProductExists = false;

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String cartProductId = childSnapshot.child("cartproductid").getValue(String.class);
                            String cartProductQty = childSnapshot.child("cartqty").getValue(String.class);
                            String cartUserid = childSnapshot.child("cartuserid").getValue(String.class);
                            String childKeyCart = childSnapshot.getKey();

                            int newQty = Integer.parseInt(cartProductQty) + Integer.parseInt(quantity);

                            if (cartProductId != null && cartProductId.equals(productId) && cartUserid.equals(SignInSingleton.getInstance().getAuthUserId())) {
                                isProductExists = true;

                                Map<String, Object> formData = new HashMap<>();
                                formData.put("cartid", childKeyCart);
                                formData.put("cartproductid", productId);
                                formData.put("cartuserid", SignInSingleton.getInstance().getAuthUserId());
                                formData.put("cartqty", String.valueOf(newQty));

                                // Update the cart entry in the "carts" table
                                DatabaseReference cartEntryReference = databaseReference.child(childKeyCart);
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

                        if (!isProductExists) {
                            // Create a new cart entry
                            DatabaseReference newChildRef = databaseReference.push();
                            String newCartChildKey = newChildRef.getKey();

                            Map<String, Object> formData = new HashMap<>();
                            formData.put("cartid", newCartChildKey);
                            formData.put("cartproductid", productId);
                            formData.put("cartuserid", SignInSingleton.getInstance().getAuthUserId());
                            formData.put("cartqty", quantity);

                            // Save the new cart entry in the "carts" table
                            DatabaseReference newCartEntryReference = databaseReference.push();
                            newCartEntryReference.setValue(formData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // New cart entry creation successful
                                        alertDialog.dismiss();
                                        Toast.makeText(context, "Successfully added to cart.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle creation failure
                                        alertDialog.dismiss();
                                        Toast.makeText(context, "Failed to add to cart.", Toast.LENGTH_SHORT).show();
                                    }
                                });
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

    private AlertDialog alertDialog;
    //update product
    private void productEditDialog(String prodid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the form layout XML file
        View formView = LayoutInflater.from(context).inflate(R.layout.product_edit, null);
        builder.setView(formView);

        TextView deleteProd;
        EditText prodname, prodprice, proddesc;
        deleteProd = formView.findViewById(R.id.delete_prod);
        prodname = formView.findViewById(R.id.product_edit_name);
        prodprice = formView.findViewById(R.id.product_edit_price);
        proddesc = formView.findViewById(R.id.product_edit_desc);

        //deleting a product
        deleteProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("products").child(prodid);
                databaseReference.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Deletion successful
                            Toast.makeText(context, "Successfully removed.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                            Toast.makeText(context, "Failed to remove.", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        //updating happens here
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("products");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isProductExists = false;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String prod_name = childSnapshot.child("productname").getValue(String.class);
                    String prod_desc = childSnapshot.child("productdesc").getValue(String.class);
                    String prod_price = childSnapshot.child("productprice").getValue(String.class);
                    String prod_id = childSnapshot.getKey();

                    if(prod_id == prodid){
                        prodname.setText(prod_name);
                        prodprice.setText(prod_price);
                        proddesc.setText(prod_desc);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle any errors
            }
        });

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the entered form data
                String pname = prodname.getText().toString();
                String pprice = prodprice.getText().toString();
                String pdesc = proddesc.getText().toString();

                if(pname.equals("") || pprice.equals("") || pdesc.equals("")){
                    Toast.makeText(context, "Please fill product information correctly. Update failed.", Toast.LENGTH_SHORT).show();
                }else {

                    // Create a data object or HashMap to hold the updated form data
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("productname", pname);
                    updateData.put("productprice", pprice);
                    updateData.put("productdesc", pdesc);

                    // Update the form data in Firebase Realtime Database
                    databaseReference.child(prodid).updateChildren(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data update successful
                                    Toast.makeText(context, "Product updated successfully.", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle update failure
                                    Toast.makeText(context, "Failed to update product.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        builder.setNegativeButton("Cancel", null);

        alertDialog = builder.create();
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
