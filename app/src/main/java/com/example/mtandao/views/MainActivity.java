package com.example.mtandao.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mtandao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        checkUserStatus();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = auth.getCurrentUser();

        if (user != null){
            //user is logged in , stay here
        }else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }
}