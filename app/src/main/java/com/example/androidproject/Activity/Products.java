package com.example.androidproject.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidproject.R;

public class Products extends AppCompatActivity {

    private TextView mtxtInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        mtxtInfo = findViewById(R.id.txtInfo);

        /*Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String value = bundle.getString("key_branch_id");
            mtxtInfo.setText("The id u clicked is: " +value);
        }*/


    }
}