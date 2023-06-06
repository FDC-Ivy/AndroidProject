package com.example.androidproject.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Adapter.CartRecyclerviewAdapter;
import com.example.androidproject.Adapter.RecyclerviewAdapter;
import com.example.androidproject.Manifest;
import com.example.androidproject.Model.AddToCart;
import com.example.androidproject.Model.Branches;
import com.example.androidproject.Model.TransactionHistory;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Singleton;

public class CartFragment extends Fragment {

    private ArrayList<AddToCart> cartlist;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Context context;
    private Button deleteAllBtn, proceedPaymentBtn;
    private CartRecyclerviewAdapter adapter;
    private TextView totaltxt;
    private double mtotalPrice = 0.00;
    private int randomNum = 0;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        databaseReference = firebaseDatabase.getReference("carts");
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_cart);

        //validation();
        displayData();

        Random random = new Random();
        randomNum = random.nextInt(1000000000);

        deleteAllBtn = view.findViewById(R.id.delete_all_cart_btn);
        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllPrompt();
            }
        });

        totaltxt = view.findViewById(R.id.total_price);
        totaltxt.setText("Total: P0.00");

        proceedPaymentBtn = view.findViewById(R.id.payment_btn);
        proceedPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactionDialog();
            }
        });
        return view;
    }

    public void validation(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //arraylist
                cartlist = new ArrayList<AddToCart>();
                int childCount = (int) dataSnapshot.getChildrenCount();  // Track the number of child snapshots processed

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String cart_user_id = childSnapshot.child("cartuserid").getValue(String.class);
                    String id = SignInSingleton.getInstance().getAuthUserId();
                    if(cart_user_id.equals(SignInSingleton.getInstance().getAuthUserId())){
                        displayData();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void displayData() {
        String userId = SignInSingleton.getInstance().getAuthUserId();
        if (userId == null) {
            // Handle the case when the user ID is not available
            return;
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new cartlist
                cartlist = new ArrayList<AddToCart>();
                int childCount = (int) dataSnapshot.getChildrenCount();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String cart_user_id = childSnapshot.child("cartuserid").getValue(String.class);
                    String childKey = childSnapshot.getKey();
                    String cart_qty = childSnapshot.child("cartqty").getValue(String.class);
                    String cart_prod_id = childSnapshot.child("cartproductid").getValue(String.class);

                    if (cart_user_id.equals(userId)) {
                        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products").child(cart_prod_id);
                        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String prodid = dataSnapshot.child("productid").getValue(String.class);
                                    String productName = dataSnapshot.child("productname").getValue(String.class);
                                    String productPrice = dataSnapshot.child("productprice").getValue(String.class);

                                    AddToCart addToCart = new AddToCart(childKey, productName, productPrice, cart_qty, R.drawable.pxfuel, cart_prod_id);
                                    cartlist.add(addToCart);

                                    // Update the RecyclerView adapter
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));

                                    //mtotalPrice = mtotalPrice + (Double.parseDouble(cart_qty) * Double.parseDouble(productPrice));
                                    double totalPrice = Double.parseDouble(cart_qty) * Double.parseDouble(productPrice);
                                    mtotalPrice += totalPrice;
                                }

                                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                                String formattedTotalPrice = decimalFormat.format(mtotalPrice);
                                totaltxt.setText("Total: P"+String.valueOf(formattedTotalPrice));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                        mtotalPrice = 0.00;
                    }
                }

                if (cartlist.size() == childCount) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
                }

            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    public void deleteAllCarts() {
        DatabaseReference cartsReference = FirebaseDatabase.getInstance().getReference().child("carts");

        cartsReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Deletion successful
                        Toast.makeText(context, "All items are removed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(context, "Failed to remove items.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteAllPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to clear your cart?");
        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllCarts();
                totaltxt.setText("Total: P0.00");
                mtotalPrice = 0.00;
            }
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void transactionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Great!");
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formattedTotalPrice = decimalFormat.format(mtotalPrice);
        builder.setMessage("You are about to pay " +formattedTotalPrice+ " amount for this order. Would you like to proceed?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Step 1: Retrieve data from "carts" table
                DatabaseReference cartsRef = FirebaseDatabase.getInstance().getReference().child("carts");
                cartsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                            // Step 2: Create a new transaction object
                            String cartid = cartSnapshot.child("cartid").getValue(String.class);
                            String userId = cartSnapshot.child("cartuserid").getValue(String.class);
                            String productId = cartSnapshot.child("cartproductid").getValue(String.class);
                            String quantity = cartSnapshot.child("cartqty").getValue(String.class);

                            // Step 3: Store the transaction object in "transactions" table
                            DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference().child("transactions");
                            String transactionId = transactionsRef.push().getKey();

                            //getting the timestamp
                            long currentTimestamp = System.currentTimeMillis();
                            Date currentDate = new Date(currentTimestamp);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                            String formattedDate = dateFormat.format(currentDate);
                            double total = mtotalPrice;
                            TransactionHistory transaction = new TransactionHistory(String.valueOf(randomNum), cartid, userId, productId, String.valueOf(mtotalPrice), String.valueOf(formattedDate));
                            transactionsRef.child(transactionId).setValue(transaction);

                            // Step 4: Remove the cart item from "carts" table
                            deleteAllCarts();

                        }
                        totaltxt.setText("Total: P0.00");
                        mtotalPrice = 0.00;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        Random random = new Random();
        if(randomNum != 0) {
            randomNum = random.nextInt(1000000000);
        }
    }

    public void randomInteger() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000000);
    }
}