package com.example.androidproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidproject.Auth.SignIn;
import com.example.androidproject.Interface.SignInCallback;
import com.example.androidproject.LoadingBar.LoadingBar;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextView mtxtHyperlinkToRegistration;
    private TextInputEditText mtxtEmail, mtxtPassword;
    private Button mbtnLogin;
    public int loginid1;

    //to store user data and not ask to login again
    private SharedPreferences sharedPreferences;

    //Firebase Database
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project1-4a559-default-rtdb.firebaseio.com/");;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final LoadingBar loadingBar = new LoadingBar(Login.this);

        //if user is already logged in
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String loginuserid = sharedPreferences.getString("user_id_prefs","");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if(isLoggedIn){
            Bundle bundle = new Bundle();
            bundle.putString("user_id", sharedPreferences.getString("user_id_prefs", ""));
            SignInSingleton.getInstance().setAuthUserId(loginuserid);
            Intent intent = new Intent(Login.this, CustomerHome.class);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        //

        mtxtHyperlinkToRegistration = findViewById(R.id.txtHyperlinkToRegistration);
        mtxtEmail = findViewById(R.id.txtEmail);
        mtxtPassword = findViewById(R.id.txtPassword);
        mbtnLogin = findViewById(R.id.btnLogin);

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.startLoadingDialog();
                SignIn signIn = new SignIn();
                signIn.signInWithEmailAndPassword(mtxtEmail.getText().toString(), mtxtPassword.getText().toString(), Login.this, sharedPreferences);
                finish();
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