package com.example.afinal.request3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.databinding.ActivityCardBinding;
import com.example.afinal.databinding.ActivityTypeWordBinding;
import com.example.afinal.objects.Ranking;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request2.DetailTopicActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CardActivity extends AppCompatActivity {

    TextView frontTextView;
    TextView backTextView;

    ActivityCardBinding binding;

    ArrayList<Vocabulary> list = new ArrayList<>();

    long startTime;
    long elapsedTime;

    long finalElapsedTime;

    private TextView tvElapsedTime;
    Handler handler;

    private TextToSpeech englishTTS;
    private TextToSpeech vietnameseTTS;
    int position = 0, score = 0, left=0;
    Button next, uncertain;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        binding = ActivityCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
        getListVocab();
        handler = new Handler();
    }

    void initUi(){
        frontTextView = findViewById(R.id.frontTextView);
        backTextView = findViewById(R.id.backTextView);
        next = findViewById(R.id.btnNextFlash);
        uncertain = findViewById(R.id.btnUncertain);
    }

    void initListener(){

        binding.moreSettingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        binding.volumeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playTextToSpeech(englishTTS, list.get(position).getVocabWord());
                playTextToSpeech(vietnameseTTS, list.get(position).getVocabMeaning());
            }
        });
        playAnimation(frontTextView, 0, list.get(position).getVocabWord());
        playAnimation(backTextView, 0, list.get(position).getVocabMeaning());

        englishTTS = new TextToSpeech(CardActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Nếu khởi tạo thành công, sử dụng TextToSpeech để phát âm tiếng Anh
                    playTextToSpeech(englishTTS, frontTextView.getText().toString());
                } else {
                    Log.e("CardActivity", "English TextToSpeech initialization failed");
                }
            }
        }, "english");

        // Khởi tạo TextToSpeech cho tiếng Việt
        vietnameseTTS = new TextToSpeech(CardActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Nếu khởi tạo thành công, sử dụng TextToSpeech để phát âm tiếng Việt
                    playTextToSpeech(vietnameseTTS, backTextView.getText().toString());
                } else {
                    Log.e("CardActivity", "Vietnamese TextToSpeech initialization failed");
                }
            }
        }, "vietnamese");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position++;
                score++;
                if(position == list.size()){
                    endQuiz();
                    return;
                }

                playTextToSpeech(englishTTS, list.get(position).getVocabWord());
                playTextToSpeech(vietnameseTTS, list.get(position).getVocabMeaning());

                playAnimation(frontTextView, 0, list.get(position).getVocabWord());
                playAnimation(backTextView, 0, list.get(position).getVocabMeaning());
            }
        });

        uncertain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position++;

                if(position == list.size()){
                    endQuiz();
                    return;
                }

                playTextToSpeech(englishTTS, list.get(position).getVocabWord());
                // Phát âm tiếng Việt khi chuyển sang câu trả lời
                playTextToSpeech(vietnameseTTS, list.get(position).getVocabMeaning());


                playAnimation(frontTextView, 0, list.get(position).getVocabWord());
                playAnimation(backTextView, 0, list.get(position).getVocabMeaning());
            }
        });


        tvElapsedTime = findViewById(R.id.timer_flash_card);

        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimeRunnable, 1000);
    }

    private void showBottomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_setting_study);

        LinearLayout englishLayout = dialog.findViewById(R.id.layoutEnglish);
        englishLayout.setVisibility(View.GONE);
        LinearLayout vietnameseLayout = dialog.findViewById(R.id.layoutVietnamese);
        vietnameseLayout.setVisibility(View.GONE);

        LinearLayout studyAllLayout = dialog.findViewById(R.id.layoutStudyAll);

        studyAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                getListVocab();
                dialog.dismiss();
            }
        });

        LinearLayout studyStarLayout = dialog.findViewById(R.id.layoutStudyStar);

        studyStarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                getListVocabStar();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void playTextToSpeech(TextToSpeech tts, String textToRead) {
        if (tts != null) {
            // Chọn ngôn ngữ tương ứng
            Locale locale = tts == englishTTS ? Locale.ENGLISH : new Locale("vi", "VN");
            tts.setLanguage(locale);

            // Tạo HashMap để truyền loại ngôn ngữ cho sự kiện hoàn thành
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, tts == englishTTS ? "english" : "vietnamese");

            tts.speak(textToRead, TextToSpeech.QUEUE_ADD, params);
        }
    }
    @Override
    protected void onDestroy() {
        if (englishTTS != null) {
            englishTTS.stop();
            englishTTS.shutdown();
        }

        if (vietnameseTTS != null) {
            vietnameseTTS.stop();
            vietnameseTTS.shutdown();
        }

        super.onDestroy();
    }

    private void saveJoinTime() {
        // Lấy SharedPreferences của ứng dụng
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Lấy Editor để chỉnh sửa SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();

        // Lưu thời gian hiện tại
        long currentTime = System.currentTimeMillis();
        editor.putLong("join_time", currentTime);

        // Áp dụng các thay đổi
        editor.apply();
    }

    private String getJoinTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        long joinTimeMillis = preferences.getLong("join_time", 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date joinDate = new Date(joinTimeMillis);
        return sdf.format(joinDate);
    }

    private void endQuiz() {
        saveJoinTime();

        String joinTime = getJoinTime();

        finalElapsedTime = elapsedTime;

        long finalElapsedTimeInSeconds = finalElapsedTime / 1000;

        long minutes = TimeUnit.SECONDS.toMinutes(finalElapsedTimeInSeconds);
        long seconds = finalElapsedTimeInSeconds - TimeUnit.MINUTES.toSeconds(minutes);

        String formattedTime = String.format(Locale.getDefault(), "%d m %d s", minutes, seconds);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent1 = getIntent();
        Topic receivedTopic = (Topic) intent1.getSerializableExtra("topic_object_flash");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("ranking");
        DocumentReference documentRef = topicCollection.document(user.getUid()+receivedTopic.getTopicId());

        left = list.size() - score;

        documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Ranking ranking1 = document.toObject(Ranking.class);

                        Ranking ranking = new Ranking(documentRef.getId(),receivedTopic.getOldTopicId(), receivedTopic.getTopicId() ,user.getUid(),user.getEmail(), formattedTime, joinTime, ranking1.getTimeStudied() + 1, score);
                        // Nếu tài liệu đã tồn tại, sử dụng update()
                        documentRef.update(ranking.toMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        handler.removeCallbacks(updateTimeRunnable);
                                        Intent intent_to_edit = new Intent(CardActivity.this, ScoreActivity.class);
                                        intent_to_edit.putExtra("card_to_ranking", ranking);
                                        intent_to_edit.putExtra("card_left", left);
                                        startActivity(intent_to_edit);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Xử lý khi cập nhật thất bại
                                    }
                                });
                    } else {
                        Ranking ranking = new Ranking(documentRef.getId(),receivedTopic.getOldTopicId(), receivedTopic.getTopicId(), user.getUid(), user.getEmail(),formattedTime, joinTime, 1, score);
                        // Nếu tài liệu không tồn tại, sử dụng set()
                        documentRef.set(ranking.toMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        handler.removeCallbacks(updateTimeRunnable);
                                        Intent intent_to_edit = new Intent(CardActivity.this, ScoreActivity.class);
                                        intent_to_edit.putExtra("card_to_ranking", ranking);
                                        intent_to_edit.putExtra("card_left", left);
                                        startActivity(intent_to_edit);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Xử lý khi thêm mới thất bại
                                    }
                                });
                    }
                } else {
                    // Xử lý khi có lỗi khi kiểm tra tài liệu
                }
            }
        });

    }

    private Runnable updateTimeRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {

            elapsedTime = System.currentTimeMillis() - startTime;


            long elapsedSeconds = elapsedTime / 1000;


            tvElapsedTime.setText(elapsedSeconds+"/s");


            handler.postDelayed(this, 1000);
        }
    };

    private void playAnimation(View view, int value, String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value)
                .setDuration(500)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        if(value==0){
                            try{
                                if (view instanceof TextView) {
                                    //set cau hoi cho data
                                    ((TextView) view).setText(data);
                                    binding.totalQuestionFlash.setText(position+1+"/"+list.size());
                                } else if (view instanceof Button) {
                                    ((Button) view).setText(data);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            view.setTag(data);
                            //hien thi dap an sau khi start
                            playAnimation(view, 1, data);
                        }
                    }
                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
    }

    void getListVocab() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_flash");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(CardActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    return;
                }

                assert value != null;
                for (QueryDocumentSnapshot doc : value) {
                    Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                    list.add(vocabulary);
                }
                Collections.shuffle(list);
                // Initialize the quiz after fetching the data
                initListener();
            }
        });
    }

    void getListVocabStar() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_flash");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection.whereEqualTo("star", true) // Thêm điều kiện lọc
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(CardActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                            list.add(vocabulary);
                        }
                        Collections.shuffle(list);
                        // Initialize the quiz after fetching the data
                        initListener();
                    }
                });
    }
}