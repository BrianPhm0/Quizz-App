package com.example.afinal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.R;
import com.example.afinal.objects.Topic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicFolderAdapter extends RecyclerView.Adapter<TopicFolderAdapter.TopicFolderViewHolder>{


    List<Topic> topicList;
    Context context;
    private Set<Integer> selectedPositions = new HashSet<>();


    public Set<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    TopicFolderAdapter.IClickTopicListener iClickTopicListener;
    boolean checkVal = false;

    public interface IClickTopicListener{
        void onClickTopicItem(Topic topic);

    }

    public TopicFolderAdapter(List<Topic> topicList, Context context, IClickTopicListener iClickTopicListener) {
        this.topicList = topicList;
        this.context = context;
        this.iClickTopicListener = iClickTopicListener;
    }

    @NonNull
    @Override
    public TopicFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_item_folder, parent, false);

        return new TopicFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicFolderViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        if(topic == null){
            return;
        }
        holder.topicName.setText(topic.getTopicName());

        holder.userName.setText(topic.getUserName());

        boolean isSelected = selectedPositions.contains(position);

        holder.check.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (isSelected) {
                    selectedPositions.remove(adapterPosition);
                } else {
                    selectedPositions.add(adapterPosition);
                }
                notifyItemChanged(adapterPosition);


            }
        });
    }

    @Override
    public int getItemCount() {
        if(topicList != null){
            return topicList.size();
        }
        return 0;
    }

    public static class TopicFolderViewHolder extends RecyclerView.ViewHolder {

        EditText topicName;
        TextView userName;
        LinearLayout linear;
        CardView cardView;

        ImageView check;
        public TopicFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.topic_card_folder);
            userName = itemView.findViewById(R.id.term_count_folder);
            topicName = itemView.findViewById(R.id.topic_name_item_folder);
            linear = itemView.findViewById(R.id.topic_linear_folder);
            check = itemView.findViewById(R.id.check_folder);
            check.setVisibility(itemView.GONE);
        }
    }

}
