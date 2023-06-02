package com.example.androidproject.Activity;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidproject.Adapter.ProductRecyclerViewAdapter;
import com.example.androidproject.Adapter.RecyclerviewAdapter;
import com.example.androidproject.Model.Branches;
import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private TextView mtxtInfo;
    private TextInputEditText prodname, prodprice;
    private EditText proddesc;
    private Button btnAddProduct;
    private DatabaseReference databaseReference;
    private ArrayList<Products> productlist;
    private RecyclerView prodRecyclerView;
    private ProductRecyclerViewAdapter adapter;
    private String branchid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        //product table getting instance
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("products");

        //get branch id
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            branchid = bundle.getString("key_branch_id");
        }

        prodRecyclerView = findViewById(R.id.product_recycleview);
        adapter = new ProductRecyclerViewAdapter(this, productlist);
        prodRecyclerView.setAdapter(adapter);

        //display array list
        displayAllProducts();


        btnAddProduct = findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog();
            }
        });
    }

    private void showFormDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
        builder.setTitle("Products");

        // Inflate the form layout XML file
        View formView = getLayoutInflater().inflate(R.layout.product_add, null);
        builder.setView(formView);


        prodname = formView.findViewById(R.id.txt_prod_name);
        prodprice = formView.findViewById(R.id.txt_prod_price);
        proddesc = formView.findViewById(R.id.txt_prod_desc);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the entered form data
                String pname = prodname.getText().toString();
                String pprice = prodprice.getText().toString();
                String pdesc = proddesc.getText().toString();

                // Create a data object or HashMap to hold the form data
                Map<String, Object> formData = new HashMap<>();
                formData.put("productname", pname);
                formData.put("productprice", pprice);
                formData.put("productdesc", pdesc);
                formData.put("productbranchid", branchid);

                // Save the form data to Firebase Realtime Database
                databaseReference.push().setValue(formData);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void displayAllProducts() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //arraylist
                productlist = new ArrayList<Products>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve child data
                    String childKey = childSnapshot.getKey();

                    String prod_name = childSnapshot.child("productname").getValue(String.class);
                    String prod_price = childSnapshot.child("productprice").getValue(String.class);
                    String prod_desc = childSnapshot.child("productdesc").getValue(String.class);
                    String prod_branchid = childSnapshot.child("productbranchid").getValue(String.class);

                    // Display child data
                    //Log.d("ChildData", "Key: " + childKey + ", Value: " + childValue);

                    //items.add(new Branches(childKey, branch_name, branch_address, R.drawable.pxfuel));

                    if(branchid.equals(prod_branchid)){
                        Products prods = new Products(childKey, prod_name, prod_price, prod_desc, R.drawable.pxfuel, branchid);
                        productlist.add(prods);

                    }


                }

                prodRecyclerView.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
                prodRecyclerView.setAdapter(new ProductRecyclerViewAdapter(ProductActivity.this, productlist));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }
}