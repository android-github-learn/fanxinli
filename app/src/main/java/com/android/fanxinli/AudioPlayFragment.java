package com.android.fanxinli;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    private RecyclerViewAdapter mAudioPlayRecyclerViewAdapter;
    private RecyclerViewAdapter.ViewHolder mAudioPlayAdapterViewHolder;
    private MediaPlayer mAudioPlayMediaPlayer;

    private List<ChildClassInfo> mChildClassInfoList = new ArrayList<>();
    private AssetFileDescriptor mAssetFileDescriptor;

    private AudioPlayClassInfo mAudioPlayClassInfo;

    private int current_play_position = -1;
    private String[] mMusics = {"shaonian.mp3","b.mp3","b.mp3","1.mp3"};

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

        return view;
    }

    private void initData(){
        ChildClassInfo childClassInfo1 = new ChildClassInfo();
        childClassInfo1.setTitle("爱自己冥想");
        childClassInfo1.setName("第一课时");
        childClassInfo1.setCollection(true);
        childClassInfo1.setClassTimer(13);
        childClassInfo1.setPlayProgress(36);
        childClassInfo1.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo1);

        ChildClassInfo childClassInfo2 = new ChildClassInfo();
        childClassInfo2.setTitle("爱自己冥想");
        childClassInfo2.setName("第二课时");
        childClassInfo2.setCollection(false);
        childClassInfo2.setClassTimer(12);
        childClassInfo2.setPlayProgress(45);
        childClassInfo2.setCollection(false);
        childClassInfo2.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo2);

        ChildClassInfo childClassInfo3 = new ChildClassInfo();
        childClassInfo3.setTitle("爱自己冥想");
        childClassInfo3.setName("第三课时");
        childClassInfo3.setCollection(true);
        childClassInfo3.setClassTimer(15);
        childClassInfo3.setPlayProgress(67);
        childClassInfo3.setCollection(false);
        childClassInfo3.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo3);

        ChildClassInfo childClassInfo4 = new ChildClassInfo();
        childClassInfo4.setTitle("爱自己冥想");
        childClassInfo4.setName("第四课时");
        childClassInfo4.setCollection(true);
        childClassInfo4.setClassTimer(15);
        childClassInfo4.setPlayProgress(27);
        childClassInfo4.setCollection(false);
        childClassInfo4.setPlayStatus(false);
        mChildClassInfoList.add(childClassInfo4);
    }

    private void initMediaPlayer(){
        mAudioPlayMediaPlayer = new MediaPlayer();
        // 设置音量，参数分别表示左右声道声音大小，取值范围为0~1
        mAudioPlayMediaPlayer.setVolume(0.5f, 0.5f);
        // 设置是否循环播放
//        mAudioPlayMediaPlayer.setLooping(true);

        mAudioPlayMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.i("guochunhong","onPrepared current_play_position :" + current_play_position);
                int position = current_play_position;
                //播放到列表中的最后一节，播放按钮显示从头开始播放第一节
                if(position==mChildClassInfoList.size()-1){
                    position = 1;
                    mAudioPlayView.setText("开始第"+position+"节");
                }else{
                    mAudioPlayView.setText("开始第"+(position+2)+"节");
                }

                mAudioPlayMediaPlayer.start();
//                mAudioPlayAdapterViewHolder.play_status.setBackground(getActivity().getResources().getDrawable(R.drawable.play,null));

                mAudioPlayRecyclerViewAdapter.notifyDataSetChanged();
                mChildClassInfoList.get(current_play_position).setPlayStatus(true);
            }
        });

        mAudioPlayMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("guochunhong","onCompletion current_play_position :" + current_play_position);
                mediaPlay();
            }
        });
        mAudioPlayMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                Log.i("guochunhong","setOnErrorListener  what:" + what+"    extra :" + extra);
                return false;
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
                    if(mAudioPlayMediaPlayer == null){
                        mAudioPlayMediaPlayer = new MediaPlayer();
                    }

                    if(current_play_position == position){
                        AudioPlayView.show(getActivity(),childClassInfo, mAudioPlayMediaPlayer);
                        return;
                    }
                    if(current_play_position != -1){
                        for (int i = 0;i<mChildClassInfoList.size();i++){
                            mChildClassInfoList.get(i).setPlayStatus(false);
                            mAudioPlayRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    mAudioPlayMediaPlayer.reset();
                    mAssetFileDescriptor = getActivity().getAssets().openFd(mMusics[position]);
                    mAudioPlayMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
                    mAudioPlayMediaPlayer.prepareAsync();
//                    mAudioPlayMediaPlayer.start();
                    current_play_position = position;
                    AudioPlayView.show(getActivity(),mChildClassInfoList.get(current_play_position), mAudioPlayMediaPlayer);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mAudioPlayRecyclerViewAdapter.setOnCollectClickListener(new RecyclerViewAdapter.CollectClickListener() {
            @Override
            public void onCollectClick(int postion) {

            }
        });
    }

    private void mediaPlay(){
        try {
            if(current_play_position != -1){
                for (int i = 0;i<mChildClassInfoList.size();i++){
                    mChildClassInfoList.get(i).setPlayStatus(false);
                    mAudioPlayRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
            if(current_play_position==mChildClassInfoList.size()-1){
                current_play_position = -1;
            }
            mAudioPlayMediaPlayer.reset();
            current_play_position ++;
            mAssetFileDescriptor = getActivity().getAssets().openFd(mMusics[current_play_position]);
            mAudioPlayMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
            mAudioPlayMediaPlayer.prepareAsync();
        }catch (IOException e){

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AudioPlayViewPlayCompleteEvent event) {
        mediaPlay();
        AudioPlayView.notifyUI(mChildClassInfoList.get(current_play_position),mAudioPlayMediaPlayer);
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
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
