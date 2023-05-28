package com.example.androidproject.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.androidproject.Activity.CustomerHome;
import com.example.androidproject.Activity.Products;
import com.example.androidproject.Adapter.RecyclerviewAdapter;
import com.example.androidproject.Model.Branches;
import com.example.androidproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Button mbtnadd;
    private EditText branchname;
    private EditText branchaddress;
    private ArrayList<Branches> branchlist;
    private RelativeLayout btnBranchInfo;
    Context context = getContext();
    private RecyclerviewAdapter adapter;

    //This is a fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Saving to database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("branches");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new RecyclerviewAdapter(context, branchlist);
        recyclerView.setAdapter(adapter);

        //display alert list
        displayData();

        mbtnadd = view.findViewById(R.id.btnadd);

        mbtnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing the alert dialogue for branch add
                showFormDialog();
            }
        });

        View view2 = inflater.inflate(R.layout.branch_view, container, false);

        /*
        RelativeLayout relativeLayout = view2.findViewById(R.id.relativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event
                // Start the desired activity using an Intent
                Intent intent = new Intent(context, Products.class);
                startActivity(intent);
            }
        });*/
        btnBranchInfo = view2.findViewById(R.id.branch_button);
        //btnBranchInfo.setAdapter(adapter);
        btnBranchInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(context, Products.class);
                startActivity(intent);*/
                Toast.makeText(context, "Button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //for branch add here
    private void showFormDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Branches");

        // Inflate the form layout XML file
        View formView = getLayoutInflater().inflate(R.layout.branches_add, null);
        builder.setView(formView);

        branchname = formView.findViewById(R.id.txt_branchname);
        branchaddress = formView.findViewById(R.id.txt_branchaddress);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the entered form data
                String bname = branchname.getText().toString();
                String baddress = branchaddress.getText().toString();

                // Create a data object or HashMap to hold the form data
                Map<String, Object> formData = new HashMap<>();
                formData.put("branchname", bname);
                formData.put("branchaddress", baddress);

                // Save the form data to Firebase Realtime Database
                databaseReference.push().setValue(formData);
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
                branchlist = new ArrayList<Branches>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve child data
                    String childKey = childSnapshot.getKey();

                    String branch_name = childSnapshot.child("branchname").getValue(String.class);
                    String branch_address = childSnapshot.child("branchaddress").getValue(String.class);

                    // Display child data
                    //Log.d("ChildData", "Key: " + childKey + ", Value: " + childValue);

                    //items.add(new Branches(childKey, branch_name, branch_address, R.drawable.pxfuel));
                    Branches branch = new Branches(childKey, branch_name, branch_address, R.drawable.pxfuel);
                    branchlist.add(branch);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new RecyclerviewAdapter(getActivity().getApplicationContext(), branchlist));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }
}