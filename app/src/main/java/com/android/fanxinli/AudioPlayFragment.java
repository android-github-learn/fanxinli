package com.android.fanxinli;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AudioPlayFragment extends Fragment {

    private ImageView mAudioPlayTopImg;
    private ImageView mAudioPlayBack;
    private ImageView mAudioPlayShare;
    private TextView mAudioPlayTitle;
    private TextView mAudioPlayClassHour;
    private TextView mAudioPlayPracticeNumber;
    private TextView mAudioPlayDescription;
    private ImageView mAudioPlayLecturerPhoto;
    private TextView mAudioPlayLecturerName;
    private TextView mAudioPlayLecturerIntroduction;
    private TextView mAudioPlayView;
    private RecyclerView mAudioPlayRecyclerView;
    private static RecyclerViewAdapter mAudioPlayRecyclerViewAdapter;
    private RecyclerViewAdapter.ViewHolder mAudioPlayAdapterViewHolder;
    private static MediaPlayer mAudioPlayMediaPlayer;
    private MediaPlayer mBackgroundMediaPlayer;

    private static List<ChildClassInfo> mChildClassInfoList = new ArrayList<>();
    private AssetFileDescriptor mAssetFileDescriptor;

    private AudioPlayClassInfo mAudioPlayClassInfo;
    public static Thread mThread;

    public static boolean mIsThreadRunning = true;
    private static int current_play_position = -1;
    private int current_play_complay_position = -1;
    private static long mCurrentMediaTotalTime;
    private String[] mMusics = {"shaonian.mp3","b.mp3","b.mp3","1.mp3","d.mp3"};

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("guochunhong","handleMessage ......"  + mAudioPlayMediaPlayer.isPlaying());
            if(mAudioPlayMediaPlayer == null || !mAudioPlayMediaPlayer.isPlaying()){
                return;
            }
            long currentPosition = msg.what;
            AudioPlayView.notifySeekbarDataAndUI(currentPosition);

            int progress = (int) ((double)currentPosition/mCurrentMediaTotalTime*100);
            Log.i("guochunhong","progress :" + progress + ",  currentPosition :" + currentPosition + ",  mCurrentMediaTotalTime :" + mCurrentMediaTotalTime);
            if(progress >= 99){
                progress=100;
            }
            //更新列表progressbar进度
            ChildClassInfo childClassInfo = mChildClassInfoList.get(current_play_position);
            childClassInfo.setProgress(progress);
            mAudioPlayRecyclerViewAdapter.notifyItemChanged(current_play_position,childClassInfo);
        }
    };

    class MuiscThread implements Runnable {

        @Override
        //实现run方法
        public void run() {
            //判断音乐的状态，在不停止与不暂停的情况下向总线程发出信息
            Log.i("guochunhong","MuiscThread run mIsThreadRunning :" + mIsThreadRunning);
            while (mAudioPlayMediaPlayer != null && mIsThreadRunning) {

                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发出的信息
                handler.sendEmptyMessage(mAudioPlayMediaPlayer.getCurrentPosition());
                Log.i("guochunhong","MuiscThread run222 mIsThreadRunning :" + mIsThreadRunning);
            }
        }
    }

    public static Runnable mPlayerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("guochunhong","mPlayerRunnable run mIsThreadRunning :" + mIsThreadRunning);
            while (mAudioPlayMediaPlayer != null && mIsThreadRunning) {

                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发出的信息
                handler.sendEmptyMessage(mAudioPlayMediaPlayer.getCurrentPosition());
                Log.i("guochunhong","mPlayerRunnable run222 mIsThreadRunning :" + mIsThreadRunning);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_play_fragment_layout,container,false);
        mAudioPlayTopImg = view.findViewById(R.id.audio_play_layout_img);
        mAudioPlayBack = view.findViewById(R.id.audio_play_layout_back);
        mAudioPlayShare = view.findViewById(R.id.audio_play_layout_share);
        mAudioPlayTitle = view.findViewById(R.id.audio_play_layout_name);
        mAudioPlayClassHour = view.findViewById(R.id.audio_play_layout_class_hour);
        mAudioPlayPracticeNumber = view.findViewById(R.id.audio_play_layout_practice_number);
        mAudioPlayDescription = view.findViewById(R.id.audio_play_layout_description);
        mAudioPlayLecturerPhoto = view.findViewById(R.id.audio_play_layout_lecturer_photo);
        mAudioPlayLecturerName = view.findViewById(R.id.audio_play_layout_lecturer_name);
        mAudioPlayLecturerIntroduction = view.findViewById(R.id.audio_play_layout_lecturer_introduction);
        mAudioPlayRecyclerView = view.findViewById(R.id.audio_play_layout_recyclerview);
        mAudioPlayView = view.findViewById(R.id.audio_play_layout_play);
        mAudioPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlay();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mAudioPlayRecyclerView.setLayoutManager(manager);


        initData();

        mAudioPlayRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), mChildClassInfoList);
        mAudioPlayRecyclerView.setAdapter(mAudioPlayRecyclerViewAdapter);

        initMediaPlayer();
        initClickListener();

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

