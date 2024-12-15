package com.example.afinal.request2;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.adapter.TopicAdapter;

public class RecyclerViewTouch extends ItemTouchHelper.SimpleCallback {
    private ItemTouchListener listener;
    public RecyclerViewTouch(int dragDirs, int swipeDirs,ItemTouchListener listener ) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener != null){
            listener.onSwiped(viewHolder);
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {

        if(viewHolder!= null){
            View foregroundView = ((TopicAdapter.TopicViewHolder) viewHolder).linear;
            getDefaultUIUtil().onSelected(foregroundView);
        }

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = ((TopicAdapter.TopicViewHolder) viewHolder).linear;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX,dY, actionState,isCurrentlyActive);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = ((TopicAdapter.TopicViewHolder) viewHolder).linear;
        getDefaultUIUtil().onDraw(c,recyclerView,foregroundView, dX, dY, actionState,isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foregroundView = ((TopicAdapter.TopicViewHolder) viewHolder).linear;
        getDefaultUIUtil().clearView(foregroundView);
    }
}
