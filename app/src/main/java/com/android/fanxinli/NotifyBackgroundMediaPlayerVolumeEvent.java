package com.android.fanxinli;

public class NotifyBackgroundMediaPlayerVolumeEvent {

    public int mVolumeNumber;

    public NotifyBackgroundMediaPlayerVolumeEvent(int mVolumeNumber){
        this.mVolumeNumber = mVolumeNumber;
    }

    public int getVolumeNumber(){
        return mVolumeNumber;
    }
}
