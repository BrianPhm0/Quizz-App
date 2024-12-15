package com.example.afinal.adapter;

import android.content.Context;
import android.icu.text.CaseMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.R;
import com.example.afinal.objects.Folder;
import com.example.afinal.objects.Vocabulary;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder>{

    List<Folder> folderList;

    Context context;

    FolderAdapter.IClickFolderListener iClickFolderListener;

    public FolderAdapter(List<Folder> folderList, Context context, IClickFolderListener iClickFolderListener) {
        this.folderList = folderList;
        this.context = context;
        this.iClickFolderListener = iClickFolderListener;
    }

    public interface IClickFolderListener{
        void onClickFolderItem(Folder folder);

    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_item, parent, false);

        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);

        if(folder == null){
            return;
        }
        holder.folderName.setText(folder.getFolderName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickFolderListener.onClickFolderItem(folder);
            }
        });
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickFolderListener.onClickFolderItem(folder);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(folderList != null){
            return folderList.size();
        }
        return 0;
    }
    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        CardView cardView;

        LinearLayout linear;
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            folderName = itemView.findViewById(R.id.folder_name_item);
            cardView = itemView.findViewById(R.id.folder_card);
            linear = itemView.findViewById(R.id.folder_linear);

        }
    }
}
