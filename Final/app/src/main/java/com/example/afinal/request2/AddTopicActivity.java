package com.example.afinal.request2;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;
import static java.util.Arrays.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.adapter.VocabAdapter;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.User;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request1.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTopicActivity extends AppCompatActivity {

    TextView done, publish;
    EditText topicName, term, definition, update_term, update_definition;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch visible;

    String documentId;

    FloatingActionButton add_button;

    RecyclerView recyclerViewVocab;
    VocabAdapter vocabAdapter;

    List<Vocabulary> mListVocab;

    boolean switchValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);
        init();
        initListener();
    }

    @SuppressLint("SetTextI18n")
    void init(){
        add_button = findViewById(R.id.fab_add_vocab);
        topicName = findViewById(R.id.title);
        done = findViewById(R.id.done);
        visible = findViewById(R.id.switchShowMyTopics);
        publish = findViewById(R.id.un_publish);

        if(visible.isChecked()){
            publish.setText("Publish");
        }
        else {
            publish.setText("Private");
        }

        recyclerViewVocab = findViewById(R.id.rcv_vocab);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewVocab.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerViewVocab.addItemDecoration(dividerItemDecoration);

        mListVocab = new ArrayList<>();
        vocabAdapter = new VocabAdapter(mListVocab, this, new VocabAdapter.IClickVocabListener() {
            @Override
            public void onClickVocabItem(Vocabulary vocabulary) {
                openUpdateVocab(Gravity.CENTER, vocabulary);
            }

            @Override
            public void onClickVocabDelete(Vocabulary vocabulary) {
                onClickDeleteData(vocabulary);
            }
        });

        recyclerViewVocab.setAdapter(vocabAdapter);
    }

    private void onClickDeleteData(Vocabulary vocabulary) {
        new AlertDialog.Builder(AddTopicActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure to delete this item")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference topicCollection = db.collection("topic");
                        DocumentReference documentRef = topicCollection.document(documentId);

                        CollectionReference vocabularyCollection = documentRef.collection("vocabulary");
                        vocabularyCollection.document(vocabulary.getVocabId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(AddTopicActivity.this, "Delete item successfully.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        getListVocab();

    }
    void initListener(){
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNewVocab(Gravity.CENTER);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");
        DocumentReference documentRef = topicCollection.document();
        Topic topic = new Topic("", documentRef.getId(), "", "","","", Arrays.<String>asList("", ""), true);
        documentId = documentRef.getId();
        documentRef.set(topic);
        visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // on below line we are checking
                // if switch is checked or not.
                if (isChecked) {
                    // on below line we are setting text
                    // if switch is checked.
                    switchValue = true;
                    publish.setText("Publish");
                } else {
                    // on below line we are setting text
                    // if switch is unchecked.
                    switchValue = false;
                    publish.setText("Private");
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleVal = topicName.getText().toString().trim();
                boolean check = validateTitle(titleVal);
                if(check){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference topicCollection = db.collection("topic");
                    DocumentReference documentRef = topicCollection.document(documentId);

                    final DocumentReference docUserRef = db.collection("user").document(user.getUid());
                    docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    User user1 = document.toObject(User.class);
                                    Topic topic = new Topic(titleVal, documentRef.getId(), user.getUid(), user1.getName(),user.getUid(),documentRef.getId(), Arrays.<String>asList("", ""), switchValue);
                                    documentRef.update(topic.toMap());
                                    Toast.makeText(AddTopicActivity.this, "Created Topic Successfully.",
                                            Toast.LENGTH_SHORT).show();

                                    finish();

                                } else {
                                    Log.d(TAG, "Current data: null");
                                }

                            } else {
                                Log.w(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                }
            }
        });

    }
    private void openAddNewVocab(int gravity) {

        final Dialog dialog = new Dialog(AddTopicActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        term = dialog.findViewById(R.id.enter_term);
        definition = dialog.findViewById(R.id.enter_definition);

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        Button cancel = dialog.findViewById(R.id.cancel_add_btn);
        Button add = dialog.findViewById(R.id.add_vocab_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String term_val = term.getText().toString().trim();
                String definition_val = definition.getText().toString().trim();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference topicCollection = db.collection("topic");
                DocumentReference documentRef = topicCollection.document(documentId);
                boolean check = validateInfo(term_val,definition_val);

                if(check){
                    CollectionReference vocabularyCollection = documentRef.collection("vocabulary");
                    DocumentReference documentVocabRef = vocabularyCollection.document();

                    Vocabulary vocabulary = new Vocabulary(documentRef.getId(), documentVocabRef.getId(), term_val, definition_val, "Terms left", false);

                    documentVocabRef.set(vocabulary)
                            .addOnSuccessListener(aVoid1 -> {
                                clearText();
                                Toast.makeText(AddTopicActivity.this, "Vocabulary created successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(AddTopicActivity.this, "Failed to create Vocabulary.", Toast.LENGTH_SHORT).show();

                            });

                }
                getListVocab();
            }
        });

        dialog.show();

    }

    private void openUpdateVocab(int gravity, Vocabulary vocabulary) {

        final Dialog dialog = new Dialog(AddTopicActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        update_term = dialog.findViewById(R.id.update_term);
        update_definition = dialog.findViewById(R.id.update_definition);

        update_term.setText(vocabulary.getVocabWord());
        update_definition.setText(vocabulary.getVocabMeaning());
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        Button cancel = dialog.findViewById(R.id.cancel_update_btn);
        Button add = dialog.findViewById(R.id.update_vocab_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String term_val = update_term.getText().toString().trim();
                String definition_val = update_definition.getText().toString().trim();

                vocabulary.setVocabWord(term_val);
                vocabulary.setVocabMeaning(definition_val);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference topicCollection = db.collection("topic");
                DocumentReference documentRef = topicCollection.document(documentId);
                CollectionReference vocabularyCollection = documentRef.collection("vocabulary");
                vocabularyCollection.document(vocabulary.getVocabId())
                        .update(vocabulary.toMap());

                dialog.dismiss();
                Toast.makeText(AddTopicActivity.this, "Update item successfully.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    void clearText(){
        term.setText("");
        term.clearFocus();
        definition.setText("");
        definition.clearFocus();
    }
    private boolean validateInfo(String termVal, String definitionVal) {
        if(termVal.length()==0){
            term.requestFocus();
            term.setError("Field can't be empty");
            return false;
        }
        else if (definitionVal.length() == 0) {
            definition.requestFocus();
            definition.setError("Field can't be empty");
            return false;
        }
        return true;
    }
    private boolean validateTitle(String titleVal) {
        if(titleVal.length()==0){
            topicName.requestFocus();
            topicName.setError("Field can't be empty");
            return false;
        }
        return true;
    }
    private void getListVocab() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("topic").document(documentId);

        docRef.collection("vocabulary")
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    return;
                }
                mListVocab.clear();
                assert value != null;
                for (QueryDocumentSnapshot doc : value) {
                    Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                    mListVocab.add(vocabulary);
                }
                vocabAdapter.notifyDataSetChanged();
            }

        });
    }

}