package com.example.androidproject.Fragments;

import static android.app.Activity.RESULT_OK;

import static com.google.common.io.Files.getFileExtension;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Adapter.ProductRecyclerViewAdapter;
import com.example.androidproject.Model.Products;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    private ImageView productImage;
    private FloatingActionButton fab;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();

        //image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            imageUri = data.getData();

                            Picasso.get().load(imageUri).into(productImage);
                            productImage.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "Image added", Toast.LENGTH_SHORT).show();
                        }
                        else {

                        }
                    }
                });
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
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                displayData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btnAddProduct = view.findViewById(R.id.btn_add_product);
        deleteAllProd = view.findViewById(R.id.delete_all_prod);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userRef = usersRef.child(SignInSingleton.getInstance().getAuthUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String usertype = dataSnapshot.child("usertype").getValue(String.class);

                    if(usertype.equals("CUSTOMER")){
                        btnAddProduct.setVisibility(View.GONE);
                        deleteAllProd.setVisibility(View.GONE);
                    }else{
                        btnAddProduct.setVisibility(View.VISIBLE);
                        deleteAllProd.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing the alert dialogue for branch add
                showFormDialog();
            }
        });

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
        productImage = formView.findViewById(R.id.product_image);
        fab = formView.findViewById(R.id.floatingActionButton);


        //image button
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });



        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Save to Storage
                saveImageToFirebase(imageUri);
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
                    String pImage = childSnapshot.child("productimage").getValue(String.class);





                    /*String imgPath = pImage;
                    FirebaseStorage mStorage = FirebaseStorage.getInstance();
                    StorageReference storageRef = mStorage.getReference().child(imgPath);

                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String pURL = String.valueOf(uri);

                            *//*Products prods = new Products(childKey, prod_name, prod_price, prod_desc, pURL,branchid);
                            productlist.add(prods);
                            adapter.notifyDataSetChanged();*//*
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });*/

                    /*prodRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    prodRecyclerView.setAdapter(new ProductRecyclerViewAdapter(context, productlist));*/


                    Products prods = new Products(childKey, prod_name, prod_price, prod_desc, pImage,branchid);
                    productlist.add(prods);

                    //Products prods = new Products(childKey, prod_name, prod_price, prod_desc, R.drawable.pxfuel, branchid);
                    /*Products prods = new Products(childKey, prod_name, prod_price, prod_desc,  "https://firebasestorage.googleapis.com/v0/b/project1-4a559.appspot.com/o/content%3A%2Fmedia%2Fexternal_primary%2Fimages%2Fmedia%2F1000001118?alt=media&token=40923164-4745-4412-b870-eb1206fb9689&_gl=1*h64lj8*_ga*MjA4NzUzNDYzNS4xNjg0OTMxMzcy*_ga_CW55HF8NVT*MTY4NjExNDM5OC40Ni4wLjE2ODYxMTQzOTguMC4wLjA.",branchid);
                    productlist.add(prods);*/

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

    //opening file when uploading image from gallery
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveImageToFirebase(Uri imageUri) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images");
        StorageReference imageRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(String.valueOf(imageUri)));
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // The image was uploaded successfully
                // Get the download URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Handle the success case, you can use the uri to retrieve the uploaded image URL
                        String imageUrl = uri.toString();
                        // Do something with the image URL

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
                        formData.put("productimage", imageUrl);

                        // Save the form data to Firebase Realtime Database
                        newChildRef.setValue(formData);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors that occurred while uploading the image
            }
        });
    }

    //template of alert dialog
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