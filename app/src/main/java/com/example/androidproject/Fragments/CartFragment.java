package com.example.androidproject.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Adapter.CartRecyclerviewAdapter;
import com.example.androidproject.Model.AddToCart;
import com.example.androidproject.Model.TransactionHistory;
import com.example.androidproject.Paypal.Config;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;



import org.json.JSONException;

import java.math.BigDecimal;

/*
public class CartFragment extends Fragment {

    private ArrayList<AddToCart> cartlist;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Context context;
    private TextView deleteAllBtn, proceedPaymentBtn, noCartLbl;
    private CartRecyclerviewAdapter adapter;
    private TextView totaltxt;
    private double mtotalPrice = 0.00;
    private int randomNum = 0;
    String amount = "";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    //Paypal SDK
    public static final int PAYPAL_REQUEST_CODE = 123;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK) //Sandbox for testing
            .clientId(Config.PAYPAL_CLIENT_ID);

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        databaseReference = firebaseDatabase.getReference("carts");
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        FirebaseApp.initializeApp(context);

        recyclerView = view.findViewById(R.id.recycler_view_cart);
        noCartLbl = view.findViewById(R.id.no_cart_label);

        //Start Paypal Service
        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        context.startService(intent);

        //validation();
        displayData();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

    */
/*private void displayData() {

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
                                    String pImage = dataSnapshot.child("productimage").getValue(String.class);

                                    AddToCart addToCart = new AddToCart(childKey, productName, productPrice, cart_qty, pImage, cart_prod_id);
                                    cartlist.add(addToCart);

                                    // Update the RecyclerView adapter
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));

                                    //mtotalPrice = mtotalPrice + (Double.parseDouble(cart_qty) * Double.parseDouble(productPrice));
                                    double totalPrice = Double.parseDouble(cart_qty) * Double.parseDouble(productPrice);
                                    mtotalPrice += totalPrice;

                                    deleteAllBtn.setVisibility(View.VISIBLE);
                                    //noCartLbl.setVisibility(View.GONE);
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

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
                }

                *//*
*/
/*if (cartlist.size() == childCount) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
                }*//*
*/
/*

                if (!dataSnapshot.exists()){
                    deleteAllBtn.setVisibility(View.GONE);
                    noCartLbl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

    }*//*


    private void displayData() {
        String userId = SignInSingleton.getInstance().getAuthUserId();
        if (userId == null) {
            // Handle the case when the user ID is not available
            return;
        }

        DatabaseReference cartsReference = FirebaseDatabase.getInstance().getReference().child("carts");
        Query cartUserQuery = cartsReference.orderByChild("cartuserid").equalTo(userId);
        cartUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new cartlist
                cartlist = new ArrayList<>();

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
                                    String pImage = dataSnapshot.child("productimage").getValue(String.class);

                                    AddToCart addToCart = new AddToCart(childKey, productName, productPrice, cart_qty, pImage, cart_prod_id);
                                    cartlist.add(addToCart);

                                    */
/*recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));*//*


                                    double totalPrice = Double.parseDouble(cart_qty) * Double.parseDouble(productPrice);
                                    mtotalPrice += totalPrice;
                                }

                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));

                                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                                String formattedTotalPrice = decimalFormat.format(mtotalPrice);
                                totaltxt.setText("Total: P" + formattedTotalPrice);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                    }
                }

                if (!dataSnapshot.exists()){
                    deleteAllBtn.setVisibility(View.GONE);
                    noCartLbl.setVisibility(View.VISIBLE);

                    cartlist.clear();
                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
                    mtotalPrice = 0.00;
                }else{
                    deleteAllBtn.setVisibility(View.VISIBLE);
                    noCartLbl.setVisibility(View.GONE);

                    */
/*recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));*//*

                }

                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        mtotalPrice = 0.00;
    }


    public void deleteAllCarts() {
        DatabaseReference cartsReference = FirebaseDatabase.getInstance().getReference().child("carts");

        Query cartUserQuery = cartsReference.orderByChild("cartuserid").equalTo(SignInSingleton.getInstance().getAuthUserId());
        cartUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
                //displayData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                // Empty the cartlist
                cartlist.clear();
                // Update the RecyclerView adapter
                recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
                totaltxt.setText("Total: P0.00");
                mtotalPrice = 0.00;
                Toast.makeText(context, "All items are removed.", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("You are about to pay P" +formattedTotalPrice+ " amount for this order. Would you like to proceed?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processPayment();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void processPayment(){
        amount = String.valueOf(mtotalPrice);
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "PHP",
                "Payment for Jollibee Order", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        //startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

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

                                    if(userId.equals(SignInSingleton.getInstance().getAuthUserId())){
                                        TransactionHistory transaction = new TransactionHistory(String.valueOf(randomNum), cartid, userId, productId, quantity, String.valueOf(mtotalPrice), String.valueOf(formattedDate), false);
                                        transactionsRef.child(transactionId).setValue(transaction);
                                    }

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

                        Toast.makeText(context, "Payment is successful. Please check your order history.", Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("error message", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                Log.i("paymentExample", "The user canceled.");
            }
        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show();
        }

        Random random = new Random();
        if(randomNum != 0) {
            randomNum = random.nextInt(1000000000);
        }

    }

}*/

public class CartFragment extends Fragment implements CartRecyclerviewAdapter.OnDataSetChangedListener {

