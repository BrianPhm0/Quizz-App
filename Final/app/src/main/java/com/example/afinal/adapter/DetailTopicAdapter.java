package com.example.afinal.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.R;
import com.example.afinal.objects.Vocabulary;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Locale;


public class DetailTopicAdapter extends RecyclerView.Adapter<DetailTopicAdapter.DetailTopicViewHolder> {

    List<Vocabulary> vocabularyList;

    TextToSpeech textToSpeech;

    Locale locale;

    Context context;
    boolean check;

    DetailTopicAdapter.IClickVocabListener iClickVocabListener;

    public DetailTopicAdapter(List<Vocabulary> vocabularyList, Context context, IClickVocabListener iClickVocabListener) {
        this.vocabularyList = vocabularyList;
        this.context = context;
        this.iClickVocabListener = iClickVocabListener;

        locale = new Locale("en", "US");
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(locale);
            }
        });
    }


    @NonNull
    @Override
    public DetailTopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vocab_detail_item, parent, false);

        return new DetailTopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailTopicViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        if(vocabulary == null){
            return;
        }

        holder.term.setText(vocabulary.getVocabWord());
        holder.definition.setText(vocabulary.getVocabMeaning());
        if(vocabulary.isStar()){
            holder.star.setVisibility(View.GONE);
            holder.checked_star.setVisibility(View.VISIBLE);
        }
        else{
            holder.star.setVisibility(View.VISIBLE);
            holder.checked_star.setVisibility(View.GONE);
        }

        holder.star.setOnClickListener(view -> {
            check = true;
            holder.star.setVisibility(View.GONE);
            holder.checked_star.setVisibility(View.VISIBLE);

            if (iClickVocabListener != null) {
                iClickVocabListener.onClickStar(vocabulary, check);
            }
        });
        holder.checked_star.setOnClickListener(view -> {
            check = false;
            holder.star.setVisibility(View.VISIBLE);
            holder.checked_star.setVisibility(View.GONE);
            if (iClickVocabListener != null) {
                iClickVocabListener.onClickStar(vocabulary,check);
            }
        });

        holder.sound.setOnClickListener(view -> {
            if (iClickVocabListener != null) {
                iClickVocabListener.onClickVocabSound(vocabulary);
            }

            speak(vocabulary.getVocabWord(), vocabulary.getVocabMeaning());
        });

    }
    private void speak(String term, String definition) {
        if (textToSpeech != null) {
            textToSpeech.setLanguage(locale);
            textToSpeech.speak(term, TextToSpeech.QUEUE_ADD, null, null);
            textToSpeech.setLanguage(new Locale("vi", "VN"));
            textToSpeech.speak(definition, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    @Override
    public int getItemCount() {
        if(vocabularyList != null){
            return vocabularyList.size();
        }
        return 0;
    }

    public interface IClickVocabListener{

        void onClickVocabSound(Vocabulary vocabulary);

        void onClickStar(Vocabulary vocabulary, boolean check);
    }


    public static class DetailTopicViewHolder extends RecyclerView.ViewHolder {

        TextView term, definition;
        ImageView sound, star, checked_star;
        CardView cardView;
        public DetailTopicViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.vocab_detail_card);
            term = itemView.findViewById(R.id.text_term_detail);
            definition = itemView.findViewById(R.id.text_definition_detail);
            sound = itemView.findViewById(R.id.sound_vocab_detail);
            star = itemView.findViewById(R.id.star_vocab_detail);
            checked_star = itemView.findViewById(R.id.star2_vocab_detail);



        }
    }


}
