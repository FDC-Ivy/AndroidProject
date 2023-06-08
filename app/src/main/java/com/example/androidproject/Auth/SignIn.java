package com.example.androidproject.Auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidproject.Activity.CustomerHome;
import com.example.androidproject.Activity.Login;
import com.example.androidproject.Activity.SellerHome;
import com.example.androidproject.DataManager.SharedPreferenceManager;
import com.example.androidproject.Enum.UserType;
import com.example.androidproject.Interface.SignInCallback;
import com.example.androidproject.Singleton.SignInSingleton;
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
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String authid;
    private String mloginid;
    private SharedPreferences sharedPreferences;
    // Function to handle user login
    public void signInWithEmailAndPassword(String email, String password, Context context, SharedPreferences sharedPreferences) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // User login successful
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        //String userId = user.getUid();
                        SignInSingleton.getInstance().setAuthUserId(user.getUid());

                        // Retrieve user type
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        DatabaseReference userRef = usersRef.child(user.getUid());

                        //Storing to Shared Preferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_id_prefs", user.getUid());
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();


                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String usertype = dataSnapshot.child("usertype").getValue(String.class);


                                    Bundle bundle = new Bundle();
                                    bundle.putString("user_id", SignInSingleton.getInstance().getAuthUserId());
                                    // Create an Intent and put the Bundle as extra
                                    Intent intent = new Intent(context, CustomerHome.class);
                                    intent.putExtras(bundle);
                                    context.startActivity(intent);


                                    /*if(usertype.equals(UserType.CUSTOMER.toString())){

                                        Bundle bundle = new Bundle();
                                        bundle.putString("user_id", SignInSingleton.getInstance().getAuthUserId());
                                        // Create an Intent and put the Bundle as extra
                                        Intent intent = new Intent(context, CustomerHome.class);
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);


                                    }else if(usertype.equals(UserType.CASHIER.toString())){

                                        Bundle bundle = new Bundle();
                                        bundle.putString("user_id", SignInSingleton.getInstance().getAuthUserId());
                                        // Create an Intent and put the Bundle as extra
                                        Intent intent = new Intent(context, SellerHome.class);
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);
                                    }*/

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
                        Toast.makeText(context, "Login failed, please double check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

}
