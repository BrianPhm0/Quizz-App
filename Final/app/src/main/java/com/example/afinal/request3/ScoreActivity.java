package com.example.afinal.request3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afinal.R;
import com.example.afinal.objects.Ranking;
import com.example.afinal.objects.Topic;
import com.example.afinal.objects.Vocabulary;
import com.example.afinal.request2.DetailTopicActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    TextView know, still_learning,left, top_perform_user, top_perform_score, top_finish_user, top_finish_score, top_frequent, top_frequent_score;

    ImageView back;

    int total;

    List<Ranking> rankings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        initUi();

        initListener();

        initListenerGet();

    }
    void initUi(){
        know = findViewById(R.id.know);
        still_learning = findViewById(R.id.still_learning);
        left = findViewById(R.id.term_left);
        back = findViewById(R.id.close_score);
        top_perform_user = findViewById(R.id.top_perform_user);
        top_perform_score = findViewById(R.id.top_perform);

        top_frequent = findViewById(R.id.top_frequent_user);
        top_frequent_score = findViewById(R.id.top_frequent_score);

        top_finish_user = findViewById(R.id.top_100_user);
        top_finish_score = findViewById(R.id.top_100_score);
    }

    int getTotal(){
        total = Integer.parseInt(know.getText().toString()) + Integer.parseInt(still_learning.getText().toString());
        return total;
    }
    @SuppressLint("SetTextI18n")
    void initListener() {
        Intent intent = getIntent();

        Ranking ranking = (Ranking) intent.getSerializableExtra("to_ranking");
        Ranking quizRanking = (Ranking) intent.getSerializableExtra("to_ranking_quizzes");
        int quizzesLeft = intent.getIntExtra("left_quizzes", 0);

        int left = intent.getIntExtra("left", 0);

        if (ranking == null && quizRanking == null) {
            Ranking cardRanking = (Ranking) intent.getSerializableExtra("card_to_ranking");
            if (cardRanking != null) {
                ranking = cardRanking;
            }
        }

        if ((ranking != null && ranking.getScore() != 0) ||
                (quizRanking != null && quizRanking.getScore() != 0)) {
            know.setText("" + (ranking != null ? ranking.getScore() : quizRanking.getScore()));
        }

        if (left != 0 || quizzesLeft != 0) {
            still_learning.setText("" + (left != 0 ? left : quizzesLeft));
        }

        getTotal();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void initListenerGet(){


        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("to_ranking") && intent.hasExtra("left")) {
                Ranking ranking = (Ranking) intent.getSerializableExtra("to_ranking");
                int left = intent.getIntExtra("left", 0);
                assert ranking != null;
                getPerformUser(ranking);
                getFinishUser(ranking, getTotal());
                getFrequentUser(ranking);
            }
            if (intent.hasExtra("to_ranking_quizzes")&& intent.hasExtra("left_quizzes")) {
                Ranking ranking = (Ranking) intent.getSerializableExtra("to_ranking_quizzes");
                int left = intent.getIntExtra("left_quizzes", 0);
                assert ranking != null;
                getPerformUser(ranking);
                getFinishUser(ranking, getTotal());
                getFrequentUser(ranking);

            }

            if (intent.hasExtra("card_to_ranking")&& intent.hasExtra("card_left")) {
                Ranking ranking = (Ranking) intent.getSerializableExtra("card_to_ranking");
                int left = intent.getIntExtra("card_left", 0);
                assert ranking != null;
                getPerformUser(ranking);
                getFinishUser(ranking, getTotal());
                getFrequentUser(ranking);

            }
        }

    }
    void getPerformUser(Ranking ranking){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ranking")
                .whereEqualTo("oldRankTopicId",ranking.getOldRankTopic())
                .orderBy("score", Query.Direction.DESCENDING)
                .orderBy("joinTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {

                            return;
                        }
                        rankings.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Ranking ranking1 = doc.toObject(Ranking.class);
                            rankings.add(ranking1);
                        }
                        int score = rankings.get(0).getScore();
                        top_perform_score.setText(String.format("%d", score));
                        top_perform_user.setText(rankings.get(0).getUserName());

                    }
                });

    }

    void getFinishUser(Ranking ranking, int total){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ranking")
                .whereEqualTo("oldRankTopicId",ranking.getOldRankTopic())
                .whereEqualTo("score", total)
                .orderBy("times")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
//                                    Toast.makeText(ScoreActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        rankings.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Ranking ranking1 = doc.toObject(Ranking.class);
                            rankings.add(ranking1);
                        }

                        top_finish_score.setText(rankings.get(0).getTimes());
                        top_finish_user.setText(rankings.get(0).getUserName());
                    }
                });

    }

    void getFrequentUser(Ranking ranking){


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ranking")
                .whereEqualTo("oldRankTopicId",ranking.getOldRankTopic())
                .orderBy("timeStudied",Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
//                                    Toast.makeText(ScoreActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        rankings.clear();
                        assert value != null;
                        for (QueryDocumentSnapshot doc : value) {
                            Ranking ranking1 = doc.toObject(Ranking.class);
                            rankings.add(ranking1);
                        }

                        top_frequent_score.setText(""+rankings.get(0).getTimeStudied());
                        top_frequent.setText(rankings.get(0).getUserName());
                    }
                });
    }


}