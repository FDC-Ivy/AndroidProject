package com.example.androidproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.Activity.AllTransactionActivity;
import com.example.androidproject.Model.AddToCart;
import com.example.androidproject.Model.TransactionHistory;
import com.example.androidproject.R;
import com.example.androidproject.ViewHolder.AllTransactionViewHolder;
import com.example.androidproject.ViewHolder.CartViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AllTransactionAdapter extends RecyclerView.Adapter<AllTransactionViewHolder>{

    Context context;
    List<TransactionHistory> items;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

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
        String transactionProdId = items.get(position).getTransactionProductId();
        String transactionCartId = items.get(position).getTransactionCartId();

        holder.transactionDate.setText(items.get(position).getTransactionTimeStamp());
        holder.transactionid.setText(items.get(position).getTransactionId());
        holder.transactionTotal.setText("Price: "+items.get(position).getTransactionTotal());
        holder.transactionProdName.setText("Items: \n"+items.get(position).getTransactionProductName());
        holder.transactionProdQty.setText(items.get(position).getTransactionCartQty());
        holder.transactionTotal.setText(items.get(position).getTransactionTotal());
        holder.transactionPrice.setText(items.get(position).getTransactionProductPrice());
        //holder.transactionProdQty.setImageResource(items.get(position).getCartProductImage());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
