package com.example.androidproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Adapter.AllTransactionAdapter;
import com.example.androidproject.Enum.UserType;
import com.example.androidproject.Model.TransactionHistory;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private AllTransactionAdapter adapter;
    String mtransactionId = "test";
    int processedTransactions = 0;
    Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    double mtotal = 0.00;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transaction);

        recyclerView = findViewById(R.id.recycler_view_all_transaction);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("transactions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    TextView noLabel = findViewById(R.id.no_transaction_history);
                    noLabel.setVisibility(View.VISIBLE);
                } else {
                    TextView noLabel = findViewById(R.id.no_transaction_history);
                    noLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userRef = usersRef.child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String usertype = dataSnapshot.child("usertype").getValue(String.class);

                    if(usertype.equals("CUSTOMER")){
                        displayTransaction();
                    }else{
                        adminDisplayTransaction();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                DatabaseReference userRef = usersRef.child(user.getUid());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String usertype = dataSnapshot.child("usertype").getValue(String.class);

                            if(usertype.equals("CUSTOMER")){
                                displayTransaction();
                            }else{
                                adminDisplayTransaction();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
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
        Query queryTran = transactionsRef.orderByChild("notQueue");
        queryTran.addValueEventListener(new ValueEventListener() {
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
                            String cartQty = snapshot.child("transactionCartQty").getValue(String.class);

                            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products").child(transactionProdId);
                            productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String productName = dataSnapshot.child("productname").getValue(String.class);

                                        if (transactionProductMap.containsKey(transactionId)) {
                                            // Add the productname to the existing list associated with the transactionId
                                            List<String> productList = transactionProductMap.get(transactionId);
                                            productList.add(productName+" ("+cartQty+")");
                                        } else {
                                            // Create a new list for the transactionId and add the productname
                                            List<String> productList = new ArrayList<>();
                                            productList.add(productName+" ("+cartQty+")");
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
                            String customerid = snapshot.child("transactionUserId").getValue(String.class);
                            boolean queue = snapshot.child("notQueue").getValue(boolean.class);

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

                                        FirebaseUser user = auth.getCurrentUser();
                                        if(tranUserID.equals(user.getUid())) {
                                            mtotal += total;
                                            String formattedTotalPrice2 = decimalFormat.format(mtotal);
                                            TextView grandtotal = findViewById(R.id.total_txt);
                                            grandtotal.setText("All Order Total: P"+String.valueOf(formattedTotalPrice2));
                                            TransactionHistory transactions = new TransactionHistory(idid, customerid, mergedProductName, "Total: P" + formattedTotalPrice, transactionDate, queue);
                                            transactionlist.add(transactions);
                                        }

                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(AllTransactionActivity.this));
                                    recyclerView.setAdapter(new AllTransactionAdapter(AllTransactionActivity.this, transactionlist));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle any errors
                                }
                            });
                        }

                        mtotal = 0.00;

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

    public void adminDisplayTransaction(){
        String products = "";
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference().child("transactions");
        Query queryTran = transactionsRef.orderByChild("notQueue");
        queryTran.addValueEventListener(new ValueEventListener() {
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
                        String cartQty = snapshot.child("transactionCartQty").getValue(String.class);

                        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products").child(transactionProdId);
                        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String productName = dataSnapshot.child("productname").getValue(String.class);

                                    if (transactionProductMap.containsKey(transactionId)) {
                                        // Add the productname to the existing list associated with the transactionId
                                        List<String> productList = transactionProductMap.get(transactionId);
                                        productList.add(productName+" ("+cartQty+")");
                                    } else {
                                        // Create a new list for the transactionId and add the productname
                                        List<String> productList = new ArrayList<>();
                                        productList.add(productName+" ("+cartQty+")");
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
                        String customerid = snapshot.child("transactionUserId").getValue(String.class);
                        boolean queue = snapshot.child("notQueue").getValue(boolean.class);

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

                                        mtotal += total;
                                        String formattedTotalPrice2 = decimalFormat.format(mtotal);
                                        TextView grandtotal = findViewById(R.id.total_txt);
                                        grandtotal.setText("All Order Total: P"+String.valueOf(formattedTotalPrice2));
                                        TransactionHistory transactions = new TransactionHistory(idid, customerid, mergedProductName, "Total: P" + formattedTotalPrice, transactionDate, queue);
                                        transactionlist.add(transactions);

                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(AllTransactionActivity.this));
                                recyclerView.setAdapter(new AllTransactionAdapter(AllTransactionActivity.this, transactionlist));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors
                            }
                        });
                    }

                    mtotal = 0.00;

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