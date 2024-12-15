package com.example.afinal.request2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.adapter.TopicAdapter;
import com.example.afinal.bottom_navigation.TopicFragment;
import com.example.afinal.objects.Folder;
import com.example.afinal.objects.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailFolderActivity extends AppCompatActivity implements ItemTouchListener {


    ImageView back, more;
    TextView folderName, userName;

    RecyclerView topicRecycler;

    TopicAdapter topicAdapter;

    List<Topic> topicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_folder);

        initUi();
        initListener();
    }

    private ActivityResultLauncher<Intent> activityResultLauncher
            = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()== RESULT_OK){
                        Intent intent = o.getData();

                        Folder folder = (Folder) intent.getSerializableExtra("updated_folder");

                        folderName.setText(folder.getFolderName());
                        userName.setText(folder.getUserName());
                    }
                }
            });

    void initUi(){
        back = findViewById(R.id.back_detail_folder);
        more = findViewById(R.id.more_detail_folder);
        folderName = findViewById(R.id.folder_name_detail);
        userName = findViewById(R.id.user_name_folder);

        topicRecycler = findViewById(R.id.recycler_view_topic_folder);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        topicRecycler.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        topicRecycler.addItemDecoration(dividerItemDecoration);

        topicList = new ArrayList<>();

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewTouch(0, ItemTouchHelper.LEFT,this);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(topicRecycler);

        topicAdapter = new TopicAdapter(topicList, this, new TopicAdapter.IClickTopicListener() {
            @Override
            public void onClickTopicItem(Topic topic) {
                goToTopicDetail(topic);
            }
            public void onDeleteTopicItem(Topic topic) {

            }
        });
        topicRecycler.setAdapter(topicAdapter);
    }

    private void goToTopicDetail(Topic topic) {
        Intent intent = new Intent(DetailFolderActivity.this, DetailTopicActivity.class);
        intent.putExtra("topic_object", topic);
        startActivity(intent);
    }

    void initListener(){
        Intent intent = getIntent();
        Folder receivedFolder = (Folder) intent.getSerializableExtra("folder_object");

        folderName.setText(receivedFolder.getFolderName());
        userName.setText(receivedFolder.getUserName());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        getListTopic();

    }

    void getListTopic(){
        Intent intent = getIntent();
        Folder receivedFolder = (Folder) intent.getSerializableExtra("folder_object");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");
        topicCollection
                .whereArrayContains("folderId", receivedFolder.getFolderId())
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
    private void showBottomDialog(){

//        Intent intent = getIntent();
//        Folder receivedFolder = (Folder) intent.getSerializableExtra("folder_object");

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_folder_detail);


        LinearLayout addLayout = dialog.findViewById(R.id.layoutAddTopic);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEditFolder);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDeleteFolder);

        deleteLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                if (intent != null) {
                    Folder receivedFolder = (Folder) intent.getSerializableExtra("folder_object");
                    new AlertDialog.Builder(DetailFolderActivity.this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage("Are you sure to delete this item")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference topicCollection = db.collection("folder");
                                    DocumentReference documentRef = topicCollection.document(receivedFolder.getFolderId());
                                    documentRef
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(DetailFolderActivity.this, "Delete Folder successfully.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        });

        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddTopic();
                dialog.dismiss();
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEdit();
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    void goToEdit(){
        Intent intent = getIntent();
        if(intent != null) {
            Folder receivedTopic = (Folder) intent.getSerializableExtra("folder_object");
            Intent intent_to_edit = new Intent(DetailFolderActivity.this, EditFolderActivity.class);
            intent_to_edit.putExtra("folder_object_edit", receivedTopic);
            activityResultLauncher.launch(intent_to_edit);
        }
    }

    void goToAddTopic(){
        Intent intent = getIntent();
        if(intent != null) {
            Folder receivedTopic = (Folder) intent.getSerializableExtra("folder_object");
            Intent intent_to_edit = new Intent(DetailFolderActivity.this, AddTopicToFolderActivity.class);
            intent_to_edit.putExtra("folder_object_add", receivedTopic);
            activityResultLauncher.launch(intent_to_edit);
        }
    }

    void onRemove(Topic topic){
        Intent intent = getIntent();
        Folder receivedFolder = (Folder) intent.getSerializableExtra("folder_object");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");
        DocumentReference documentReference = topicCollection.document(topic.getTopicId());

        documentReference.update("folderId", FieldValue.arrayRemove(receivedFolder.getFolderId()));
        Toast.makeText(DetailFolderActivity.this, "Remove Topic successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof TopicAdapter.TopicViewHolder){

            Topic topic = topicList.get(viewHolder.getAdapterPosition());

            onRemove(topic);
            topicAdapter.notifyDataSetChanged();
        }
    }
}