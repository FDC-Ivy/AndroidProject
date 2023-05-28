package com.example.androidproject.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.androidproject.Adapter.ProductRecyclerViewAdapter;
import com.example.androidproject.Model.Products;
import com.example.androidproject.R;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private TextView mtxtInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        //mtxtInfo = findViewById(R.id.txtInfo);

        /*Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String value = bundle.getString("key_branch_id");
            mtxtInfo.setText("The id u clicked is: " +value);
        }*/

        RecyclerView recyclerView = findViewById(R.id.product_recycleview);

        List<Products> items = new ArrayList<Products>();
        items.add(new Products(1,"Product 1","john.wick@email.com","", R.drawable.pxfuel, ""));
        items.add(new Products(1,"Product 1","john.wick@email.com","", R.drawable.pxfuel, ""));
        items.add(new Products(1,"Product 1","john.wick@email.com","", R.drawable.pxfuel, ""));





        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProductRecyclerViewAdapter(getApplicationContext(),items));

    }
}