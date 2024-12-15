package com.example.afinal.request1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.afinal.R;

public class HomeActivity extends AppCompatActivity {
    public Button Signup;
    public TextView Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Signup = findViewById(R.id.signup_main);
        Login = findViewById(R.id.login_main);

        Signup.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this,SignUpActivity.class);
            startActivity(intent);
        });

        Login.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
        });

    }
}