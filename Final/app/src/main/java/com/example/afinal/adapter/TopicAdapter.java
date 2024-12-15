package com.example.afinal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.R;
import com.example.afinal.objects.Folder;
import com.example.afinal.objects.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    List<Topic> topicList;

    Context context;


    TopicAdapter.IClickTopicListener iClickTopicListener;

    public interface IClickTopicListener{
        void onClickTopicItem(Topic topic);

        void onDeleteTopicItem(Topic topic);
    }

    public TopicAdapter(List<Topic> topicList, Context context, IClickTopicListener iClickTopicListener) {
        this.topicList = topicList;
        this.context = context;
        this.iClickTopicListener = iClickTopicListener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_item, parent, false);

        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        if(topic == null){
            return;
        }
        holder.topicName.setText(topic.getTopicName());

        holder.userName.setText(topic.getUserName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickTopicListener.onClickTopicItem(topic);
            }
        });
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickTopicListener.onClickTopicItem(topic);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickTopicListener.onDeleteTopicItem(topic);
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

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        EditText topicName;
        TextView userName;
        public LinearLayout linear;
        public FrameLayout cardView;

        ImageView delete;
        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.topic_card);
            userName = itemView.findViewById(R.id.term_count);
            topicName = itemView.findViewById(R.id.topic_name_item);
            linear = itemView.findViewById(R.id.topic_linear);
            delete = itemView.findViewById(R.id.image_delete);
        }
    }
}
