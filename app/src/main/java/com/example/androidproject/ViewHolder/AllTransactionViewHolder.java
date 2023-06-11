package com.example.androidproject.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;

public class AllTransactionViewHolder extends RecyclerView.ViewHolder {

    public TextView transactionDate, transactionTotal, transactionProdName, transactionProdQty, transactionPrice, transactionid, fullname, queue;
    public RelativeLayout transactionview;

    public AllTransactionViewHolder(@NonNull View itemView) {
        super(itemView);

        transactionid = itemView.findViewById(R.id.transaction_id);
        transactionDate = itemView.findViewById(R.id.transaction_date);
        transactionTotal = itemView.findViewById(R.id.transaction_total);
        transactionProdName = itemView.findViewById(R.id.transaction_product_name);
        transactionProdQty = itemView.findViewById(R.id.transaction_product_qty);
        transactionPrice = itemView.findViewById(R.id.transaction_product_price);
        fullname = itemView.findViewById(R.id.customer_name_txt);
        queue = itemView.findViewById(R.id.queue_txt);
        transactionview = itemView.findViewById(R.id.transactionview_button);
    }
}
