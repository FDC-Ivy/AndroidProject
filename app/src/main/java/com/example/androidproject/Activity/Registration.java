package com.example.androidproject.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Auth.SignUp;
import com.example.androidproject.Enum.UserType;
import com.example.androidproject.Model.Customers;
import com.example.androidproject.R;
import com.google.android.material.textfield.TextInputEditText;

public class Registration extends AppCompatActivity {

    private TextView txtHyperlinkToLogin;
    private TextInputEditText txtFirstName, txtLastName, txtEmail, txtPassword;
    private Button btnRegister;

    Customers customerObj = new Customers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtHyperlinkToLogin = findViewById(R.id.txtHyperlinkToLogin);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegistration);

        //button of registration
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customerObj.setmCustomerFirstName(txtFirstName.getText().toString());
                customerObj.setmCustomerLastName(txtLastName.getText().toString());
                customerObj.setmCustomerEmail(txtEmail.getText().toString());
                customerObj.setmCustomerPassword(txtPassword.getText().toString());
                customerObj.setmCustomerUserType(UserType.CUSTOMER);

                if(txtFirstName.getText().toString() == ""){
                    Toast.makeText(Registration.this, "Please fill out your information.", Toast.LENGTH_SHORT).show();
                }else{
                    SignUp signUp = new SignUp();
                    signUp.createUserWithNameEmailAndPassword(customerObj.getmCustomerFirstName(), customerObj.getmCustomerLastName(), customerObj.getmCustomerEmail(), customerObj.getmCustomerUserType().toString(), customerObj.getmCustomerPassword(), Registration.this);
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
}