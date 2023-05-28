package com.example.androidproject.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.androidproject.R;

public class SellerHome extends AppCompatActivity {

    private TextView userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        userid = findViewById(R.id.textView2);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String data = bundle.getString("user_id");
            // Use the data in ReceiverActivity
            // ...
            userid.setText("this is seller home, your id is: "+data);
        }
    }
}