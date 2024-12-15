package com.example.afinal.request3;

import static android.app.ProgressDialog.show;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.databinding.ActivityTypeWordBinding;
import com.example.afinal.objects.Ranking;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request2.DetailTopicActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ArrayTable;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TypeWordActivity extends AppCompatActivity {

    ActivityTypeWordBinding binding;

    ArrayList<Vocabulary> list = new ArrayList<>();

    int count = 0, position = 0, score = 0, left=0;

    long startTime;
    long elapsedTime;

    long finalElapsedTime;

    private TextView tvElapsedTime;

    TextToSpeech textToSpeech;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTypeWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
        getListVocab();
        handler = new Handler();
    }

    private void playAnimation(View view, int value, String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value)
                .setDuration(500)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {
                        if(value == 0){
                            binding.answerType.setText("");
                        }
                    }
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        if(value==0){

                            //set cau hoi cho data
                            ((TextView) view).setText(data);
                            binding.totalQuestionType.setText(position+1+"/"+list.size());

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

    private void showBottomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_setting_study);

        LinearLayout englishLayout = dialog.findViewById(R.id.layoutEnglish);
        LinearLayout vietnameseLayout = dialog.findViewById(R.id.layoutVietnamese);
        LinearLayout studyAllLayout = dialog.findViewById(R.id.layoutStudyAll);
        LinearLayout studyStarLayout = dialog.findViewById(R.id.layoutStudyStar);

        studyAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                getListVocab();
                dialog.dismiss();
            }
        });

        studyStarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                getListVocabStar();
                dialog.dismiss();
            }
        });

        englishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVietnameseType(false);
                list.clear();
                getListVocab();
                dialog.dismiss();
            }
        });

        vietnameseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                isVietnameseType(true);
                getListVocab();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void isVietnameseType(boolean isVietnameseMode) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean("isVietnameseType", isVietnameseMode).apply();
    }


    private boolean getVietnameseMode() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean("isVietnameseType", false);
    }


    void english(){

        playAnimation(binding.questionType, 0, list.get(position).getVocabWord());

        binding.volumeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textToSpeech != null) {
                    // Thiết lập ngôn ngữ cho TextToSpeech thành tiếng Anh
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Xử lý trường hợp ngôn ngữ không được hỗ trợ
                    } else {
                        // Nếu ngôn ngữ hỗ trợ, đọc từ vựng tiếng Anh
                        textToSpeech.speak(list.get(position).getVocabWord(), TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.answerType.clearFocus();
                String answerVal = binding.answerType.getText().toString().toLowerCase().trim();
                if(answerVal.equals("")){
                    binding.answerType.requestFocus();
                    binding.answerType.setError("Field can't be empty");
                }
                else{
                    checkAnswer(answerVal);

                    position++;

                    if(position == list.size()){
                        endQuiz();
                        return;
                    }
                    count = 0;
                    playAnimation(binding.questionType, 0, list.get(position).getVocabWord());
                }

            }
        });
    }
    void vietnamese() {
        playAnimation(binding.questionType, 0, list.get(position).getVocabMeaning());

        binding.volumeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textToSpeech != null) {
                    // Thiết lập ngôn ngữ cho TextToSpeech thành tiếng Việt
                    Locale vietnameseLocale = new Locale("vi", "VN");
                    int result = textToSpeech.setLanguage(vietnameseLocale);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Xử lý trường hợp ngôn ngữ không được hỗ trợ
                    } else {
                        // Nếu ngôn ngữ hỗ trợ, đọc từ vựng tiếng Việt
                        textToSpeech.speak(list.get(position).getVocabMeaning(), TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.answerType.clearFocus();
                String answerVal = binding.answerType.getText().toString().toLowerCase().trim();
                if (answerVal.equals("")) {
                    binding.answerType.requestFocus();
                    binding.answerType.setError("Field can't be empty");
                } else {
                    checkAnswerViet(answerVal);

                    position++;

                    if (position == list.size()) {
                        endQuiz();
                        return;
                    }
                    count = 0;
                    playAnimation(binding.questionType, 0, list.get(position).getVocabWord());
                }

            }
        });
    }
    void initUi(){
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("QuizzesActivity", "TextToSpeech: Language not supported");
                    }
                } else {
                    Log.e("QuizzesActivity", "TextToSpeech initialization failed");
                }
            }
        });
    }
    void initListener(){

        binding.moreSettingType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        if(getVietnameseMode()){
            vietnamese();
        }
        else if(!getVietnameseMode()){
            english();
        }

        tvElapsedTime = findViewById(R.id.timer_type_word);

        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimeRunnable, 1000);
    }

    private void endQuiz() {
        saveJoinTime();

        String joinTime = getJoinTime();


        finalElapsedTime = elapsedTime;

        long finalElapsedTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(finalElapsedTime);
        long minutes = finalElapsedTimeInSeconds / 60;
        long seconds = finalElapsedTimeInSeconds % 60;
        String formattedTime = String.format(Locale.getDefault(), "%d m %d s", minutes, seconds);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent1 = getIntent();
        Topic receivedTopic = (Topic) intent1.getSerializableExtra("topic_object_type_word");

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

                        Ranking ranking = new Ranking(documentRef.getId(),receivedTopic.getOldTopicId(), receivedTopic.getTopicId(), user.getUid(),user.getEmail(), formattedTime, joinTime, ranking1.getTimeStudied() + 1, score);
                        // Nếu tài liệu đã tồn tại, sử dụng update()
                        documentRef.update(ranking.toMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        handler.removeCallbacks(updateTimeRunnable);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 5000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent_to_edit = new Intent(TypeWordActivity.this, ScoreActivity.class);
                                                intent_to_edit.putExtra("to_ranking", ranking);
                                                intent_to_edit.putExtra("left", left);
                                                startActivity(intent_to_edit);
                                            }
                                        }, 1000);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Xử lý khi cập nhật thất bại
                                    }
                                });
                    } else {
                        Ranking ranking = new Ranking(documentRef.getId(),receivedTopic.getOldTopicId(), receivedTopic.getTopicId(), user.getUid(), user.getEmail(), formattedTime, joinTime, 1, score);
                        // Nếu tài liệu không tồn tại, sử dụng set()
                        documentRef.set(ranking.toMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        handler.removeCallbacks(updateTimeRunnable);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 5000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent_to_edit = new Intent(TypeWordActivity.this, ScoreActivity.class);
                                                intent_to_edit.putExtra("to_ranking", ranking);
                                                intent_to_edit.putExtra("left", left);
                                                startActivity(intent_to_edit);
                                            }
                                        }, 1000);
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
        // Lấy SharedPreferences của ứng dụng
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Lấy thời gian được lưu trữ từ SharedPreferences
        long joinTimeMillis = preferences.getLong("join_time", 0);

        // Chuyển đổi thời gian từ milliseconds sang định dạng chuỗi (ví dụ: "dd/MM/yyyy HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date joinDate = new Date(joinTimeMillis);
        return sdf.format(joinDate);
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
    @SuppressLint("SetTextI18n")
    void checkAnswer(String answer){
        Intent intent1 = getIntent();
        Topic receivedTopic = (Topic) intent1.getSerializableExtra("topic_object_type_word");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(answer.equals(list.get(position).getVocabMeaning().toLowerCase())){

             View customDialogView = getLayoutInflater().inflate(R.layout.dialog_answer, null);
             AlertDialog.Builder builder = new AlertDialog.Builder(TypeWordActivity.this);
             builder.setView(customDialogView);
             AlertDialog dialog = builder.create();
             dialog.show();

             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     if (dialog.isShowing()) {
                         dialog.dismiss();
                     }
                 }
             }, 900);

