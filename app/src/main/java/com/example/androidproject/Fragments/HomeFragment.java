package com.example.androidproject.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Adapter.ProductRecyclerViewAdapter;
import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//upload image imports



public class HomeFragment extends Fragment {
    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    Context context = getContext();
    private TextView deleteAllProd;
    private TextInputEditText prodname, prodprice;
    private EditText proddesc;
    private Button btnAddProduct;
    private ArrayList<Products> productlist;
    private RecyclerView prodRecyclerView;
    private ProductRecyclerViewAdapter adapter;
    private String branchid = "";

    //upload image variables


    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();
    }

    //This is a fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Saving to database
        databaseReference = firebaseDatabase.getReference("products");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        prodRecyclerView = view.findViewById(R.id.product_recycleview);
        adapter = new ProductRecyclerViewAdapter(getActivity().getApplicationContext(), productlist);
        prodRecyclerView.setAdapter(adapter);

        //display alert list
        displayData();

        btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing the alert dialogue for branch add
                showFormDialog();
            }
        });

        deleteAllProd = view.findViewById(R.id.delete_all_prod);
        deleteAllProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing the alert dialogue for branch add
                deleteAllPrompt();
            }
        });

        return view;
    }

    //for branch add here
    private void showFormDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

                DatabaseReference newChildRef = databaseReference.push();
                String childKey = newChildRef.getKey();
                // Create a data object or HashMap to hold the form data
                Map<String, Object> formData = new HashMap<>();
                formData.put("productid", childKey);
                formData.put("productname", pname);
                formData.put("productprice", pprice);
                formData.put("productdesc", pdesc);

                // Save the form data to Firebase Realtime Database
                newChildRef.setValue(formData);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //display in arraylist
    private void displayData() {

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

                    Products prods = new Products(childKey, prod_name, prod_price, prod_desc, R.drawable.pxfuel, branchid);
                    productlist.add(prods);

                }

                prodRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                prodRecyclerView.setAdapter(new ProductRecyclerViewAdapter(context, productlist));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    public void deleteAllProducts(){
        DatabaseReference prodReference = FirebaseDatabase.getInstance().getReference().child("products");
        prodReference.removeValue()
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
        builder.setTitle("Are you sure you want to delete all products?");
        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllProducts();
            }
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dialog Title");
        builder.setMessage("Dialog Message");
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}