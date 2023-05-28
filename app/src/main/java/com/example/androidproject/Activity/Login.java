package com.example.androidproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.Auth.SignIn;
import com.example.androidproject.Enum.UserType;
import com.example.androidproject.Model.Customers;
import com.example.androidproject.R;
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

    //Firebase Database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project1-4a559-default-rtdb.firebaseio.com/");;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mtxtHyperlinkToRegistration = findViewById(R.id.txtHyperlinkToRegistration);
        mtxtEmail = findViewById(R.id.txtEmail);
        mtxtPassword = findViewById(R.id.txtPassword);
        mbtnLogin = findViewById(R.id.btnLogin);

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignIn signIn = new SignIn();
                signIn.signInWithEmailAndPassword(mtxtEmail.getText().toString(), mtxtPassword.getText().toString(), Login.this);

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
}