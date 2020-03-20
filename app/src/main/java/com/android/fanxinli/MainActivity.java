package com.android.fanxinli;

import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        final LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(manager);

        List<ChildClassInfo> childClassInfoList = new ArrayList<>();
        ChildClassInfo childClassInfo1 = new ChildClassInfo();
        childClassInfo1.setTitle("爱自己冥想");
        childClassInfo1.setName("第一课时");
        childClassInfo1.setCollection(true);
        childClassInfo1.setClassTimer(13);
        childClassInfo1.setPlayProgress(36);
        childClassInfo1.setPlayStatus(false);
        childClassInfoList.add(childClassInfo1);

        ChildClassInfo childClassInfo2 = new ChildClassInfo();
        childClassInfo2.setTitle("爱自己冥想");
        childClassInfo2.setName("第二课时");
        childClassInfo2.setCollection(false);
        childClassInfo2.setClassTimer(12);
        childClassInfo2.setPlayProgress(45);
        childClassInfo2.setCollection(false);
        childClassInfo2.setPlayStatus(false);
        childClassInfoList.add(childClassInfo2);

        ChildClassInfo childClassInfo3 = new ChildClassInfo();
        childClassInfo3.setTitle("爱自己冥想");
        childClassInfo3.setName("第三课时");
        childClassInfo3.setCollection(true);
        childClassInfo3.setClassTimer(15);
        childClassInfo3.setPlayProgress(67);
        childClassInfo3.setCollection(false);
        childClassInfo3.setPlayStatus(false);
        childClassInfoList.add(childClassInfo3);

        ChildClassInfo childClassInfo4 = new ChildClassInfo();
        childClassInfo4.setTitle("爱自己冥想");
        childClassInfo4.setName("第四课时");
        childClassInfo4.setCollection(true);
        childClassInfo4.setClassTimer(15);
        childClassInfo4.setPlayProgress(27);
        childClassInfo4.setCollection(false);
        childClassInfo4.setPlayStatus(false);
        childClassInfoList.add(childClassInfo4);

        mRecyclerViewAdapter = new RecyclerViewAdapter(this,childClassInfoList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRecyclerViewAdapter != null){
            mRecyclerViewAdapter.clearMediaPlayer();
        }
    }
}
