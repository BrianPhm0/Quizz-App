package com.example.afinal.request1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.MainActivity;
import com.example.afinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ImageButton image_btn;
    EditText email, password;
    TextView forgot_pass;
    Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUi();
        initListener();
        backHome();
    }

    private void initListener() {
        email = findViewById(R.id.email_phone_login);
        password = findViewById(R.id.password_login);

        logIn = findViewById(R.id.login);

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailVal = email.getText().toString();
                String passVal = password.getText().toString();

                boolean check = validateInfo(emailVal, passVal);

                if (check) {
                    Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                    onClickLogIn();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry check information again", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    private boolean validateInfo(String emailVal, String passVal) {

        if(emailVal.length()==0){
            email.requestFocus();
            email.setError("Field can't be empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()){
            email.requestFocus();
            email.setError("Invalid email form");
            return false;

        } else if (passVal.length() == 0) {
            password.requestFocus();
            password.setError("Field can't be empty");
            return false;
        }
        else if (passVal.length() <= 8) {
            password.requestFocus();
            password.setError("Password needs to be more than 8 letters");
            return false;
        }
        return true;
    }
    private void initUi() {
        email = findViewById(R.id.email_phone_login);
        password = findViewById(R.id.password_login);
        logIn = findViewById(R.id.login);
        forgot_pass = findViewById(R.id.forgot_account);

    }
    private void backHome(){
        image_btn = findViewById(R.id.back_login);

        image_btn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void onClickLogIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailVal = email.getText().toString();
        String passVal = password.getText().toString();
        auth.signInWithEmailAndPassword(emailVal, passVal)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(getApplicationContext(),"Sorry check information again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}