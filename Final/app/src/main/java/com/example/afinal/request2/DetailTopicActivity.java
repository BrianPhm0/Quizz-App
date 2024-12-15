package com.example.afinal.request2;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.adapter.DetailTopicAdapter;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request3.CardActivity;
import com.example.afinal.request3.QuizzesActivity;
import com.example.afinal.request3.TypeWordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DetailTopicActivity extends AppCompatActivity {


    ImageView back, more;

    Button flashCard, quizzes, type;

    TextView topicName, userName;

    RecyclerView vocabularyRecycler;

    DetailTopicAdapter detailTopicAdapter;

    List<Vocabulary> vocabularyList;

    private static final int PICK_CSV_FILE = 1;

    private ActivityResultLauncher<Intent> activityResultLauncher
            = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()== RESULT_OK){
                        Intent intent = o.getData();
                        Topic topic = (Topic) intent.getSerializableExtra("updated_topic");
                        topicName.setText(topic.getTopicName());

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_topic);
        initUi();
        initListener();
    }

    void setTopic(Topic receivedTopic){
        topicName.setText(receivedTopic.getTopicName());
        userName.setText(receivedTopic.getUserName());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        vocabularyList.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                            vocabularyList.add(vocabulary);
                        }
                        detailTopicAdapter.notifyDataSetChanged();
                    }
                });
    }

    void getList(){
        Intent intent = getIntent();
        if(intent!= null) {

            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                setTopic(receivedTopic);

            }
            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                setTopic(receivedTopic);
            }
        }
    }

    void initUi(){
        back = findViewById(R.id.back_detail_topic);
        more = findViewById(R.id.more_detail_topic);
        userName = findViewById(R.id.user_name_topic);
        topicName = findViewById(R.id.topic_name_detail);
        flashCard = findViewById(R.id.flash_card);
        quizzes = findViewById(R.id.quizzes);
        type = findViewById(R.id.type_word);


        Intent intent = getIntent();
        if(intent!= null){
            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                topicName.setText(receivedTopic.getTopicName());
                userName.setText(receivedTopic.getUserName());
            }

            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                topicName.setText(receivedTopic.getTopicName());
                userName.setText(receivedTopic.getUserName());
            }
        }

        vocabularyRecycler = findViewById(R.id.recycler_view_vocab);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailTopicActivity.this);
        vocabularyRecycler.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DetailTopicActivity.this, DividerItemDecoration.VERTICAL);
        vocabularyRecycler.addItemDecoration(dividerItemDecoration);

        vocabularyList = new ArrayList<>();

        detailTopicAdapter = new DetailTopicAdapter(vocabularyList, this, new DetailTopicAdapter.IClickVocabListener() {
            @Override
            public void onClickVocabSound(Vocabulary vocabulary) {

            }
            @Override
            public void onClickStar(Vocabulary vocabulary, boolean check) {
                onClickStarCheck(vocabulary, check);
            }
        });

        vocabularyRecycler.setAdapter(detailTopicAdapter);

    }

    void onClickStarCheck(Vocabulary vocabulary, boolean check){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(vocabulary.getTopicId())
                .collection("vocabulary");

        DocumentReference documentRef = topicCollection.document(vocabulary.getVocabId());
        documentRef.update("star", check);
        detailTopicAdapter.notifyDataSetChanged();
    }

    void initListener(){

        getList();

        back();

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        flashCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFlashCard();
            }
        });

        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToQuizzes();
            }
        });

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToType();
            }
        });

    }
    void back(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showBottomDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_vobab_detail);

        LinearLayout importLayout = dialog.findViewById(R.id.layoutImport);
        LinearLayout exportLayout = dialog.findViewById(R.id.layoutExport);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);
        LinearLayout starLayout = dialog.findViewById(R.id.layoutStudy);



        importLayout.setVisibility(View.GONE);
        exportLayout.setVisibility(View.GONE);
        editLayout.setVisibility(View.GONE);
        deleteLayout.setVisibility(View.GONE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        if(intent!= null){
            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                DocumentReference documentRef = topicCollection.document(receivedTopic.getTopicId());
                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Topic topic = document.toObject(Topic.class);

                                assert topic != null;
                                assert user != null;
                                if(topic.getUserId().equals(user.getUid())){
                                    importLayout.setVisibility(View.VISIBLE);
                                    exportLayout.setVisibility(View.VISIBLE);
                                    editLayout.setVisibility(View.VISIBLE);
                                    deleteLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }

            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                DocumentReference documentRef = topicCollection.document(receivedTopic.getTopicId());
                documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Topic topic = document.toObject(Topic.class);

                                assert topic != null;
                                assert user != null;
                                if(topic.getUserId().equals(user.getUid())){
                                    importLayout.setVisibility(View.VISIBLE);
                                    exportLayout.setVisibility(View.VISIBLE);
                                    editLayout.setVisibility(View.VISIBLE);
                                    deleteLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        }
        deleteLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                if(intent!= null){
                    if (intent.hasExtra("new_topic_object_home")) {
                        Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                        deleteItem(receivedTopic, dialog);
                    }

                    if (intent.hasExtra("topic_object")) {
                        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                        deleteItem(receivedTopic, dialog);
                    }
                }
            }

        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditTopic();
                dialog.dismiss();
            }
        });

        importLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importVocabulary();
                dialog.dismiss();
            }

        });

        exportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportVocabulary();
                dialog.dismiss();
            }
        });

        starLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStarList();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    void deleteItem(Topic receivedTopic, Dialog dialog){
        new AlertDialog.Builder(DetailTopicActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure to delete this item")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference topicCollection = db.collection("topic");
                        DocumentReference documentRef = topicCollection.document(receivedTopic.getTopicId());

                        documentRef
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DetailTopicActivity.this, "Delete Topic successfully.", Toast.LENGTH_SHORT).show();
                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();

    }
    void goToEditTopic(){

        Intent intent = getIntent();
        if(intent!= null) {

            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, EditTopicActivity.class);
                intent_to_edit.putExtra("topic_object_edit", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);

            }
            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, EditTopicActivity.class);
                intent_to_edit.putExtra("topic_object_edit", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);
            }
        }

    }

    void goToStarList(){
        Intent intent = getIntent();
        if(intent!= null) {

            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, StarListActivity.class);
                intent_to_edit.putExtra("topic_object_star", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);

            }
            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, StarListActivity.class);
                intent_to_edit.putExtra("topic_object_star", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);
            }
        }

    }

    void goToQuizzes(){

        Intent intent = getIntent();
        if(intent!= null) {

            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, QuizzesActivity.class);
                intent_to_edit.putExtra("topic_object_quizzes", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);

            }
            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, QuizzesActivity.class);
                intent_to_edit.putExtra("topic_object_quizzes", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);
            }
        }

    }

    void goToFlashCard(){

        Intent intent = getIntent();
        if(intent!= null) {

            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, CardActivity.class);
                intent_to_edit.putExtra("topic_object_flash", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);

            }
            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, CardActivity.class);
                intent_to_edit.putExtra("topic_object_flash", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);
            }
        }


    }

    void goToType(){
        Intent intent = getIntent();
        if(intent!= null) {

            if (intent.hasExtra("new_topic_object_home")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("new_topic_object_home");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, TypeWordActivity.class);
                intent_to_edit.putExtra("topic_object_type_word", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);

            }
            if (intent.hasExtra("topic_object")) {
                Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object");
                Intent intent_to_edit = new Intent(DetailTopicActivity.this, TypeWordActivity.class);
                intent_to_edit.putExtra("topic_object_type_word", receivedTopic);
                activityResultLauncher.launch(intent_to_edit);
            }
        }
    }

    void importVocabulary(){
        selectCSVFile();
    }
    private void selectCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_CSV_FILE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK) {
            Uri csvUri = data.getData();
            processCSVFile(csvUri);
        }
    }
    private void processCSVFile(Uri csvUri) {
        Intent intent = getIntent();
        Topic topic = (Topic) intent.getSerializableExtra("topic_object");

        try {

            CSVReader reader = new CSVReader(new InputStreamReader(getContentResolver().openInputStream(csvUri)));

            String[] header = reader.readNext();

            List<Vocabulary> vocabularyList1 = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {


                if (row.length == 2) {
                    Vocabulary vocabulary = new Vocabulary(topic.getTopicId(), null, row[0], row[1],"Terms left", false);
                    vocabularyList1.add(vocabulary);
                } else {
                    Toast.makeText(this, "Invalid data format in CSV file", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference topicCollection = db.collection("topic");
            DocumentReference documentRef = topicCollection.document(topic.getTopicId());
            CollectionReference vocabularyCollection = documentRef.collection("vocabulary");

            for (Vocabulary vocabulary : vocabularyList1) {
                String newDocId = vocabularyCollection.document().getId();
                vocabulary.setVocabId(newDocId);
                vocabularyCollection.document(newDocId)
                        .set(vocabulary)
                        .addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(DetailTopicActivity.this, "Import vocabulary successfully.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {

                            Toast.makeText(DetailTopicActivity.this, "Failed to import Vocabulary.", Toast.LENGTH_SHORT).show();

                        });
            }

            reader.close();

            Toast.makeText(this, "Data uploaded", Toast.LENGTH_SHORT).show();

        } catch (IOException | CsvException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading CSV file", Toast.LENGTH_SHORT).show();
        }
    }
    void exportVocabulary(){
        Intent intent = getIntent();
        Topic topic = (Topic) intent.getSerializableExtra("topic_object");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");
        DocumentReference documentRef = topicCollection.document(topic.getTopicId());
        CollectionReference vocabularyCollection = documentRef.collection("vocabulary");
        vocabularyCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<Vocabulary> vocabularyList = new ArrayList<>();
                        if (error != null) {
                            return;
                        }
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                            vocabularyList.add(vocabulary);
                        }

                        exportToCsv(vocabularyList);

                    }
                });
    }
    public void exportToCsv(List<Vocabulary> vocabularyList) {
        try {
            File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ExportedData");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File csvFile = new File(exportDir, "vocabulary_export.csv");
            csvFile.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));

            String[] header = {"Term", "Meaning"};
            csvWriter.writeNext(header);

            for (Vocabulary vocabulary : vocabularyList) {
                String[] row = {vocabulary.getVocabWord(), vocabulary.getVocabMeaning()};
                csvWriter.writeNext(row);
            }

            csvWriter.close();


            Uri path = FileProvider.getUriForFile(getApplicationContext(), "com.example.afinal.provider", csvFile);
            shareCsvFile(path);

            Toast.makeText(this, "Data exported to CSV file", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting data", Toast.LENGTH_SHORT).show();
        }
    }
    private void shareCsvFile(Uri fileUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Vocabulary Data");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share CSV"));
    }
}