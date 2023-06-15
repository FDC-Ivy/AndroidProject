package com.example.androidproject.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Auth.SignIn;
import com.example.androidproject.LoadingBar.LoadingBar;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private TextView mtxtHyperlinkToRegistration;
    private TextInputEditText mtxtEmail, mtxtPassword;
    private Button mbtnLogin;
    public int loginid1;

    //to store user data and not ask to login again
    private SharedPreferences sharedPreferences;

    private ProgressBar loadingBar;
    //Firebase Database
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project1-4a559-default-rtdb.firebaseio.com/");;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingBar = findViewById(R.id.loadingBar);
        //final LoadingBar loadingBar = new LoadingBar(Login.this);

            mtxtHyperlinkToRegistration = findViewById(R.id.txtHyperlinkToRegistration);
            mtxtEmail = findViewById(R.id.txtEmail);
            mtxtPassword = findViewById(R.id.txtPassword);
            mbtnLogin = findViewById(R.id.btnLogin);

            sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve email and password
                String email = mtxtEmail.getText().toString().trim();
                String password = mtxtPassword.getText().toString().trim();

                // Perform validation checks
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if (email.isEmpty()) {
                        Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    } else if (!isValidEmail(email)) {
                        Toast.makeText(Login.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    } else if (password.isEmpty()) {
                        Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    } else {
                        showLoadingBar();
                        simulateLoading();

                        // Validation passed, proceed with sign-in
                        SignIn signIn = new SignIn();
                        signIn.signInWithEmailAndPassword(email, password, Login.this, sharedPreferences);
                    }
                }
            }
        });


        mtxtHyperlinkToRegistration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, Registration.class);
                    startActivity(intent);
                }
            });


    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
        return email.matches(emailRegex);
    }

    private void showLoadingBar() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingBar() {
        loadingBar.setVisibility(View.GONE);
    }

    private void simulateLoading() {
        // Simulate a time-consuming task (e.g., fetching data from a network)
        // Here, we use a Handler to delay the hiding of the loading bar after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide loading bar after the simulated loading is complete
                hideLoadingBar();
            }
        }, 3000); // Delay for 3 seconds
    }

    /*public void onBackPressed() {
        // Add your custom code here
        // For example, you can show a dialog asking for confirmation before allowing the back action

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform any actions or cleanup before exiting the activity
                // Call the superclass method to allow the back action
                CustomerHome.super.onBackPressed();

                *//*boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    // Navigate to the desired activity
                    Intent intent = new Intent(CustomerHome.this, Login.class);
                    startActivity(intent);
                    finish(); // Finish the current activity to remove it from the back stack
                } else {
                     // Perform the default back button behavior
                }*//*
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog and do nothing, allowing the user to stay in the activity
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
}