    private ArrayList<AddToCart> cartlist;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Context context;
    private TextView deleteAllBtn, proceedPaymentBtn, noCartLbl;
    private CartRecyclerviewAdapter adapter;
    private TextView totaltxt;
    private double mtotalPrice = 0.00;
    private int randomNum = 0;
    String amount = "";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    //Paypal SDK
    public static final int PAYPAL_REQUEST_CODE = 123;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK) //Sandbox for testing
            .clientId(Config.PAYPAL_CLIENT_ID);

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        databaseReference = firebaseDatabase.getReference("carts");
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_cart);
        noCartLbl = view.findViewById(R.id.no_cart_label);

        //Start Paypal Service
        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        context.startService(intent);

        //validation();
        displayData();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

    /*private void displayData() {

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
                                    String pImage = dataSnapshot.child("productimage").getValue(String.class);

                                    AddToCart addToCart = new AddToCart(childKey, productName, productPrice, cart_qty, pImage, cart_prod_id);
                                    cartlist.add(addToCart);

                                    // Update the RecyclerView adapter
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));

                                    //mtotalPrice = mtotalPrice + (Double.parseDouble(cart_qty) * Double.parseDouble(productPrice));
                                    double totalPrice = Double.parseDouble(cart_qty) * Double.parseDouble(productPrice);
                                    mtotalPrice += totalPrice;

                                    deleteAllBtn.setVisibility(View.VISIBLE);
                                    //noCartLbl.setVisibility(View.GONE);
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

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
                }

                *//*if (cartlist.size() == childCount) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new CartRecyclerviewAdapter(context, cartlist));
                }//

                if (!dataSnapshot.exists()){
                    deleteAllBtn.setVisibility(View.GONE);
                    noCartLbl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

    }*/

    private void displayData() {
        String userId = SignInSingleton.getInstance().getAuthUserId();
        if (userId == null) {
            // Handle the case when the user ID is not available
            return;
        }

        DatabaseReference cartsReference = FirebaseDatabase.getInstance().getReference().child("carts");

        Query cartUserQuery = cartsReference.orderByChild("cartuserid").equalTo(userId);
        cartUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create a new cartlist
                cartlist = new ArrayList<>();

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
                                    String pImage = dataSnapshot.child("productimage").getValue(String.class);

                                    AddToCart addToCart = new AddToCart(childKey, productName, productPrice, cart_qty, pImage, cart_prod_id);
                                    cartlist.add(addToCart);

                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    //CartRecyclerviewAdapter adapter = new CartRecyclerviewAdapter(context, cartlist);
                                    adapter = new CartRecyclerviewAdapter(context, cartlist);
                                    adapter.setOnDataSetChangedListener(CartFragment.this);
                                    recyclerView.setAdapter(adapter);


                                    double totalPrice = Double.parseDouble(cart_qty) * Double.parseDouble(productPrice);
                                    mtotalPrice += totalPrice;
                                }else{
                                    // Empty the cartlist
                                    cartlist.clear();
                                    //CartRecyclerviewAdapter adapter = new CartRecyclerviewAdapter(context, cartlist);
                                    adapter = new CartRecyclerviewAdapter(context, cartlist);
                                    adapter.setOnDataSetChangedListener(CartFragment.this);
                                    // Update the RecyclerView adapter
                                    recyclerView.setAdapter(adapter);
                                    totaltxt.setText("Total: P0.00");
                                    mtotalPrice = 0.00;
                                }

                                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                                String formattedTotalPrice = decimalFormat.format(mtotalPrice);
                                totaltxt.setText("Total: P" + formattedTotalPrice);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                    }
                }

                if (!dataSnapshot.exists()){
                    deleteAllBtn.setVisibility(View.GONE);
                    noCartLbl.setVisibility(View.VISIBLE);

                    cartlist.clear();
                    adapter = new CartRecyclerviewAdapter(context, cartlist);
                    mtotalPrice = 0.00;
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                    String formattedTotalPrice = decimalFormat.format(mtotalPrice);
                    totaltxt.setText("Total: P" + formattedTotalPrice);
                }else{
                    deleteAllBtn.setVisibility(View.VISIBLE);
                    noCartLbl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        mtotalPrice = 0.00;
    }


    public void deleteAllCarts() {
        DatabaseReference cartsReference = FirebaseDatabase.getInstance().getReference().child("carts");

        Query cartUserQuery = cartsReference.orderByChild("cartuserid").equalTo(SignInSingleton.getInstance().getAuthUserId());
        cartUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
                //displayData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                cartlist.clear();
                adapter = new CartRecyclerviewAdapter(context, cartlist);
                mtotalPrice = 0.00;
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                String formattedTotalPrice = decimalFormat.format(mtotalPrice);
                totaltxt.setText("Total: P" + formattedTotalPrice);
                Toast.makeText(context, "All items are removed.", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("You are about to pay P" +formattedTotalPrice+ " amount for this order. Would you like to proceed?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processPayment();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void processPayment(){
        amount = String.valueOf(mtotalPrice);
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "PHP",
                "Payment for Jollibee Order", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        //startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

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

                                    if(userId.equals(SignInSingleton.getInstance().getAuthUserId())){
                                        TransactionHistory transaction = new TransactionHistory(String.valueOf(randomNum), cartid, userId, productId, quantity, String.valueOf(mtotalPrice), String.valueOf(formattedDate), false);
                                        transactionsRef.child(transactionId).setValue(transaction);
                                    }

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

                        Toast.makeText(context, "Payment is successful. Please check your order history.", Toast.LENGTH_SHORT).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("error message", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                Log.i("paymentExample", "The user canceled.");
            }
        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(context, "Invalid", Toast.LENGTH_SHORT).show();
        }

        Random random = new Random();
        if(randomNum != 0) {
            randomNum = random.nextInt(1000000000);
        }

    }

    @Override
    public void onDataSetChanged() {
        displayData();
    }
}