package com.example.androidproject.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import com.example.androidproject.Auth.SignUp;
import com.example.androidproject.Enum.UserType;
import com.example.androidproject.Model.Customers;
import com.example.androidproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registration extends AppCompatActivity {

    private TextView txtHyperlinkToLogin;
    private TextInputEditText txtFirstName, txtLastName, txtEmail, txtPassword;
    private Button btnRegister;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    Customers customerObj = new Customers();
    private SharedPreferences sharedPreferences;
    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loadingBar = findViewById(R.id.loadingBar);

        txtHyperlinkToLogin = findViewById(R.id.txtHyperlinkToLogin);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegistration);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = txtFirstName.getText().toString().trim();
                String lastName = txtLastName.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(Registration.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                    } else if (!isValidEmail(email)) {
                        Toast.makeText(Registration.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    } else {
                        showLoadingBar();
                        simulateLoading();

                        customerObj.setmCustomerFirstName(firstName);
                        customerObj.setmCustomerLastName(lastName);
                        customerObj.setmCustomerEmail(email);
                        customerObj.setmCustomerPassword(password);
                        customerObj.setmCustomerUserType(UserType.CUSTOMER);

                        SignUp signUp = new SignUp();
                        signUp.createUserWithNameEmailAndPassword(
                                customerObj.getmCustomerFirstName(),
                                customerObj.getmCustomerLastName(),
                                customerObj.getmCustomerEmail(),
                                customerObj.getmCustomerUserType().toString(),
                                customerObj.getmCustomerPassword(),
                                Registration.this
                        );
                    }
                }


            }
        });


        //hyperlink to Login page/activity
        txtHyperlinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
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

}