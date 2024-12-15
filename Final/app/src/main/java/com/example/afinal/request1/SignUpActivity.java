package com.example.afinal.request1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {
    ImageButton image_btn;
    private EditText email, password, re_password;
    private Button signUp;

    public ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUi();
        backHome();
        initListener();
    }

    private void initListener() {
        email = findViewById(R.id.email_phone);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_enter_password);
        signUp = findViewById(R.id.sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailVal = email.getText().toString();
                String passVal = password.getText().toString();
                String re_passVal = re_password.getText().toString();

                boolean check = validateInfo(emailVal, passVal, re_passVal);

                if(check){
                    Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_SHORT).show();
                    onClickSignUp();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Sorry check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onClickSignUp() {
        String strEmail = email.getText().toString().trim();
        String strPass = password.getText().toString().trim();
        int atIndex = strEmail.indexOf('@');
        String username = strEmail.substring(0,atIndex);

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            CollectionReference usersCollection = db.collection("user");

                            DocumentReference newUserRef = usersCollection.document(user.getUid());

                            User userAcc = new User(username,strEmail,user.getUid(), Arrays.<String>asList("a", "b"));
                            newUserRef.set(userAcc);
                            Toast.makeText(SignUpActivity.this, "Created Account Successfully.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void initUi() {
        email = findViewById(R.id.email_phone);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_enter_password);
        signUp = findViewById(R.id.sign_up);

        progressBar = new ProgressBar(this);
    }
    private boolean validateInfo(String emailVal, String passVal, String rePassVal) {
        if(emailVal.length()==0){
            email.requestFocus();
            email.setError("Field can't be empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()){
            email.requestFocus();
            email.setError("Invalid email form");
            return false;

        }else if (passVal.length() == 0) {
            password.requestFocus();
            password.setError("Field can't be empty");
            return false;
        }
        else if (passVal.length() <= 8) {
            password.requestFocus();
            password.setError("Password needs to be more than 8 letters");
            return false;

        }else if(rePassVal.length() == 0) {
            re_password.requestFocus();
            re_password.setError("Field can't be empty");
            return false;
        }
        else if(rePassVal.length() <= 8) {
            re_password.requestFocus();
            re_password.setError("Password needs to be more than 8 letters");
            return false;
        } else if (!passVal.equals(rePassVal)) {
            re_password.setError("Re-password doesn't match");
            return false;
        } else {
            return true;
        }
    }
    private void backHome(){
        image_btn = findViewById(R.id.back_sign_up);

        image_btn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }


}