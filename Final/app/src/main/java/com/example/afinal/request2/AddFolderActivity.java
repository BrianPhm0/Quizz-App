package com.example.afinal.request2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.objects.Folder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFolderActivity extends AppCompatActivity {

    EditText folder_name, folder_description;
    TextView done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_folder);

        initUi();
        initListener();
    }

    void initUi(){
        folder_name = findViewById(R.id.folder_name);
        folder_description = findViewById(R.id.folder_description);
        done = findViewById(R.id.done_folder);
    }

    void initListener(){
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName = folder_name.getText().toString().trim();
                String folderDescription = folder_description.getText().toString().trim();
                boolean check = validateInfo(folderName, folderDescription );
                if(check){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference folderCollection = db.collection("folder");
                    DocumentReference documentRef = folderCollection.document();
                    Folder folder = new Folder(folderName, documentRef.getId(), user.getUid(), folderDescription, user.getDisplayName());
                    documentRef.set(folder);
                    Toast.makeText(AddFolderActivity.this, "Create folder successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private boolean validateInfo(String folderName, String folderDescription) {
        if(folderName.length()==0){
            folder_name.requestFocus();
            folder_name.setError("Field can't be empty");
            return false;
        }
        else if (folderDescription.length() == 0) {
            folder_description.requestFocus();
            folder_description.setError("Field can't be empty");
            return false;
        }
        return true;
    }

}