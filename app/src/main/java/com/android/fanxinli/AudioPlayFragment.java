package com.android.fanxinli;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private RecyclerView mAudioPlayRecyclerView;
    private RecyclerViewAdapter mAudioPlayRecyclerViewAdapter;
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

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mAudioPlayRecyclerView.setLayoutManager(manager);


        initData();

        mAudioPlayRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), mChildClassInfoList);
        mAudioPlayRecyclerView.setAdapter(mAudioPlayRecyclerViewAdapter);

        initMediaPlayer();
        initClickListener();

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
        mAudioPlayMediaPlayer.setLooping(false);

        mAudioPlayMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
//                viewHolder.play_status.setBackground(getActivity().getResources().getDrawable(R.drawable.play,null));
                mChildClassInfoList.get(current_play_position).setPlayStatus(true);
                AudioPlayView.show(getActivity(),mChildClassInfoList.get(current_play_position), mAudioPlayMediaPlayer);
            }
        });
    }

    private void initClickListener(){
        mAudioPlayRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int position) {
                final ChildClassInfo childClassInfo = mChildClassInfoList.get(position);
                try {
                    if(mAudioPlayMediaPlayer == null){
                        mAudioPlayMediaPlayer = new MediaPlayer();
                    }

                    if(current_play_position == position){
                        AudioPlayView.show(getActivity(),childClassInfo, mAudioPlayMediaPlayer);
                        return;
                    }
                    mAudioPlayMediaPlayer.reset();
                    mAssetFileDescriptor = getActivity().getAssets().openFd(mMusics[position]);
                    mAudioPlayMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
                    mAudioPlayMediaPlayer.prepareAsync();
//                    mAudioPlayMediaPlayer.start();
                    current_play_position = position;

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
    }
}
