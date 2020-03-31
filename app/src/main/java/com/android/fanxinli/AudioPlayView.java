package com.android.fanxinli;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AudioPlayView extends Dialog implements  View.OnClickListener, ClassInfoLrcView.MedCallBack {

    private TextView mClassInfoTitle;
    private TextView mClassInfoChildTitle;
    private TextView mClassInfoTimerNumber;
    private static TextView mTimer_close;
    private static TextView mtimer_cancle;
    private static TextView mtimer_10;
    private static TextView mtimer_15;
    private static TextView mtimer_30;
    private static TextView mtimer_60;
    private ImageView mClassInfoBackView;
    private ImageView mClassInfoSettingView;
    private ImageView mClassInfoTimer;
    private ImageView mClassInfoCollection;
    private ImageView mClassInfoShare;
    private ImageView mClassInfoShock;
    private static ImageView mClassInfoPlay;
    private ImageView mClassInfoBackTo15;
    private ImageView mClassInfoForwardTo15;
    private ImageView mClassInfoSubtitle;
    private static ImageView mClassInfoBGimg;
    private static ClassInfoLrcView mClassInfoLrcView;
    private ConstraintLayout mClassInfoLayout;

    private static SeekBar mClassInfoPlaySeekbar;
    private static TextView mClassInfoAlreadyPlayedTime;
    private static TextView mClassInfoTotalTime;
    private static AudioPlayView dialog;

    private static BottomSheetDialog mTimerDialog;
    private CountDownTimer mTimerCountDown;

    private static Context mContext;

    private static ChildClassInfo mChildClassInfo;
    private static MediaPlayer mMediaPlayer;
    private static MediaPlayer mBackgroundMediaPlayer;
    private int mCurrentPlayPosition = -1;
    public static int AUDIO_PLAY_BACK_FORWARD_NUMBER = 15000;

    private LrcRows lrcRows=new LrcRows();

    public AudioPlayView(Context context){
        super(context);
    }

    public static final void show(Context context, ChildClassInfo childClassInfo, MediaPlayer mediaPlayer,MediaPlayer backgroundMediaPlayer) {
        mChildClassInfo = childClassInfo;
        mMediaPlayer = mediaPlayer;
        mBackgroundMediaPlayer = backgroundMediaPlayer;
        mContext = context;
        if(dialog == null){
            dialog = new AudioPlayView(context);
        }
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.GRAY);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.getDecorView().setBackgroundColor(Color.WHITE);
        window.setAttributes(layoutParams);
        window.setWindowAnimations(R.style.Dialog_Anim_Style);

        int duration2 = mMediaPlayer.getDuration() / 1000;
        int position = mMediaPlayer.getCurrentPosition()/1000;
        mClassInfoAlreadyPlayedTime.setText(Utils.calculateTime(position));
        mClassInfoTotalTime.setText(Utils.calculateTime(duration2));
        mClassInfoPlaySeekbar.setMax(duration2);
    }

    public static void notifyPlayStatus(boolean isPlay){
        if(isPlay){
            mClassInfoPlay.setBackgroundResource(R.drawable.play1);
        }else{
            mClassInfoPlay.setBackgroundResource(R.drawable.pause1);
        }
    }

    public static void notifyUI(ChildClassInfo childClassInfo, MediaPlayer mediaPlayer){
        Log.i("guochunhong","AudioPlayView notifyUI....... ");
        mChildClassInfo = childClassInfo;
        mMediaPlayer = mediaPlayer;
    }

    public static void notifySeekbarDataAndUI(long progress){
        // 将SeekBar位置设置到当前播放位置
        mClassInfoPlaySeekbar.setProgress((int)(progress/1000));
        //获得音乐的当前播放时间
        mClassInfoAlreadyPlayedTime.setText(Utils.calculateTime((int)(progress/1000)));
        mClassInfoLrcView.LrcToPlayer(progress);//根据播放的进度，时时跟新歌词
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_child_content_view);
        initView();
        setBgImageAnimation();
    }

    private void initView(){
        mClassInfoBackView = findViewById(R.id.class_info_back);
        mClassInfoSettingView = findViewById(R.id.class_info_setting);
        mClassInfoTitle = findViewById(R.id.class_info_name);
        mClassInfoChildTitle = findViewById(R.id.class_info_child_name);
        mClassInfoTimer = findViewById(R.id.class_info_timer);
        mClassInfoTimerNumber = findViewById(R.id.class_info_time_number);
        mClassInfoCollection = findViewById(R.id.class_info_collection);
        mClassInfoShare = findViewById(R.id.class_info_share);
        mClassInfoShock = findViewById(R.id.class_info_shock);
        mClassInfoPlay = findViewById(R.id.class_info_play);
        mClassInfoBackTo15 = findViewById(R.id.class_info_back_15);
        mClassInfoForwardTo15 = findViewById(R.id.class_info_forward_15);
        mClassInfoSubtitle = findViewById(R.id.class_info_subtitle);
        mClassInfoPlaySeekbar = findViewById(R.id.class_info_play_seekbar);
        mClassInfoAlreadyPlayedTime = findViewById(R.id.class_info_already_played_time);
        mClassInfoTotalTime = findViewById(R.id.class_info_total_play_time);
        mClassInfoLrcView = findViewById(R.id.class_info_lyric_show);
        mClassInfoBGimg = findViewById(R.id.class_info_bg_img);
        mClassInfoLayout = findViewById(R.id.class_info_layout);

        try {
            InputStream inputStream=mContext.getResources().getAssets().open("shaonian.lrc");
            List<LrcRow>list=lrcRows.BuildList(inputStream);
            mClassInfoLrcView.setLrc(list);
            mClassInfoLrcView.setCall(this);
        }catch (IOException E){

        }

        mClassInfoTitle.setText(mChildClassInfo.getTitle());
        mClassInfoChildTitle.setText(mChildClassInfo.getName());

        mClassInfoBackView.setOnClickListener(this);
        mClassInfoSettingView.setOnClickListener(this);
        mClassInfoTimer.setOnClickListener(this);
        mClassInfoCollection.setOnClickListener(this);
        mClassInfoShare.setOnClickListener(this);
        mClassInfoShock.setOnClickListener(this);
        mClassInfoPlay.setOnClickListener(this);
        mClassInfoBackTo15.setOnClickListener(this);
        mClassInfoForwardTo15.setOnClickListener(this);
        mClassInfoSubtitle.setOnClickListener(this);
        mClassInfoLayout.setOnClickListener(this);
        mClassInfoBGimg.setOnClickListener(this);

        //监听进度条拖动位置
        mClassInfoPlaySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int duration2 = mMediaPlayer.getDuration() / 1000;
                int position = mMediaPlayer.getCurrentPosition();
                mClassInfoAlreadyPlayedTime.setText(Utils.calculateTime(position / 1000));
                mClassInfoTotalTime.setText(Utils.calculateTime(duration2));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress()*1000);//在当前位置播放
                mBackgroundMediaPlayer.seekTo(seekBar.getProgress()*1000);
                mClassInfoAlreadyPlayedTime.setText(Utils.calculateTime(mMediaPlayer.getCurrentPosition()/1000));
                mClassInfoPlay.setBackgroundResource(R.drawable.play1);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("guochunhong","AudioPlayView onCompletion....... ");
                EventBus.getDefault().post(new AudioPlayViewPlayCompleteEvent());
                AudioPlayFragment.mIsThreadRunning = false;
                if(isShowing()){
                    mMediaPlayer.stop();
                    mBackgroundMediaPlayer.stop();
                    mClassInfoPlay.setBackgroundResource(R.drawable.pause1);
                }
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.i("guochunhong","AudioPlayView onError....... ");
                return true;
            }
        });
        mBackgroundMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        initTimer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.class_info_back:
                dismiss();
                break;
            case R.id.class_info_setting:
                mContext.startActivity(new Intent(mContext,SettingActivity.class));
                break;
            case R.id.class_info_timer:
                mTimerDialog.show();
                if(mClassInfoTimerNumber.getVisibility()==View.VISIBLE){
                    mTimer_close.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.class_info_collection:
                if(mChildClassInfo.isIs_collect()){
                    mClassInfoCollection.setBackground(mContext.getResources().getDrawable(R.drawable.collection1,null));
                }else {
                    mClassInfoCollection.setBackground(mContext.getResources().getDrawable(R.drawable.collection,null));
                }
                break;
            case R.id.class_info_share:
                mContext.startActivity(new Intent(mContext,ShareActivity.class));
                break;
            case R.id.class_info_shock:
//                Vibrator vibrator = (Vibrator)mContext.getSystemService(mContext.VIBRATOR_SERVICE);
//                vibrator.vibrate(1000);
                break;
            case R.id.class_info_play:
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                    mBackgroundMediaPlayer.pause();
                    mCurrentPlayPosition = mMediaPlayer.getCurrentPosition();
                    mClassInfoPlay.setBackgroundResource(R.drawable.pause1);
                    if(AudioPlayFragment.mIsThreadRunning){
                        AudioPlayFragment.mIsThreadRunning = false;
                    }
                }else{
                    mMediaPlayer.seekTo(mCurrentPlayPosition);
                    mMediaPlayer.start();
                    mBackgroundMediaPlayer.start();
                    mClassInfoPlay.setBackgroundResource(R.drawable.play1);
                    if(!AudioPlayFragment.mIsThreadRunning){
                        AudioPlayFragment.mIsThreadRunning = true;
                        new Thread(AudioPlayFragment.mPlayerRunnable).start();
                    }
                }
                break;
            case R.id.class_info_back_15:
                mCurrentPlayPosition = mMediaPlayer.getCurrentPosition();
                if(mCurrentPlayPosition-AUDIO_PLAY_BACK_FORWARD_NUMBER > 0){
                    mMediaPlayer.seekTo(mCurrentPlayPosition-AUDIO_PLAY_BACK_FORWARD_NUMBER);
                    mBackgroundMediaPlayer.seekTo(mCurrentPlayPosition-AUDIO_PLAY_BACK_FORWARD_NUMBER);
                }else{
                    mMediaPlayer.seekTo(0);
                    mBackgroundMediaPlayer.seekTo(0);
                }
                break;
            case R.id.class_info_forward_15:
                mCurrentPlayPosition = mMediaPlayer.getCurrentPosition();
                if(mCurrentPlayPosition+AUDIO_PLAY_BACK_FORWARD_NUMBER < mMediaPlayer.getDuration()){
                    mMediaPlayer.seekTo(mCurrentPlayPosition+AUDIO_PLAY_BACK_FORWARD_NUMBER);
                    mBackgroundMediaPlayer.seekTo(mCurrentPlayPosition+AUDIO_PLAY_BACK_FORWARD_NUMBER);
                }else{
                    mMediaPlayer.seekTo(mMediaPlayer.getDuration());
                    mMediaPlayer.pause();
                    mBackgroundMediaPlayer.seekTo(mBackgroundMediaPlayer.getDuration());
                    mBackgroundMediaPlayer.pause();
                    mClassInfoPlay.setBackgroundResource(R.drawable.pause1);
                }
                break;
            case R.id.class_info_subtitle:
                if(mClassInfoLrcView.getVisibility() == View.VISIBLE){
                    mClassInfoLrcView.setVisibility(View.GONE);
                }else{
                    mClassInfoLrcView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.class_info_layout:
                mClassInfoLayout.setVisibility(View.GONE);
                break;
            case R.id.class_info_bg_img:
                mClassInfoLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void call(long time) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo((int) time);
        }
        if (mBackgroundMediaPlayer.isPlaying()) {
            mBackgroundMediaPlayer.seekTo((int) time);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void initTimer(){
        if(mTimerDialog == null){
            View view = LayoutInflater.from(mContext).inflate(R.layout.timer_layout, null);
            mTimerDialog = new BottomSheetDialog(mContext);
            mTimerDialog.setContentView(view);
            mtimer_10 = mTimerDialog.findViewById(R.id.timer_10);
            mtimer_15 = mTimerDialog.findViewById(R.id.timer_15);
            mtimer_30 = mTimerDialog.findViewById(R.id.timer_30);
            mtimer_60 = mTimerDialog.findViewById(R.id.timer_60);
            mTimer_close = mTimerDialog.findViewById(R.id.timer_close);
            mTimer_close.setVisibility(View.GONE);
            mtimer_cancle = mTimerDialog.findViewById(R.id.timer_cancle);
        }
        mTimer_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimerDialog.dismiss();
                if(mTimerCountDown != null){
                    mTimerCountDown.cancel();
                    mTimerCountDown = null;
                }
                mClassInfoTimerNumber.setVisibility(View.GONE);
                mTimer_close.setVisibility(View.GONE);
            }
        });
        mtimer_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimerDialog.dismiss();
            }
        });
        mtimer_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimerDialog.dismiss();
                if(mTimerCountDown != null){
                    mTimerCountDown.cancel();
                    mTimerCountDown = null;
                }
                initCountDownTimer(10*60*1000);
            }
        });
        mtimer_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimerDialog.dismiss();
                if(mTimerCountDown != null){
                    mTimerCountDown.cancel();
                    mTimerCountDown = null;
                }
                initCountDownTimer(15*60*1000);
            }
        });
        mtimer_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimerDialog.dismiss();
                if(mTimerCountDown != null){
                    mTimerCountDown.cancel();
                    mTimerCountDown = null;
                }
                initCountDownTimer(30*60*1000);
            }
        });
        mtimer_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimerDialog.dismiss();
                if(mTimerCountDown != null){
                    mTimerCountDown.cancel();
                    mTimerCountDown = null;
                }
                initCountDownTimer(60*60*1000);
            }
        });
    }

    private void initCountDownTimer(long totalTime){
        mTimerCountDown = new CountDownTimer(totalTime,1000) {
            @Override
            public void onTick(long l) {
                if(mClassInfoTimerNumber.getVisibility() != View.VISIBLE){
                    mClassInfoTimerNumber.setVisibility(View.VISIBLE);
                }
                mClassInfoTimerNumber.setText(Utils.getToTimems(l));
            }

            @Override
            public void onFinish() {
                mClassInfoTimerNumber.setVisibility(View.GONE);
                mMediaPlayer.pause();
                mBackgroundMediaPlayer.pause();
            }
        };
        mTimerCountDown.start();
    }

    private static void setBgImageAnimation() {

        //表示从1f --> 1.13f 的变化过程
        ObjectAnimator animatorLargeX = ObjectAnimator.ofFloat(mClassInfoBGimg, "scaleX", 1f, 1.8f);
        animatorLargeX.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator animatorLargeY = ObjectAnimator.ofFloat(mClassInfoBGimg, "scaleY", 1f, 1.8f);
        animatorLargeY.setRepeatCount(ValueAnimator.INFINITE);
        //表示多个动画的协同工作
        final AnimatorSet setLarge = new AnimatorSet();
        setLarge.setDuration(20000).play(animatorLargeX).with(animatorLargeY);
        //对动画的监听,动画结束后立马跳转到主页面上
        setLarge.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final View largeImage = mClassInfoBGimg;
                int largeImageViewWidth = largeImage.getMeasuredWidth();
                int largeImageViewHeight = largeImage.getMeasuredHeight();
                ViewGroup.LayoutParams lp = mClassInfoBGimg.getLayoutParams();
                lp.width = (int) (largeImageViewWidth + 300);
                lp.height = (int) (largeImageViewHeight * ((largeImageViewWidth + 300) / largeImageViewWidth));
                ((ViewGroup.MarginLayoutParams) lp).setMargins(-(lp.width - largeImageViewWidth) / 2, 0, 0, 0);
                largeImage.setLayoutParams(lp);

                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);//第一个参数开始的透明度，第二个参数结束的透明度
                alphaAnimation.setDuration(3000);//多长时间完成这个动作
                largeImage.startAnimation(alphaAnimation);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

//                mClassInfoBGimg.setAlpha(0f);
//                mClassInfoBGimg.setVisibility(View.VISIBLE);
//                mClassInfoBGimg.animate()
//                        .alpha(1f)
//                        .setDuration(2000)
//                        .setListener(null);
//                largeImage.animate()
//                        .alpha(0f)
//                        .setDuration(2000)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                largeImage.setVisibility(View.GONE);
//                            }
//                        });

//                mClassInfoBGimg.setScaleX(1f);
//                mClassInfoBGimg.setScaleY(1f);
            }
        });
        setLarge.start();

    }

}
