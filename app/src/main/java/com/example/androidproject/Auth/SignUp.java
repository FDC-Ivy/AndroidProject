package com.example.androidproject.Auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.androidproject.Activity.CustomerHome;
import com.example.androidproject.Activity.Registration;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp {
    // Get an instance of FirebaseAuth
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private SharedPreferences sharedPreferences;

    // Function to handle user registration
    public void createUserWithNameEmailAndPassword(String fname, String lname, String email, String usertype, String password, Context context) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User creation successful
                        FirebaseUser user = auth.getCurrentUser();
                        //SignInSingleton.getInstance().setAuthUserId(user.getUid());
                        if (user != null) {
                            sharedPreferences = ((Activity) context).getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_id_prefs", user.getUid());
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            String userId = user.getUid();
                            saveUserNameToDatabase(userId, fname, lname, email, usertype);

                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", userId);
                            // Create an Intent and put the Bundle as extra
                            Intent intent = new Intent(context, CustomerHome.class);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    } else {
                        // Handle any errors that occur during user creation
                        if (task.getException() instanceof FirebaseAuthException) {
                            FirebaseAuthException exception = (FirebaseAuthException) task.getException();
                            String errorCode = exception.getErrorCode();
                            String errorMessage = exception.getMessage();
                            // Handle specific error codes or display a generic error message
                            Toast.makeText(context, "Email exists.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    // Function to save user's name to the database
    private void saveUserNameToDatabase(String userid, String fname, String lname, String email, String usertype) {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Create a new child node under "customers" with the user's ID as the key
        DatabaseReference userRef = usersRef.child(userid);

        // Save the user's name as a value under the customers node
        userRef.child("userid").setValue(userid)
                .addOnSuccessListener(aVoid -> {
                    // User name saved successfully
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during data saving
                    // Display an error message or take appropriate action
                });

        userRef.child("userfirstname").setValue(fname)
                .addOnSuccessListener(aVoid -> {
                    // User name saved successfully
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during data saving
                    // Display an error message or take appropriate action
                });

        userRef.child("userlastname").setValue(lname)
                .addOnSuccessListener(aVoid -> {
                    // User name saved successfully
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during data saving
                    // Display an error message or take appropriate action
                });

        userRef.child("useremail").setValue(email)
                .addOnSuccessListener(aVoid -> {
                    // User name saved successfully
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during data saving
                    // Display an error message or take appropriate action
                });

        userRef.child("usertype").setValue(usertype)
                .addOnSuccessListener(aVoid -> {
                    // User name saved successfully
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during data saving
                    // Display an error message or take appropriate action
                });

        userRef.child("userimage").setValue("")
                .addOnSuccessListener(aVoid -> {
                    // User name saved successfully
                    // You can perform additional actions here if needed
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur during data saving
                    // Display an error message or take appropriate action
                });
    }
}
