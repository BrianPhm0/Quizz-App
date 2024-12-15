package com.example.afinal.request2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.objects.Folder;
import com.example.afinal.objects.Topic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class EditFolderActivity extends AppCompatActivity {

    EditText folderName, folderDes;
    TextView done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);
        init();
        initListener();
    }

    void init(){
        folderDes = findViewById(R.id.folder_description_edit);
        folderName = findViewById(R.id.folder_name_edit);
        done = findViewById(R.id.done_folder_edit);
    }
    void initListener(){
        Intent intent = getIntent();
        if(intent!= null) {
            Folder receivedFolder = (Folder) intent.getSerializableExtra("folder_object_edit");

            folderName.setText(receivedFolder.getFolderName());
            folderDes.setText(receivedFolder.getFolderDescription());

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String folderNameVal = folderName.getText().toString().trim();
                    String folderDescriptionVal = folderDes.getText().toString().trim();
                    boolean check = validateInfo(folderNameVal,folderDescriptionVal);
                    if(check){
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference topicCollection = db.collection("folder");
                        DocumentReference documentRef = topicCollection.document(receivedFolder.getFolderId());

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("folderName", folderNameVal);
                        updates.put("folderDescription", folderDescriptionVal);

                        documentRef.update(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(EditFolderActivity.this, "Edit Folder Successfully.",
                                                Toast.LENGTH_SHORT).show();

                                        documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                if (error != null) {
                                                    return;
                                                }

                                                if(value != null && value.exists()){
                                                    Folder folder1 = value.toObject(Folder.class);
                                                    Intent resultIntent = new Intent();
                                                    setResult(RESULT_OK, resultIntent);
                                                    resultIntent.putExtra("updated_folder", folder1);
                                                    finish();
                                                }
                                            }

                                        });
                                    }
                                });

                    }
                }
            });

        }
    }

    private boolean validateInfo(String folderNameVal, String folderDescriptionVal) {
        if(folderNameVal.length()==0){
            folderName.requestFocus();
            folderName.setError("Field can't be empty");
            return false;
        }
        else if (folderDescriptionVal.length() == 0) {
            folderDes.requestFocus();
            folderDes.setError("Field can't be empty");
            return false;
        }
        return true;
    }


}