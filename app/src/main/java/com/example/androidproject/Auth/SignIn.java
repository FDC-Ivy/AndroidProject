package com.example.androidproject.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.androidproject.Activity.CustomerHome;
import com.example.androidproject.Activity.Login;
import com.example.androidproject.Activity.SellerHome;
import com.example.androidproject.DataManager.SharedPreferenceManager;
import com.example.androidproject.Enum.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn {
    // Get an instance of FirebaseAuth
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private String authid;
    private String mloginid;

    // Function to handle user login
    public void signInWithEmailAndPassword(String email, String password, Context context) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // User login successful
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();

                        // Retrieve user type
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        DatabaseReference userRef = usersRef.child(user.getUid());

                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String usertype = dataSnapshot.child("usertype").getValue(String.class);



                                    if(usertype.equals(UserType.CUSTOMER.toString())){

                                        Bundle bundle = new Bundle();
                                        bundle.putString("user_id", userId);
                                        // Create an Intent and put the Bundle as extra
                                        Intent intent = new Intent(context, CustomerHome.class);
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);

                                    }else if(usertype.equals(UserType.CASHIER.toString())){

                                        Bundle bundle = new Bundle();
                                        bundle.putString("user_id", userId);
                                        // Create an Intent and put the Bundle as extra
                                        Intent intent = new Intent(context, SellerHome.class);
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);
                                    }

                                    /*SharedPreferenceManager manager = SharedPreferenceManager.getInstance();
                                    manager.saveSharedPreference(this, "user_id", userId);*/
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle any errors that occur during data retrieval
                                // Display an error message or take appropriate action
                            }
                        });
                    }

                } else {
                    // Handle any errors that occur during user login
                    if (task.getException() instanceof FirebaseAuthException) {
                        FirebaseAuthException exception = (FirebaseAuthException) task.getException();
                        String errorCode = exception.getErrorCode();
                        String errorMessage = exception.getMessage();
                        // Handle specific error codes or display a generic error message
                    }
                }
            });
    }
}
