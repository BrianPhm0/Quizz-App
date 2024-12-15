package com.example.afinal.request1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.afinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_user = email.getText().toString().trim();
                boolean check = validateInfo(email_user);
                if(check){
                    OnClickForgotPass();
                }
                else{
                    Toast.makeText(getApplicationContext()
                            ,"Sorry check information again"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    void init() {
        email = findViewById(R.id.email);
        send = findViewById(R.id.click);
    }

    void OnClickForgotPass(){

        String email_user = email.getText().toString().trim();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email_user;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Email sent", Toast.LENGTH_SHORT).show();
                            backToLogin();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Fail to sent", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean validateInfo(String emailVal) {

        if (emailVal.length() == 0) {
            email.requestFocus();
            email.setError("Field can't be empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            email.requestFocus();
            email.setError("Invalid email form");
            return false;
        }
        return true;
    }

    void backToLogin(){
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}