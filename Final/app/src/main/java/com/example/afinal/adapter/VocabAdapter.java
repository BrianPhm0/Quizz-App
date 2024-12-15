package com.example.afinal.adapter;

import android.content.Context;
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

import java.util.List;

public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.VocabViewHolder>  {


    List<Vocabulary> mLisVocab;

    VocabAdapter.IClickVocabListener IClickVocabListener;

    Context context;

    public interface IClickVocabListener{
        void onClickVocabItem(Vocabulary vocabulary);
        void onClickVocabDelete(Vocabulary vocabulary);

    }

    public VocabAdapter(List<Vocabulary> mLisVocab, Context context, VocabAdapter.IClickVocabListener IClickVocabListener) {
        this.mLisVocab = mLisVocab;
        this.IClickVocabListener = IClickVocabListener;
        this.context = context;
    }

    @NonNull
    @Override
    public VocabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vocab_item, parent, false);

        return new VocabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabViewHolder holder, int position) {
        Vocabulary vocabulary = mLisVocab.get(position);
        if(vocabulary == null){
            return;
        }

        holder.term.setText(vocabulary.getVocabWord());
        holder.definition.setText(vocabulary.getVocabMeaning());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IClickVocabListener.onClickVocabDelete(vocabulary);
            }
        });
        holder.change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IClickVocabListener.onClickVocabItem(vocabulary);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mLisVocab != null){
            return mLisVocab.size();
        }
        return 0;
    }

    public static class VocabViewHolder extends RecyclerView.ViewHolder{

        EditText term, definition;
        ImageView change, delete;

        CardView cardView;

        public VocabViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.vocab_card);
            term = itemView.findViewById(R.id.text_term);
            definition = itemView.findViewById(R.id.text_definition);
            change = itemView.findViewById(R.id.change_vocab);
            delete = itemView.findViewById(R.id.delete_vocab);
        }
    }
}