//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//            CollectionReference topicCollection = db.collection("topic")
//                    .document(receivedTopic.getTopicId())
//                    .collection("vocabulary");
//
//            DocumentReference documentRef = topicCollection.document(list.get(position).getVocabId());
//            documentRef.update("status", "Know");

            score++;
        }
        else {
            String trueAnswer = list.get(position).getVocabMeaning().toLowerCase();
            View customDialogView = getLayoutInflater().inflate(R.layout.dialog_wrong_answer, null);
            TextView messageTextView = customDialogView.findViewById(R.id.dialog_wrong);

            messageTextView.setText("The true answer is " + trueAnswer);

            AlertDialog.Builder builder = new AlertDialog.Builder(TypeWordActivity.this);
            builder.setView(customDialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 if (dialog.isShowing()) {
                     dialog.dismiss();
                 }
             }
            }, 1200);

//            CollectionReference topicCollection = db.collection("topic")
//                    .document(receivedTopic.getTopicId())
//                    .collection("vocabulary");
//
//            DocumentReference documentRef = topicCollection.document(list.get(position).getVocabId());
//            documentRef.update("status", "Still learning");
        }
    }

    void checkAnswerViet(String answer){
        if(answer.equals(list.get(position).getVocabWord().toLowerCase())){

            View customDialogView = getLayoutInflater().inflate(R.layout.dialog_answer, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(TypeWordActivity.this);
            builder.setView(customDialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, 900);

            score++;
        }
        else {
            String trueAnswer = list.get(position).getVocabWord().toLowerCase();
            View customDialogView = getLayoutInflater().inflate(R.layout.dialog_wrong_answer, null);
            TextView messageTextView = customDialogView.findViewById(R.id.dialog_wrong);

            messageTextView.setText("The true answer is " + trueAnswer);

            AlertDialog.Builder builder = new AlertDialog.Builder(TypeWordActivity.this);
            builder.setView(customDialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, 1200);
        }
    }

    void getListVocabStar() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_type_word");

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
                    Toast.makeText(TypeWordActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    return;
                }
                list.clear();
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
    void getListVocab() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_type_word");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(TypeWordActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    return;
                }
                list.clear();
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