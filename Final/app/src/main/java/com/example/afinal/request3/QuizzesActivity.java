package com.example.afinal.request3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.databinding.ActivityQuizzesBinding;
import com.example.afinal.objects.QuestionModel;
import com.example.afinal.objects.Ranking;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request2.AddFolderActivity;
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
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class QuizzesActivity extends AppCompatActivity {


    // Binding dữ liệu
    ActivityQuizzesBinding binding;

    // Danh sách câu hỏi
    ArrayList<QuestionModel> list = new ArrayList<>();

    List<Vocabulary> vocabularies = new ArrayList<>();

    // Biến
    int count = 0, position = 0, score = 0;

    long startTime;
    long elapsedTime;

    long finalElapsedTime;

    private TextView tvElapsedTime;
    Handler handler;

    TextToSpeech textToSpeech;

    View sound;


    int left = 0;

    // Kiểm tra xem đã chọn câu trả lời hay chưa
    boolean isAnyOptionSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUi();

        binding = ActivityQuizzesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnNext.setAlpha((float) 0.3);


        if(getVietnameseMode() && getStarMode()){
            getListVietVocabStar();
        }
        else if(!getVietnameseMode() && getStarMode()){
            getListVocabStar();
        }
        else if(getVietnameseMode()){
            getListVietVocab();
        }
        else if(!getVietnameseMode()){
            getListVocab();
        }
    }

    private void isVietnamese(boolean isVietnameseMode) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean("isVietnameseMode", isVietnameseMode).apply();
    }

    private void isStudyStar(boolean isStarMode) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean("isStarMode", isStarMode).apply();
    }

    private boolean getVietnameseMode() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean("isVietnameseMode", false);
    }

    private boolean getStarMode() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean("isStarMode", false);
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
                isStudyStar(false);
                list.clear();
                dialog.dismiss();
                recreate();
            }
        });

        studyStarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStudyStar(true);
                list.clear();
                dialog.dismiss();
                recreate();

            }
        });

        englishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVietnamese(false);
                list.clear();
                dialog.dismiss();
                recreate();
            }
        });

        vietnameseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVietnamese(true);
                list.clear();
                dialog.dismiss();
                recreate();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    void getListVocabStar() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_quizzes");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection.whereEqualTo("star", true) // Thêm điều kiện lọc
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(QuizzesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        vocabularies.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                            vocabularies.add(vocabulary);
                        }
                        createQuestionsFromVocabularies(vocabularies);

                        // Khởi tạo trò chơi sau khi lấy dữ liệu
                        initListener();
                    }
                });
    }

    void getListVietVocabStar() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_quizzes");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection.whereEqualTo("star", true) // Thêm điều kiện lọc
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(QuizzesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        vocabularies.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                            vocabularies.add(vocabulary);
                        }
                        createVietQuestionsFromVocabularies(vocabularies);

                        // Khởi tạo trò chơi sau khi lấy dữ liệu
                        initListener();
                    }
                });
    }

    void createQuestionWithOptions(Vocabulary vocabulary, List<Vocabulary> vocabularies) {
        String correctAnswer = vocabulary.getVocabMeaning();

        List<String> otherDefinitions = new ArrayList<>();
        for (Vocabulary vocab : vocabularies) {
            if (!vocab.getVocabWord().equals(vocabulary.getVocabWord())) {
                otherDefinitions.add(vocab.getVocabMeaning());
            }
        }

        List<String> randomDefinitions = getRandomDefinitions(otherDefinitions, 3);

        // Add the correct answer to the options
        randomDefinitions.add(correctAnswer);

        // Shuffle the options
        Collections.shuffle(randomDefinitions);

        QuestionModel questionModel = new QuestionModel(
                vocabulary.getVocabWord(),
                randomDefinitions.get(0),
                randomDefinitions.get(1),
                randomDefinitions.get(2),
                randomDefinitions.get(3),  // Assuming you have 4 options
                correctAnswer,
                "");

        list.add(questionModel);
    }
    void createVietQuestionWithOptions(Vocabulary vocabulary, List<Vocabulary> vocabularies) {
        String correctAnswer = vocabulary.getVocabWord();

        List<String> otherDefinitions = new ArrayList<>();
        for (Vocabulary vocab : vocabularies) {
            if (!vocab.getVocabMeaning().equals(vocabulary.getVocabMeaning())) {
                otherDefinitions.add(vocab.getVocabWord());
            }
        }

        List<String> randomDefinitions = getRandomDefinitions(otherDefinitions, 3);

        // Add the correct answer to the options
        randomDefinitions.add(correctAnswer);

        // Shuffle the options
        Collections.shuffle(randomDefinitions);

        QuestionModel questionModel = new QuestionModel(
                vocabulary.getVocabMeaning(),
                randomDefinitions.get(0),
                randomDefinitions.get(1),
                randomDefinitions.get(2),
                randomDefinitions.get(3),  // Assuming you have 4 options
                correctAnswer,
                "");

        list.add(questionModel);
    }
    void createQuestionsFromVocabularies(List<Vocabulary> vocabularies) {
        for (Vocabulary vocabulary : vocabularies) {
            createQuestionWithOptions(vocabulary, vocabularies);
        }

        Collections.shuffle(list);
    }

    void createVietQuestionsFromVocabularies(List<Vocabulary> vocabularies) {
        for (Vocabulary vocabulary : vocabularies) {
            createVietQuestionWithOptions(vocabulary, vocabularies);
        }

        Collections.shuffle(list);
    }
    private List<String> getRandomDefinitions(List<String> definitions, int count) {
        Collections.shuffle(definitions);
        return definitions.subList(0, count);
    }
    private void disableOtherOptions(View selectedOption) {
        for (int i = 0; i < 4; i++) {
            View option = binding.optionContainer.getChildAt(i);
            if (option != selectedOption) {
                option.setEnabled(false);
            }
        }
    }

    private void playAnimation(View view, int value, String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value)
                .setDuration(500)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {
                        if (value == 0 && count < 4) {
                            // Lựa chọn chuỗi
                            String option = "";
                            if (count == 0) {
                                option = list.get(position).getOptionA();
                            } else if (count == 1) {
                                option = list.get(position).getOptionB();
                            } else if (count == 2) {
                                option = list.get(position).getOptionC();
                            } else if (count == 3) {
                                option = list.get(position).getOptionD();
                            }

                            // Khởi tạo các kết quả lựa chọn
                            playAnimation(binding.optionContainer.getChildAt(count), 0, option);
                            // Lấy ra các lựa chọn
                            count++;
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        if (value == 0) {
                            try {
                                if (view instanceof TextView) {
                                    // Đặt câu hỏi cho dữ liệu
                                    ((TextView) view).setText(data);

                                    binding.totalQuestion.setText(position + 1 + "/" + list.size());
                                } else if (view instanceof Button) {
                                    ((Button) view).setText(data);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            view.setTag(data);
                            // Hiển thị đáp án sau khi bắt đầu
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

    // Khởi tạo lại lựa chọn sau khi nhấn next
    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            binding.optionContainer.getChildAt(i).setEnabled(enable);
            if (enable) {
                isAnyOptionSelected = false;
                binding.optionContainer.getChildAt(i).setBackgroundResource(R.drawable.custom_login_btn);
            }
        }
    }

    // Kiểm tra lại kết quả
    private void checkAnswer(Button selectedOption) {
        if (selectedOption.getText().toString().equals(list.get(position).getCorrectAnswer())) {
            score++;
            selectedOption.setBackgroundResource(R.drawable.right_answer);


        } else {
            selectedOption.setBackgroundResource(R.drawable.wrong_ansver);
            Button correctOption = (Button) binding.optionContainer.findViewWithTag(list.get(position).getCorrectAnswer());
            if (correctOption != null) {
                correctOption.setBackgroundResource(R.drawable.right_answer);
            }
        }
    }

    void initUi() {
        handler = new Handler();
        sound = findViewById(R.id.quizzes_sound);

        // Kiểm tra null trước khi gán sự kiện
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

    void initListener() {


        binding.moreSettingQuizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });



        for (int i = 0; i < 4; i++) {
            binding.optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Đánh dấu đã lựa chọn
                    isAnyOptionSelected = true;
                    binding.btnNext.setAlpha(1);
                    // Kiểm tra kết quả
                    checkAnswer((Button) view);
                    binding.btnNext.setEnabled(true);
                    disableOtherOptions(view);
                }
            });
        }
        if(getVietnameseMode()){
            binding.quizzesSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textToSpeech != null) {
                        // Thiết lập ngôn ngữ tiếng Việt
                        Locale vietnameseLocale = new Locale("vi", "VN");
                        textToSpeech.setLanguage(vietnameseLocale);

                        // Kiểm tra xem TextToSpeech đã khởi tạo thành công chưa
                        textToSpeech.speak(list.get(position).getQuestion(), TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });

        }
        else{
            binding.quizzesSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (textToSpeech != null) {
                        // Kiểm tra xem TextToSpeech đã khởi tạo thành công chưa
                        textToSpeech.speak( list.get(position).getQuestion(), TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            });
        }

        // Khởi tạo play animation
        playAnimation(binding.question, 0, list.get(position).getQuestion());

        // Thực hiện nút next
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.btnNext.setEnabled(false);
                binding.btnNext.setAlpha((float) 0.3);
                enableOption(true);

                // Cộng 1 position
                position++;

                if (position == list.size()) {
                    endQuiz();
                    return;
                }
                count = 0;

                // Sau khi nhấn next sẽ thay đổi lại màn hình
                playAnimation(binding.question, 0, list.get(position).getQuestion());
            }
        });

        tvElapsedTime = findViewById(R.id.timer);

        startTime = System.currentTimeMillis();

        handler.postDelayed(updateTimeRunnable, 1000);
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;

            long elapsedSeconds = elapsedTime / 1000;

            tvElapsedTime.setText(elapsedSeconds + "/s");

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateTimeRunnable);
        super.onDestroy();
    }

    void getListVocab() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_quizzes");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(QuizzesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    return;
                }

                vocabularies.clear();
                assert value != null;
                for (QueryDocumentSnapshot doc : value) {
                    Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                    vocabularies.add(vocabulary);
                }
                createQuestionsFromVocabularies(vocabularies);

                // Khởi tạo trò chơi sau khi lấy dữ liệu
                initListener();
            }
        });
    }

    void getListVietVocab() {
        Intent intent = getIntent();
        Topic receivedTopic = (Topic) intent.getSerializableExtra("topic_object_quizzes");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic")
                .document(receivedTopic.getTopicId())
                .collection("vocabulary");

        topicCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(QuizzesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    return;
                }
                vocabularies.clear();
                assert value != null;
                for (QueryDocumentSnapshot doc : value) {
                    Vocabulary vocabulary = doc.toObject(Vocabulary.class);
                    vocabularies.add(vocabulary);
                }
                createVietQuestionsFromVocabularies(vocabularies);

                initListener();


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
        Topic receivedTopic = (Topic) intent1.getSerializableExtra("topic_object_quizzes");

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

                        Ranking ranking = new Ranking(documentRef.getId(),receivedTopic.getOldTopicId() ,receivedTopic.getTopicId(), user.getUid(), user.getEmail(), formattedTime, joinTime, ranking1.getTimeStudied() + 1, score);
                        // Nếu tài liệu đã tồn tại, sử dụng update()
                        documentRef.update(ranking.toMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(QuizzesActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                                        handler.removeCallbacks(updateTimeRunnable);

                                        Intent intent_to_edit = new Intent(QuizzesActivity.this, ScoreActivity.class);
                                        intent_to_edit.putExtra("to_ranking_quizzes", ranking);
                                        intent_to_edit.putExtra("left_quizzes", left);
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
                        Ranking ranking = new Ranking(documentRef.getId(), receivedTopic.getOldTopicId(), receivedTopic.getTopicId(), user.getUid(), user.getEmail(), formattedTime, joinTime, 1, score);
                        // Nếu tài liệu không tồn tại, sử dụng set()
                        documentRef.set(ranking.toMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(QuizzesActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                                        handler.removeCallbacks(updateTimeRunnable);

                                        Intent intent_to_edit = new Intent(QuizzesActivity.this, ScoreActivity.class);
                                        intent_to_edit.putExtra("to_ranking_quizzes", ranking);
                                        intent_to_edit.putExtra("left_quizzes", left);
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
}
