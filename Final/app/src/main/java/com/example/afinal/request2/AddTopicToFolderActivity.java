package com.example.afinal.request2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.adapter.TopicAdapter;
import com.example.afinal.adapter.TopicFolderAdapter;
import com.example.afinal.bottom_navigation.TopicFragment;
import com.example.afinal.objects.Folder;
import com.example.afinal.objects.Topic;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Set;

public class AddTopicToFolderActivity extends AppCompatActivity {

    RecyclerView topicRecycler;

    TopicFolderAdapter topicFolderAdapter;

    List<Topic> topicList;


    TextView done;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic_to_folder);

        init();
        initListener();
    }

    void init(){
        done = findViewById(R.id.done_add_topic_folder);

        topicRecycler = findViewById(R.id.rcv_topic_add_folder);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        topicRecycler.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        topicRecycler.addItemDecoration(dividerItemDecoration);

        topicList = new ArrayList<>();

        topicFolderAdapter = new TopicFolderAdapter(topicList, this, new TopicFolderAdapter.IClickTopicListener() {

           @Override
           public void onClickTopicItem(Topic topic) {

           }
        });
        topicRecycler.setAdapter(topicFolderAdapter);
    }
    void initListener(){
        Intent intent = getIntent();
        Folder receivedFolder = (Folder) intent.getSerializableExtra("folder_object_add");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");
        topicCollection
                .whereNotEqualTo("userId","")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        topicList.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Topic topic = doc.toObject(Topic.class);
                            if(!topic.getFolderId().contains(receivedFolder.getFolderId())){
                                topicList.add(topic);
                            }
                        }
                        topicFolderAdapter.notifyDataSetChanged();
                    }
                });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<Integer> selectedPositions = topicFolderAdapter.getSelectedPositions();
                if(selectedPositions.isEmpty()){
                    Toast.makeText(AddTopicToFolderActivity.this, "No Topic Selected.", Toast.LENGTH_SHORT).show();
                }
                for(int position : selectedPositions){
                    Topic topic = topicList.get(position);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference topicCollection = db.collection("topic");
                    DocumentReference docRef = topicCollection.document(topic.getTopicId());
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Topic topic = documentSnapshot.toObject(Topic.class);
//                                Toast.makeText(AddTopicToFolderActivity.this, "Add Topic Successfully."+ topic.getFolderId(), Toast.LENGTH_SHORT).show();

                                List<String> currentFolderIds = topic.getFolderId();
                                String newFolderId = receivedFolder.getFolderId();

                                if (!currentFolderIds.contains(newFolderId)) {
                                    currentFolderIds.add(newFolderId);
                                }
                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("folderId", currentFolderIds);
                                docRef.update(updateData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddTopicToFolderActivity.this, "Add Topic Successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

}