//        mThread = new Thread(new MuiscThread());
//        mThread.start();
        new Thread(mPlayerRunnable).start();

        return view;
    }

    private void initData(){
        ChildClassInfo childClassInfo1 = new ChildClassInfo();
        childClassInfo1.setTitle("爱自己冥想");
        childClassInfo1.setName("第一课时");
        childClassInfo1.setIs_collect(true);
        childClassInfo1.setTime(13);
        childClassInfo1.setProgress(36);
        childClassInfo1.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo1);

        ChildClassInfo childClassInfo2 = new ChildClassInfo();
        childClassInfo2.setTitle("爱自己冥想");
        childClassInfo2.setName("第二课时");
        childClassInfo2.setIs_collect(false);
        childClassInfo2.setTime(12);
        childClassInfo2.setProgress(45);
        childClassInfo2.setIs_collect(false);
        childClassInfo2.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo2);

        ChildClassInfo childClassInfo3 = new ChildClassInfo();
        childClassInfo3.setTitle("爱自己冥想");
        childClassInfo3.setName("第三课时");
        childClassInfo3.setIs_collect(true);
        childClassInfo3.setTime(15);
        childClassInfo3.setProgress(67);
        childClassInfo3.setIs_collect(false);
        childClassInfo3.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo3);

        ChildClassInfo childClassInfo4 = new ChildClassInfo();
        childClassInfo4.setTitle("爱自己冥想");
        childClassInfo4.setName("第四课时");
        childClassInfo4.setIs_collect(true);
        childClassInfo4.setTime(15);
        childClassInfo4.setProgress(27);
        childClassInfo4.setIs_collect(false);
        childClassInfo4.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo4);
    }

    private void initMediaPlayer(){
        mAudioPlayMediaPlayer = new MediaPlayer();
        mBackgroundMediaPlayer= new MediaPlayer();
        // 设置音量，参数分别表示左右声道声音大小，取值范围为0~1
        mBackgroundMediaPlayer.setVolume(0.0f, 0.0f);
        mAudioPlayMediaPlayer.setVolume(0.5f, 0.5f);
        // 设置是否循环播放
//        mAudioPlayMediaPlayer.setLooping(true);

        mAudioPlayMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mCurrentMediaTotalTime = mAudioPlayMediaPlayer.getDuration();
                Log.i("guochunhong","onPrepared current_play_position :" + current_play_position);
                mAudioPlayMediaPlayer.start();
                Log.i("guochunhong","onPrepared mIsThreadRunning :" + mIsThreadRunning);
                if(!mIsThreadRunning){
                    mIsThreadRunning = true;
                    new Thread(mPlayerRunnable).start();
                }
                mAudioPlayRecyclerViewAdapter.notifyDataSetChanged();
                mChildClassInfoList.get(current_play_position).setPlayStatus(true);
                AudioPlayView.notifyPlayStatus(mAudioPlayMediaPlayer.isPlaying());
            }
        });

        mBackgroundMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mBackgroundMediaPlayer.start();
            }
        });

        mAudioPlayMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("guochunhong","onCompletion current_play_position :" + current_play_position);
                current_play_complay_position = current_play_position;
                //播放到列表中的最后一节，播放按钮显示从头开始播放第一节
                if(current_play_complay_position==mChildClassInfoList.size()-1){
                    current_play_complay_position = 1;
                    mAudioPlayView.setText("开始第"+current_play_complay_position+"节");
                }else{
                    mAudioPlayView.setText("开始第"+(current_play_complay_position+2)+"节");
                }
                mAudioPlayMediaPlayer.stop();
                mBackgroundMediaPlayer.stop();
                mIsThreadRunning = false;
            }
        });
        mAudioPlayMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                Log.i("guochunhong","setOnErrorListener  what:" + what+"    extra :" + extra);
                return true;
            }
        });
        mBackgroundMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
    }

    private void initClickListener(){
        mAudioPlayRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int position, RecyclerViewAdapter.ViewHolder viewHolder) {
                mAudioPlayAdapterViewHolder = viewHolder;
                final ChildClassInfo childClassInfo = mChildClassInfoList.get(position);
                try {
                    if(current_play_position == position){
                        AudioPlayView.show(getActivity(),childClassInfo, mAudioPlayMediaPlayer,mBackgroundMediaPlayer);
                        return;
                    }
                    if(current_play_position != -1){
                        for (int i = 0;i<mChildClassInfoList.size();i++){
                            mChildClassInfoList.get(i).setPlayStatus(false);
                            mAudioPlayRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    //背景音乐播放
                    mBackgroundMediaPlayer.reset();
                    AssetFileDescriptor assetFileDescriptor = getActivity().getAssets().openFd(mMusics[4]);
                    mBackgroundMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                    mBackgroundMediaPlayer.prepareAsync();

                    mAudioPlayMediaPlayer.reset();
                    mAssetFileDescriptor = getActivity().getAssets().openFd(mMusics[position]);
                    mAudioPlayMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
                    mAudioPlayMediaPlayer.prepareAsync();
                    current_play_position = position;
                    AudioPlayView.show(getActivity(),mChildClassInfoList.get(current_play_position), mAudioPlayMediaPlayer,mBackgroundMediaPlayer);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mAudioPlayRecyclerViewAdapter.setOnCollectClickListener(new RecyclerViewAdapter.CollectClickListener() {
            @Override
            public void onCollectClick(int position) {
                ChildClassInfo childClassInfo = mChildClassInfoList.get(position);
                if(childClassInfo.isIs_collect()){
                    childClassInfo.setIs_collect(false);
                }else {
                    childClassInfo.setIs_collect(true);
                }
                mAudioPlayRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        mAudioPlayShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ShareActivity.class));
            }
        });
    }

    private void mediaPlay(){
        try {
            if(current_play_complay_position == current_play_position){
                if(current_play_position != -1){
                    for (int i = 0;i<mChildClassInfoList.size();i++){
                        mChildClassInfoList.get(i).setPlayStatus(false);
                        mAudioPlayRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }
                if(current_play_position==mChildClassInfoList.size()-1){
                    current_play_position = -1;
                }
                //背景音乐播放
                mBackgroundMediaPlayer.reset();
                AssetFileDescriptor assetFileDescriptor = getActivity().getAssets().openFd(mMusics[4]);
                mBackgroundMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                mBackgroundMediaPlayer.prepareAsync();

                mAudioPlayMediaPlayer.reset();
                current_play_position ++;
                mAssetFileDescriptor = getActivity().getAssets().openFd(mMusics[current_play_position]);
                mAudioPlayMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
                mAudioPlayMediaPlayer.prepareAsync();
            }
            AudioPlayView.show(getActivity(),mChildClassInfoList.get(current_play_position), mAudioPlayMediaPlayer,mBackgroundMediaPlayer);
        }catch (IOException e){

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AudioPlayViewPlayCompleteEvent event) {
//        AudioPlayView.notifyUI(mChildClassInfoList.get(current_play_position),mAudioPlayMediaPlayer);
        current_play_complay_position = current_play_position;
        //播放到列表中的最后一节，播放按钮显示从头开始播放第一节
        if(current_play_complay_position==mChildClassInfoList.size()-1){
            current_play_complay_position = 1;
            mAudioPlayView.setText("列表播放完成");
        }else{
            mAudioPlayView.setText("开始第"+(current_play_complay_position+2)+"节");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NotifyBackgroundMediaPlayerVolumeEvent event){
        float volumeNumber = ((float)event.getVolumeNumber()/100);
        Log.i("guochunhong","NotifyBackgroundMediaPlayerVolumeEvent volumeNumber :"  + volumeNumber);
        mBackgroundMediaPlayer.setVolume(volumeNumber,volumeNumber);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAudioPlayMediaPlayer != null) {
            mAudioPlayMediaPlayer.stop();
            mAudioPlayMediaPlayer.release();
            mAudioPlayMediaPlayer = null;
        }
        if (mBackgroundMediaPlayer != null) {
            mBackgroundMediaPlayer.stop();
            mBackgroundMediaPlayer.release();
            mBackgroundMediaPlayer = null;
        }
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        if(mAudioPlayRecyclerViewAdapter != null){
            mAudioPlayRecyclerViewAdapter.clear();
        }
    }
}
