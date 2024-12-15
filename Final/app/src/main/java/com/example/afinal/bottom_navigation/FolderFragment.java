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
import com.example.afinal.objects.Folder;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request2.DetailFolderActivity;
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


public class FolderFragment extends Fragment {


    RecyclerView folderRecycler;

    FolderAdapter folderAdapter;

    List<Folder> folderList;
    View view;

    public FolderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_folder, container, false);
        initUi();
        initListener();
        return view;
    }
    void initUi(){
        folderRecycler = view.findViewById(R.id.rcv_folder_fragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FolderFragment.this.getContext());
        folderRecycler.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(FolderFragment.this.getContext(), DividerItemDecoration.VERTICAL);
        folderRecycler.addItemDecoration(dividerItemDecoration);

        folderList = new ArrayList<>();

        folderAdapter = new FolderAdapter(folderList, FolderFragment.this.getContext(), new FolderAdapter.IClickFolderListener() {
            @Override
            public void onClickFolderItem(Folder folder) {
                goToDetailFolder(folder);
            }
        });

        folderRecycler.setAdapter(folderAdapter);
    }

    private void goToDetailFolder(Folder folder) {
        Intent intent = new Intent(getActivity(), DetailFolderActivity.class);
        intent.putExtra("folder_object", folder);
        startActivity(intent);
    }

    void initListener(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference folderCollection = db.collection("folder");

        folderCollection
                .whereEqualTo("userId", user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }

                folderList.clear();

                assert value != null;
                for (QueryDocumentSnapshot doc : value) {
                    Folder folder = doc.toObject(Folder.class);
                    folderList.add(folder);
                }
                folderAdapter.notifyDataSetChanged();
            }
        });
    }
}