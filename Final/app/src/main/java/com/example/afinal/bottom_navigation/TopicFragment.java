package com.example.afinal.bottom_navigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.afinal.R;
import com.example.afinal.adapter.FolderAdapter;
import com.example.afinal.adapter.TopicAdapter;
import com.example.afinal.objects.Folder;
import com.example.afinal.objects.Topic;
import com.example.afinal.request2.DetailTopicActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class TopicFragment extends Fragment {

    RecyclerView topicRecycler;

    TopicAdapter topicAdapter;

    List<Topic> topicList;
    View view;

    private static final int DELETE_TOPIC_REQUEST_CODE = 1;

    public TopicFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_topic, container, false);

        initUi();
        initListener();
        return view;
    }

    void initUi(){
        topicRecycler = view.findViewById(R.id.rcv_topic_fragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TopicFragment.this.getContext());
        topicRecycler.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(TopicFragment.this.getContext(), DividerItemDecoration.VERTICAL);
        topicRecycler.addItemDecoration(dividerItemDecoration);

        topicList = new ArrayList<>();

        topicAdapter = new TopicAdapter(topicList, TopicFragment.this.getContext(), new TopicAdapter.IClickTopicListener() {
            @Override
            public void onClickTopicItem(Topic topic) {
                goToTopicDetail(topic);
            }
            public void onDeleteTopicItem(Topic topic) {

            }
        });
        topicRecycler.setAdapter(topicAdapter);
    }

    void initListener(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");
        assert user != null;
        topicCollection
                .whereNotEqualTo("userId","")
                .whereEqualTo("userIdStudy", user.getUid())

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
                            topicList.add(topic);
                        }
                        topicAdapter.notifyDataSetChanged();
                    }
                });

    }
    void goToTopicDetail(Topic topic){
        Intent intent = new Intent(getActivity(), DetailTopicActivity.class);
        intent.putExtra("topic_object", topic);
        startActivity(intent);

    }
    @Override
    public void onResume() {
        super.onResume();
        initListener();
    }

}