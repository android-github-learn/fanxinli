package com.android.fanxinli;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mian);


//        mSwipeBackLayout = getSwipeBackLayout();
//        setSwipeBackEnable(true);
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        //获取屏幕的宽度
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int  phoneWidth  = displayMetrics.widthPixels ;
        //设置侧滑的区域为屏幕宽度的1/3，如果不设置系统默认为50dip
//        mSwipeBackLayout.setEdgeSize( phoneWidth / 5 );

        AudioPlayFragment audioPlayFragment = new AudioPlayFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mian_layout, audioPlayFragment);
        transaction.commitAllowingStateLoss();

    }


}
