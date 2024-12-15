package com.example.afinal.bottom_navigation;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.adapter.TopicAdapter;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.User;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request1.HomeActivity;
import com.example.afinal.request2.AddTopicToFolderActivity;
import com.example.afinal.request2.DetailTopicActivity;
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
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {



    public HomeFragment() {
        // Required empty public constructor
    }

    RecyclerView topicRecycler;

    TopicAdapter topicAdapter;

    List<Topic> topicList;

    List<Vocabulary> vocabularyList = new ArrayList<>();

    String topicId;
    View view;

    List<String> currentTopicIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initUi();
        initListener();
        return view;
    }
    void initUi(){

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String collectionPath = "topic"; // Thay thế bằng tên của collection bạn muốn xoá
//
//// Lấy tất cả các documents trong collection
//        db.collection(collectionPath)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // Tạo một batch để chứa tất cả các thao tác ghi
//                            WriteBatch batch = db.batch();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                // Xoá mỗi document trong collection
//                                batch.delete(document.getReference());
//                            }
//
//                            // Thực hiện batch
//                            batch.commit()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                // Xoá collection thành công
//                                                Log.d(TAG, "Collection successfully deleted!");
//                                            } else {
//                                                // Xảy ra lỗi khi xoá collection
//                                                Log.w(TAG, "Error deleting collection", task.getException());
//                                            }
//                                        }
//                                    });
//                        } else {
//                            // Xảy ra lỗi khi lấy documents từ collection
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });

        topicRecycler = view.findViewById(R.id.rcv_home_fragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeFragment.this.getContext());
        topicRecycler.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(HomeFragment.this.getContext(), DividerItemDecoration.VERTICAL);
        topicRecycler.addItemDecoration(dividerItemDecoration);

        topicList = new ArrayList<>();

        topicAdapter = new TopicAdapter(topicList, HomeFragment.this.getContext(), new TopicAdapter.IClickTopicListener() {
            @Override
            public void onClickTopicItem(Topic topic) {
                createTopicForUser(topic);
            }

            public void onDeleteTopicItem(Topic topic) {
            }
        });

        topicRecycler.setAdapter(topicAdapter);
    }

    private void createTopicForUser(Topic topic) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("topic").document(topic.getTopicId());

        docRef.collection("vocabulary").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
            }
        });

        //ham tao topic moi
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    DocumentReference newRef = db.collection("topic").document();
                    Topic topic1 = snapshot.toObject(Topic.class);
                    topicId = topic1.getTopicId();
                    Topic newTopic = new Topic(topic1.getTopicName(), newRef.getId(), topic1.getUserId(),topic1.getUserName(),user.getUid(),topicId, Arrays.<String>asList("", ""),false);
                    newRef.set(newTopic);

                    for(Vocabulary vocabulary : vocabularyList){
                        DocumentReference documentReference = newRef.collection("vocabulary").document();
                        Vocabulary newVocabulary = new Vocabulary(newRef.getId(),documentReference.getId(),vocabulary.getVocabWord(),vocabulary.getVocabMeaning(),"Terms left",false);
                        documentReference.set(newVocabulary);

                    }
                    Intent intent = new Intent(getActivity(), DetailTopicActivity.class);
                    intent.putExtra("new_topic_object_home", newTopic);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        assert user != null;

        //xet study id cho user
        final DocumentReference docUserRef = db.collection("user").document(user.getUid());
        docUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {

                    User user1 = snapshot.toObject(User.class);
                    List<String> currentTopicIds = user1.getTopicStudyId();

                    String newTopicId = topicId;
                    if(!currentTopicIds.contains(newTopicId)){
                        currentTopicIds.add(newTopicId);
                    }
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("topicStudyId", currentTopicIds);

                    docUserRef.update(updateData);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    void initListener(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference docUserRef = db.collection("user").document(user.getUid());
        CollectionReference topicCollection = db.collection("topic");

        topicCollection
                .whereNotEqualTo("userId", "")
                .whereEqualTo("visible", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        assert value != null;
                        docUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document != null && document.exists()) {
                                        User user1 = document.toObject(User.class);
                                        List<String> currentTopicIds = user1.getTopicStudyId();
                                        topicList.clear();
                                        for (QueryDocumentSnapshot doc : value) {
                                            Topic topic = doc.toObject(Topic.class);

                                            if (!topic.getUserIdStudy().equals(user.getUid()) && !currentTopicIds.contains(topic.getTopicId())) {
                                                topicList.add(topic);
                                            }
                                        }

                                        topicAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d(TAG, "Current data: null");
                                    }

                                } else {
                                    Log.w(TAG, "get failed with ", task.getException());
                                }

                            }

                        });

                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        initListener();
    }
}