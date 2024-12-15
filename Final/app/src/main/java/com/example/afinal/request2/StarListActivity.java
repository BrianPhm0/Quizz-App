package com.example.afinal.request2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.afinal.R;
import com.example.afinal.adapter.DetailTopicAdapter;
import com.example.afinal.adapter.TopicAdapter;
import com.example.afinal.adapter.VocabAdapter;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.Vocabulary;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StarListActivity extends AppCompatActivity {
    ImageView back;

    RecyclerView vocabularyRecycler;

    DetailTopicAdapter detailTopicAdapter;

    List<Vocabulary> vocabularyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_list);
        initUi();
        initListener();
    }

    void initUi(){
        back = findViewById(R.id.back_star);
        vocabularyRecycler = findViewById(R.id.rcv_star);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        vocabularyRecycler.setLayoutManager(linearLayoutManager);

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

    private void onClickStarCheck(Vocabulary vocabulary, boolean check) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(vocabulary.getTopicId())
                .collection("vocabulary");

        DocumentReference documentRef = topicCollection.document(vocabulary.getVocabId());
        documentRef.update("star", check);
    }

    void initListener(){
        Intent intent = getIntent();
        if(intent!= null) {
            Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_star");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference topicCollection = db.collection("topic")
                    .document(receivedTopic.getTopicId())
                    .collection("vocabulary");

            topicCollection
                    .whereEqualTo("star", true)
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}