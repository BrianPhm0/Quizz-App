package com.example.afinal.request2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.afinal.objects.Vocabulary;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditTopicActivity extends AppCompatActivity {

    TextView done,publish;

    EditText topicName, term, definition, update_term, update_definition;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch visible;

    FloatingActionButton add;

    RecyclerView recyclerView;

    VocabAdapter vocabAdapter;

    List<Vocabulary> mListVocab;

    boolean switchValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topic);
        initUi();
        initListener();
    }

    @SuppressLint("SetTextI18n")
    void initUi(){
        done = findViewById(R.id.done_edit);
        topicName = findViewById(R.id.title_topic);
        visible = findViewById(R.id.switchShowMyTopicsEdit);
        add = findViewById(R.id.fab_add_vocab_edit);
        publish = findViewById(R.id.un_publish_edit);

//        done = findViewById(R.id.done_edit);

        recyclerView = findViewById(R.id.rcv_vocab_edit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mListVocab = new ArrayList<>();
        vocabAdapter = new VocabAdapter(mListVocab, this, new VocabAdapter.IClickVocabListener() {
            Intent intent = getIntent();
            Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_edit");
            @Override
            public void onClickVocabItem(Vocabulary vocabulary) {
                openUpdateVocab(Gravity.CENTER, vocabulary, receivedTopic);
            }
            @Override
            public void onClickVocabDelete(Vocabulary vocabulary) {

                onClickDeleteData(vocabulary, receivedTopic);
            }
        });
        recyclerView.setAdapter(vocabAdapter);
    }

    private void onClickDeleteData(Vocabulary vocabulary, Topic topic) {

        new AlertDialog.Builder(EditTopicActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure to delete this item")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference topicCollection = db.collection("topic");
                        DocumentReference documentRef = topicCollection.document(topic.getTopicId());

                        CollectionReference vocabularyCollection = documentRef.collection("vocabulary");
                        vocabularyCollection.document(vocabulary.getVocabId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(EditTopicActivity.this, "Delete item successfully.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        getListVocab(topic);
    }

    private void openUpdateVocab(int gravity, Vocabulary vocabulary, Topic topic) {

        final Dialog dialog = new Dialog(EditTopicActivity.this);
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
                DocumentReference documentRef = topicCollection.document(topic.getTopicId());
                CollectionReference vocabularyCollection = documentRef.collection("vocabulary");
                vocabularyCollection.document(vocabulary.getVocabId())
                        .update(vocabulary.toMap());

                dialog.dismiss();
                Toast.makeText(EditTopicActivity.this, "Update item successfully.", Toast.LENGTH_SHORT).show();
                getListVocab(topic);
            }


        });
        dialog.show();
    }

    void initListener(){
        Intent intent = getIntent();
        if(intent!= null) {
            Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_edit");
            topicName.setText(receivedTopic.getTopicName());
            switchValue = receivedTopic.isVisible();
            getListVocab(receivedTopic);
            visible.setChecked(switchValue);
            updateText(switchValue);

            visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        switchValue = true;
                        publish.setText("Publish");
                    } else {
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
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference topicCollection = db.collection("topic");
                        DocumentReference documentRef = topicCollection.document(receivedTopic.getTopicId());

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("topicName", titleVal);
                        updates.put("visible", switchValue);

                        documentRef.update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(EditTopicActivity.this, "Edit Topic Successfully.",
                                            Toast.LENGTH_SHORT).show();

                                    documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                return;
                                            }

                                            if(value != null && value.exists()){
                                                Topic topic1 = value.toObject(Topic.class);
                                                Intent resultIntent = new Intent();
                                                setResult(RESULT_OK, resultIntent);
                                                resultIntent.putExtra("updated_topic", topic1);
                                                finish();
                                            }
                                        }

                                    });
                                }
                            });

                    }
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAddNewVocab(Gravity.CENTER, receivedTopic);
                }
            });
        }

    }

    private void getListVocab(Topic topic) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("topic").document(topic.getTopicId());

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

    @SuppressLint("SetTextI18n")
    void updateText(boolean switchValue){
        if(switchValue){
            publish.setText("Publish");
        }
        else {
            publish.setText("Private");
        }
    }
    private void openAddNewVocab(int gravity, Topic topic) {

        final Dialog dialog = new Dialog(EditTopicActivity.this);
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
                DocumentReference documentRef = topicCollection.document(topic.getTopicId());
                boolean check = validateInfo(term_val,definition_val);

                if(check){
                    CollectionReference vocabularyCollection = documentRef.collection("vocabulary");
                    DocumentReference documentVocabRef = vocabularyCollection.document();

                    Vocabulary vocabulary = new Vocabulary(documentRef.getId(), documentVocabRef.getId(), term_val, definition_val, "Terms left", false);

                    documentVocabRef.set(vocabulary)
                            .addOnSuccessListener(aVoid1 -> {
                                clearText();
                                Toast.makeText(EditTopicActivity.this, "Vocabulary created successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(EditTopicActivity.this, "Failed to create Vocabulary.", Toast.LENGTH_SHORT).show();

                            });

                }
                getListVocab(topic);
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

}