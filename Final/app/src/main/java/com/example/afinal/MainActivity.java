package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.afinal.bottom_navigation.FolderFragment;
import com.example.afinal.bottom_navigation.HomeFragment;
import com.example.afinal.bottom_navigation.ProfileFragment;
import com.example.afinal.bottom_navigation.TopicFragment;
import com.example.afinal.databinding.ActivityMainBinding;
import com.example.afinal.objects.Topic;
import com.example.afinal.request2.AddFolderActivity;
import com.example.afinal.request2.AddTopicActivity;
import com.example.afinal.request2.DetailTopicActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    public static final int PICK_CSV_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            }
            else if(item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            else if(item.getItemId() == R.id.topic) {
                replaceFragment(new TopicFragment());
            }
            else if(item.getItemId() == R.id.folder) {
                replaceFragment(new FolderFragment());
            }
            return true;
        });

        binding.bottomNav.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    private void showBottomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout folderLayout = dialog.findViewById(R.id.layoutFolder);
        LinearLayout topicLayout = dialog.findViewById(R.id.layoutTopic);

        folderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFolderActivity.class);
                startActivity(intent);
                dialog.dismiss();

            }
        });
        topicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTopicActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    void goToTopicDetail(Topic topic){
        Intent intent = new Intent(MainActivity.this, DetailTopicActivity.class);
        intent.putExtra("topic_object", topic);
        startActivity(intent);
    }


}