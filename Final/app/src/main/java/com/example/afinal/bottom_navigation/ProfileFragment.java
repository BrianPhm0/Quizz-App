package com.example.afinal.bottom_navigation;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.afinal.R;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.User;
import com.example.afinal.request1.ChangePasswordActivity;
import com.example.afinal.request1.HomeActivity;
import com.example.afinal.request1.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    View mView;

    ImageView imageView;
    EditText name;
    TextView email;

    Button update, change_pass, sign_out;

    Uri mUri;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == RESULT_OK){
                                Intent intent = result.getData();
                                if(intent == null)
                                {
                                    return;
                                }
                                Uri uri = intent.getData();
                                setUri(uri);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                                    setBitmapImageView(bitmap);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });

    void setUri(Uri mUri){
        this.mUri = mUri;
    }


    public ProfileFragment() {
        // Required empty public constructor
    }
    public void setBitmapImageView(Bitmap bitmapImageView){
        imageView.setImageBitmap(bitmapImageView);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        init();
        initListener();

        return mView;
    }

    void init(){
        imageView = mView.findViewById(R.id.user_image);
        name = mView.findViewById(R.id.user_name_profile);
        email = mView.findViewById(R.id.email_profile);
        update = mView.findViewById(R.id.update);
        change_pass = mView.findViewById(R.id.change_password);
        sign_out = mView.findViewById(R.id.sign_out);
    }
    void initListener(){
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });

        showInformation();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFragment.this.getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    void showInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference userCollection = db.collection("user");
            DocumentReference documentRef = userCollection.document(user.getUid());

            String emailVal = user.getEmail();
            String nameVal = user.getDisplayName();
            Uri photoUrl = user.getPhotoUrl();

            documentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }

                    if(value != null && value.exists()){
                        User user1 = value.toObject(User.class);
                        name.setText(user1.getName());
                    }
                }
            });

            email.setText(emailVal);
            if(photoUrl == null){
                return;
            }
            Glide.with(this).load(photoUrl)
                    .into(imageView);

        }
    }

    void onClickUpdateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String nameVal = name.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nameVal)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference ref = db.collection("user").document(user.getUid());
                        ref.update("name", nameVal);
                        clearFocus();
                        Toast.makeText(getActivity(), "Update profile successfully",
                                Toast.LENGTH_SHORT).show();
                        showInformation();
                    }
                });
    }

    void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }


    void SignOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileFragment.this.getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    void clearFocus(){
        name.clearFocus();
    }
}