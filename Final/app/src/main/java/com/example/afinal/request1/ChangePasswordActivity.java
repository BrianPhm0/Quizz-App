package com.example.afinal.request1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.afinal.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText old_pass, new_pass, confirm_pass;
    Button save_change;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();
        initListener();

    }

    void init(){
        old_pass = findViewById(R.id.old_pass);
        new_pass = findViewById(R.id.new_pass);
        confirm_pass = findViewById(R.id.confirm_pass);
        back = findViewById(R.id.back_profile);
        save_change = findViewById(R.id.change);
    }
    void initListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backProfile();
            }
        });

        save_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass = old_pass.getText().toString().trim();
                String newPass = new_pass.getText().toString().trim();
                String confirmPass = confirm_pass.getText().toString().trim();
                boolean check = validateInfo(oldPass,newPass,confirmPass);
                if(check){
                    Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_SHORT).show();
                    updatePassword(oldPass, newPass);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Sorry check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void updatePassword(String oldPass, String newPass){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPass);

        user.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(newPass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext()
                                                ,"Password Updated..."
                                                , Toast.LENGTH_SHORT).show();
                                        clearFocus();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext()
                                                ,"Fail..."
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext()
                                ,"Fail..."
                                , Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private boolean validateInfo(String oldPass, String newPass, String confirmPass) {
        if(oldPass.length()==0){
            old_pass.requestFocus();
            old_pass.setError("Field can't be empty");
            return false;
        }
        else if (oldPass.length() <= 8) {
            old_pass.requestFocus();
            old_pass.setError("Password needs to be more than 8 letters");
            return false;

        }else if (newPass.length() == 0) {
            new_pass.requestFocus();
            new_pass.setError("Field can't be empty");
            return false;
        }
        else if (newPass.length() <= 8) {
            new_pass.requestFocus();
            new_pass.setError("Password needs to be more than 8 letters");
            return false;

        }else if(confirmPass.length() == 0) {
            confirm_pass.requestFocus();
            confirm_pass.setError("Field can't be empty");
            return false;
        }
        else if(confirmPass.length() <= 8) {
            confirm_pass.requestFocus();
            confirm_pass.setError("Password needs to be more than 8 letters");
            return false;
        } else if (!newPass.equals(confirmPass)) {
            confirm_pass.setError("Confirm-password doesn't match");
            return false;
        } else {
            return true;
        }
    }

    void backProfile(){
        super.onBackPressed();
    }

    void clearFocus(){
        old_pass.clearFocus();
        old_pass.setText("");
        new_pass.clearFocus();
        new_pass.setText("");
        confirm_pass.clearFocus();
        confirm_pass.setText("");
    }

}