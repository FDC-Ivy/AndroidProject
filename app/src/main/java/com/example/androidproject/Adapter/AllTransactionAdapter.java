package com.example.androidproject.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Activity.AllTransactionActivity;
import com.example.androidproject.Enum.UserType;
import com.example.androidproject.Model.AddToCart;
import com.example.androidproject.Model.TransactionHistory;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.example.androidproject.ViewHolder.AllTransactionViewHolder;
import com.example.androidproject.ViewHolder.CartViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllTransactionAdapter extends RecyclerView.Adapter<AllTransactionViewHolder>{

    private Context context;
    List<TransactionHistory> items;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String tranID, prodID;

    public AllTransactionAdapter(Context context, List<TransactionHistory> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public AllTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AllTransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.all_transaction_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AllTransactionViewHolder holder, int position) {
        String transactionId = items.get(position).getTransactionId();
        tranID = transactionId;
        String transactionProdId = items.get(position).getTransactionProductId();
        prodID = transactionProdId;
        String transactionCartId = items.get(position).getTransactionCartId();
        String transactionUserId = items.get(position).getTransactionUserId();

        holder.transactionDate.setText(items.get(position).getTransactionTimeStamp());
        holder.transactionid.setText("Order #: "+items.get(position).getTransactionId());
        holder.transactionTotal.setText("Price: "+items.get(position).getTransactionTotal());
        holder.transactionProdName.setText("Items: \n"+items.get(position).getTransactionProductName());
        holder.transactionProdQty.setText(items.get(position).getTransactionCartQty());
        holder.transactionTotal.setText(items.get(position).getTransactionTotal());
        holder.transactionPrice.setText(items.get(position).getTransactionProductPrice());

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userRef = usersRef.child(SignInSingleton.getInstance().getAuthUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String usertype = dataSnapshot.child("usertype").getValue(String.class);

                    if(usertype.equals("CUSTOMER")){
                        holder.fullname.setVisibility(View.GONE);
                    }else{
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        DatabaseReference userRef = usersRef.child(transactionUserId);
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String fname = dataSnapshot.child("userfirstname").getValue(String.class);
                                    String lname = dataSnapshot.child("userlastname").getValue(String.class);
                                    holder.fullname.setText("Recipient: "+fname+" "+lname);
                                    holder.fullname.setVisibility(View.VISIBLE );
                                    return;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {}
                        });

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        if(items.get(position).isOnQueue() == true){
            holder.queue.setText("Queueing");
            holder.queue.setTextColor(Color.parseColor("#FFFA0000"));

            DatabaseReference userRef2 = usersRef.child(SignInSingleton.getInstance().getAuthUserId());
            userRef2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String usertype = dataSnapshot.child("usertype").getValue(String.class);

                        if(usertype.equals(String.valueOf(UserType.ADMIN))){
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adminDisplayTransactionDialog(transactionId);
                                }
                            });
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            });

        }else{
            holder.queue.setText("Done");
            holder.queue.setTextColor(Color.parseColor("#FF299533"));
        }
    }

    private AlertDialog dialog;
    public void adminDisplayTransactionDialog(String mtransactionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.transaction_scrollview, null);
        builder.setView(dialogView);
        Button servingBtn = dialogView.findViewById(R.id.serving_btn);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);

        servingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference transactionsRef = database.getReference("transactions");

                String transactionId = mtransactionId;
                boolean newOnQueueValue = false; // Update this with your desired value

                Query query = transactionsRef.orderByChild("transactionId").equalTo(transactionId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            if (key != null) {
                                DatabaseReference transactionRef = transactionsRef.child(key);
                                transactionRef.child("onQueue").setValue(newOnQueueValue)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Now Serving.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Update failed
                                                // Handle failure event
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle cancellation event
                    }
                });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
