package com.android.fanxinli;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private List<ChildClassInfo> mChildClassInfoList;
    private Context mContext;

    private ItemClickListener mItemClickListener;
    private CollectClickListener mCollectClickListener;

    public RecyclerViewAdapter(Context context,List<ChildClassInfo> classInfos){
        mChildClassInfoList = classInfos;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final ChildClassInfo childClassInfo = mChildClassInfoList.get(position);
        if(childClassInfo.isPlayStatus()){
            viewHolder.play_status.setBackground(mContext.getResources().getDrawable(R.drawable.play,null));
        }else{
            viewHolder.play_status.setBackground(mContext.getResources().getDrawable(R.drawable.pause,null));
        }
        viewHolder.class_name.setText(childClassInfo.getName());
        viewHolder.class_time.setText(String.valueOf(childClassInfo.getClassTimer()));
        viewHolder.class_progress_number.setText(childClassInfo.getPlayProgress()+"%");
        viewHolder.class_progress.setProgress(childClassInfo.getPlayProgress());

        viewHolder.class_constrainLayoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(position,viewHolder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChildClassInfoList.size() != 0? mChildClassInfoList.size():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView play_status;
        private TextView class_name;
        private TextView class_time;
        private ProgressBar class_progress;
        private TextView class_progress_number;
        private ConstraintLayout class_constrainLayoutInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            play_status = itemView.findViewById(R.id.class_play_status);
            class_name =  itemView.findViewById(R.id.class_audio_name);
            class_time =  itemView.findViewById(R.id.class_time_number);
            class_progress = itemView.findViewById(R.id.class_progressbar);
            class_progress_number = itemView.findViewById(R.id.class_progressbar_number);
            class_constrainLayoutInfo = itemView.findViewById(R.id.class_info_layout);
        }
    }

    public interface ItemClickListener {
        public void onItemClick(int postion,ViewHolder viewHolder);
    }

    public interface CollectClickListener {
        public void onCollectClick(int postion);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnCollectClickListener(CollectClickListener listener) {
        this.mCollectClickListener = listener;
    }
}
