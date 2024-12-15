package com.example.afinal.request3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;

import com.example.afinal.R;
import com.example.afinal.objects.Vocabulary;

import java.util.ArrayList;

public class VocabularyAdapter extends ArrayAdapter<Vocabulary> {

    Context context;
    ArrayList<Vocabulary> vocabularyList;

    public VocabularyAdapter(Context context, ArrayList<Vocabulary> vocabularyList) {
        super(context, 0, vocabularyList);
        this.context = context;
        this.vocabularyList = vocabularyList;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.swipe_fling_item, parent, false);
        }

        Vocabulary currentVocabulary = getItem(position);

        TextView wordTextView = convertView.findViewById(R.id.wordTextView);
        TextView meaningTextView = convertView.findViewById(R.id.meaningTextView);

        if (currentVocabulary != null) {
            wordTextView.setText(currentVocabulary.getVocabWord());
            meaningTextView.setText(currentVocabulary.getVocabMeaning());

            // Declare a final array to hold convertView
            final View[] finalConvertView = {convertView};

// Set click listener to toggle between word and meaning
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the ViewFlipper
                    ViewFlipper viewFlipper = finalConvertView[0].findViewById(R.id.viewFlipper);

                    // Check the currently displayed child view
                    if (viewFlipper.getDisplayedChild() == 0) {

                        viewFlipper.setInAnimation(context, R.anim.fade_in);
                        viewFlipper.setOutAnimation(context, R.anim.fade_out);
                        viewFlipper.showNext();
                    } else {

                        viewFlipper.setInAnimation(context, R.anim.fade_in);
                        viewFlipper.setOutAnimation(context, R.anim.fade_out);
                        viewFlipper.showPrevious();
                    }
                }
            });

        }
        return convertView;
    }
}
