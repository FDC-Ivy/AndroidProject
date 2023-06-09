package com.example.androidproject.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidproject.Adapter.MyViewPagerAdapter;
import com.example.androidproject.LoadingBar.LoadingBar;
import com.example.androidproject.R;
import com.example.androidproject.Singleton.SignInSingleton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerHome extends AppCompatActivity {

    private TextView userid;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPagerAdapter myViewPagerAdapter;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        /*userid = findViewById(R.id.textView3);*/
        FirebaseUser user = auth.getCurrentUser();
        String login_user_id_auth = user.getUid();

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id_prefs", user.getUid());
        editor.putBoolean("isLoggedIn", true);
        editor.apply();



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String data = bundle.getString("user_id");
            // Use the data in ReceiverActivity
            // ...
            /*userid.setText("this is customer home, your id is: "+data);*/
        }

        //final LoadingBar loadingBar = new LoadingBar(CustomerHome.this);

        //if user is already logged in
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String loginuserid = sharedPreferences.getString("user_id_prefs","");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if(isLoggedIn){
            Bundle bundle2 = new Bundle();
            bundle2.putString("user_id", sharedPreferences.getString("user_id_prefs", ""));
            SignInSingleton.getInstance().setAuthUserId(loginuserid);

        }else{

            Intent intent = new Intent(CustomerHome.this, Login.class);
            startActivity(intent);
            finish();
            return;

        }

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager2.setAdapter(myViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }

    @Override
    public void onBackPressed() {
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

                /*boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    // Navigate to the desired activity
                    Intent intent = new Intent(CustomerHome.this, Login.class);
                    startActivity(intent);
                    finish(); // Finish the current activity to remove it from the back stack
                } else {
                     // Perform the default back button behavior
                }*/
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
    }

}