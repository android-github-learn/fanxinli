package com.android.fanxinli;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private List<ChildClassInfo> mChildClassInfoList;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private AssetFileDescriptor mAssetFileDescriptor;
    private String[] mMusics = {"shaonian.mp3","b.mp3","b.mp3","1.mp3"};
    private int current_play_position = -1;
    private boolean isClassPlayPause;
    private int mCurrentPlayPosition;

    public RecyclerViewAdapter(Context context,List<ChildClassInfo> classInfos){
        mChildClassInfoList = classInfos;
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        // 设置音量，参数分别表示左右声道声音大小，取值范围为0~1
        mMediaPlayer.setVolume(0.8f, 0.8f);
        // 设置是否循环播放
        mMediaPlayer.setLooping(false);
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
                try {
                    if(mMediaPlayer == null){
                        mMediaPlayer = new MediaPlayer();
                    }
                    childClassInfo.setPlayStatus(true);


//                    if(current_play_position == position){
//                        if(mMediaPlayer.isPlaying()){
//                            mMediaPlayer.pause();
//                            isClassPlayPause = true;
//                            mCurrentPlayPosition = mMediaPlayer.getCurrentPosition();
//                            viewHolder.play_status.setBackground(mContext.getResources().getDrawable(R.drawable.pause,null));
//                        }else {
//                            mMediaPlayer.seekTo(mCurrentPlayPosition);
//                            mMediaPlayer.start();
//                            viewHolder.play_status.setBackground(mContext.getResources().getDrawable(R.drawable.play,null));
//                        }
//                        return;
//                    }
//                    if(mMediaPlayer.isPlaying()){
//                        mMediaPlayer.reset();
//                    }
                    if(current_play_position == position){
                        AudioChildContentView.show(mContext,childClassInfo,mMediaPlayer);
                        return;
                    }
                    mMediaPlayer.reset();
                    mAssetFileDescriptor = mContext.getAssets().openFd(mMusics[position]);
                    mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
                    mMediaPlayer.prepareAsync();
//                    mMediaPlayer.start();
                    current_play_position = position;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                viewHolder.play_status.setBackground(mContext.getResources().getDrawable(R.drawable.play,null));
                mChildClassInfoList.get(current_play_position).setPlayStatus(true);
                AudioChildContentView.show(mContext,childClassInfo,mMediaPlayer);
            }
        });

        //播放完成
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                try {
//                    if(current_play_position > mChildClassInfoList.size() - 1){
//                        return;
//                    }
//                    mAssetFileDescriptor = mContext.getAssets().openFd(mMusics[current_play_position+1]);
//                    mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
//                    mMediaPlayer.prepareAsync();
////                    mMediaPlayer.start();
//                    current_play_position++;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mChildClassInfoList.size() != 0? mChildClassInfoList.size():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView play_status;
        private TextView class_name;
        private TextView class_time;
        private ProgressBar class_progress;
        private TextView class_progress_number;
        private ConstraintLayout class_constrainLayoutInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            play_status = itemView.findViewById(R.id.play_status);
            class_name =  itemView.findViewById(R.id.audioName);
            class_time =  itemView.findViewById(R.id.time);
            class_progress = itemView.findViewById(R.id.progressbar);
            class_progress_number = itemView.findViewById(R.id.progressbar_number);
            class_constrainLayoutInfo = itemView.findViewById(R.id.class_info);
        }
    }

    public void clearMediaPlayer(){
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
