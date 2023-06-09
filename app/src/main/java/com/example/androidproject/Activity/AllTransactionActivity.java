package com.example.androidproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Adapter.AllTransactionAdapter;
import com.example.androidproject.Adapter.CartRecyclerviewAdapter;
import com.example.androidproject.Model.AddToCart;
import com.example.androidproject.Model.TransactionHistory;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AllTransactionActivity extends AppCompatActivity {

    private ArrayList<TransactionHistory> transactionlist;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Button deleteAllBtn, proceedPaymentBtn;
    private AllTransactionAdapter adapter;
    List<String> mergedProductNames = new ArrayList<>();
    String mtransactionId = "test";
    int processedTransactions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transaction);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("transactions");

        recyclerView = findViewById(R.id.recycler_view_all_transaction);

        displayTransaction();

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                displayTransaction();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void displayTransaction(){

        String products = "";
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference().child("transactions");
        transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()) {

                        //Getting other data
                        HashMap<String, List<TransactionHistory>> transactionMap = new HashMap<>();
                        List<TransactionHistory> transactionList = new ArrayList<>();

                        HashMap<String, List<String>> transactionProductMap = new HashMap<>();
                        int totalTransactions = (int) dataSnapshot.getChildrenCount();
                        HashSet<String> uniqueTransactionIds = new HashSet<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the data for each transaction
                            String transactionId = snapshot.child("transactionId").getValue(String.class);
                            String transactionProdId = snapshot.child("transactionProductId").getValue(String.class);

                            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products").child(transactionProdId);
                            productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String productName = dataSnapshot.child("productname").getValue(String.class);

                                        if (transactionProductMap.containsKey(transactionId)) {
                                            // Add the productname to the existing list associated with the transactionId
                                            List<String> productList = transactionProductMap.get(transactionId);
                                            productList.add(productName);
                                        } else {
                                            // Create a new list for the transactionId and add the productname
                                            List<String> productList = new ArrayList<>();
                                            productList.add(productName);
                                            transactionProductMap.put(transactionId, productList);
                                        }
                                    }

                                    // Increment the counter for processed transactions
                                    processedTransactions++;

                                    // Check if all transactions have been processed
                                    if (processedTransactions == totalTransactions) {
                                        // All transactions have been processed, perform further operations
                                        List<String> mergedProductNames = new ArrayList<>();
                                        for (Map.Entry<String, List<String>> entry : transactionProductMap.entrySet()) {
                                            String transactionId = entry.getKey();
                                            List<String> productList = entry.getValue();
                                            String mergedProductName = TextUtils.join(", ", productList);
                                            mergedProductNames.add(mergedProductName);

                                            //Toast.makeText(AllTransactionActivity.this, mergedProductName, Toast.LENGTH_SHORT).show();

                                            //Saving to database
                                            DatabaseReference mergedProductNamesRef = FirebaseDatabase.getInstance().getReference().child("mergedproductnames");
                                            mergedProductNamesRef.child(transactionId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (!dataSnapshot.exists()) {
                                                        // TransactionId does not exist, save the transactionId and mergedProductName
                                                        DatabaseReference newChildRef = mergedProductNamesRef.child(transactionId);
                                                        newChildRef.child("mergedtransactionid").setValue(transactionId);
                                                        newChildRef.child("mergedproductname").setValue(mergedProductName);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    // Handle any errors
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle any errors
                                }
                            });

                            uniqueTransactionIds.add(transactionId);
                        }

                        TransactionHistory transaction = null;
                        transactionlist = new ArrayList<TransactionHistory>();
                        HashSet<String> uniqueTransactionIds2 = new HashSet<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieve the data for each transaction
                            mtransactionId = snapshot.child("transactionId").getValue(String.class);
                            String idid = mtransactionId;
                            String tranUserID = snapshot.child("transactionUserId").getValue(String.class);
                            String transactionTotal = snapshot.child("transactionTotal").getValue(String.class);
                            double total = Double.parseDouble(transactionTotal);
                            String transactionDate = snapshot.child("transactionTimeStamp").getValue(String.class);

                            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                            String formattedTotalPrice = decimalFormat.format(total);


                            if (uniqueTransactionIds2.contains(mtransactionId)) {
                                continue; // Skip the duplicate transactionId
                            }

                            uniqueTransactionIds2.add(mtransactionId);

                            List<TransactionHistory> transactionList2;
                            if (transactionMap.containsKey(mtransactionId)) {
                                // Get the existing list associated with the transaction ID
                                transactionList2 = transactionMap.get(mtransactionId);
                            } else {
                                // Create a new list for the transaction ID
                                transactionList2 = new ArrayList<>();
                                transactionMap.put(mtransactionId, transactionList2);
                            }


                            DatabaseReference mergedProductNamesRef = FirebaseDatabase.getInstance().getReference().child("mergedproductnames");
                            mergedProductNamesRef.child(mtransactionId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot mergedProductNamesSnapshot) {
                                    if (mergedProductNamesSnapshot.exists()) {
                                        String mergedProductName = mergedProductNamesSnapshot.child("mergedproductname").getValue(String.class);

                                        if(tranUserID.equals(SignInSingleton.getInstance().getAuthUserId())) {
                                            TransactionHistory transactions = new TransactionHistory("Transaction #: " + idid, mergedProductName, "Total: P" + formattedTotalPrice, transactionDate);
                                            transactionlist.add(transactions);
                                            TextView no_label = findViewById(R.id.no_transaction_history);
                                            no_label.setVisibility(View.GONE);
                                        }else{
                                            TextView no_label = findViewById(R.id.no_transaction_history);
                                            no_label.setVisibility(View.VISIBLE);
                                            no_label.setText("No Transaction History");
                                        }

                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(AllTransactionActivity.this));
                                    recyclerView.setAdapter(new AllTransactionAdapter(getApplicationContext(), transactionlist));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle any errors
                                }
                            });
                        }

                        // Display the grouped data
                        for (String transactionId : transactionMap.keySet()) {
                            transactionList = transactionMap.get(transactionId);
                            System.out.println("Transaction ID: " + transactionId);
                            for (TransactionHistory transaction1 : transactionList) {
                                // Display the details of each transaction
                                System.out.println("Transaction Name: " + transaction1.getTransactionId());
                                // ... Display other fields as needed
                            }
                        }


                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}