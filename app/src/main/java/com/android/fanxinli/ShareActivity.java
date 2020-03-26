package com.android.fanxinli;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ShareActivity extends AppCompatActivity {

    private GridView mShareGridView;
    private ImageView mCancleImg;
    private ConstraintLayout mShareImgLayout;
    private ImageView mShareQRcode;
    private TextView mShareTitle;
    private TextView mShareChildTitle;
    private TextView mShareSlogan;
    private LinearLayout mSharePlayLayout;

    private int mShareGridImg[] = {R.drawable.wechat_friend,R.drawable.wechat_friend,R.drawable.weibo,R.drawable.qqzone,R.drawable.qq,R.drawable.download_pic};
    private String mShareGridName[] = {"朋友圈","微信好友","微博","QQ空间","QQ","下载图片"};

    private List<Map<String, Object>> data_list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);

        mCancleImg = findViewById(R.id.share_cancle);
        mShareImgLayout = findViewById(R.id.share_img_layout);
        mShareTitle = findViewById(R.id.share_img_title);
        mShareChildTitle = findViewById(R.id.share_img_child_name);
        mSharePlayLayout = findViewById(R.id.share_img_play_layout);
        mShareSlogan = findViewById(R.id.share_img_bottom_layout_slogan);
        mShareQRcode = findViewById(R.id.share_img_bottom_layout_qrcode);

        mCancleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initShareGrid();

    }

    private void initShareGrid(){
        mShareGridView = findViewById(R.id.share_img_grid);
        String [] from ={"image","text"};
        int [] to = {R.id.image,R.id.text};

        for(int i = 0; i< mShareGridImg.length; i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", mShareGridImg[i]);
            map.put("text", mShareGridName[i]);
            data_list.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data_list, R.layout.share_gridview_item_layout, from, to);
        //配置适配器
        mShareGridView.setAdapter(simpleAdapter);

        mShareGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0://朋友圈

                        break;
                    case 1://微信好友
                        ShareManager.getInstance(ShareActivity.this).shareImageToWx(ShareActivity.this, Utils.layoutToImage(mShareImgLayout,(int) Utils.dip2px(ShareActivity.this,390),(int) Utils.dip2px(ShareActivity.this,310)), 0);
                        break;
                }
            }
        });
    }
}
