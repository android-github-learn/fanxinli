package com.android.fanxinli;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LrcRows {

    private List<LrcRow> list = new ArrayList<LrcRow>();//存放每行歌词的集合

    //获取list集合的方法，将每行的歌词添加到list集合中
    public List<LrcRow> BuildList(InputStream inputStream) {

        //获取assets的管理器
//        AssetManager assetManager = context.getAssets();
        //打开assets下的指定文件，获取输入流
        try {
//            InputStream inputStream = assetManager.open("shaonian.lrc");
            //将字节输入流转化为字符流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"GB2312"));
//            Log.i("guochunhong","LrcRows BuildList bufferedReader :" + bufferedReader + "   inputStream : " + inputStream);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                LrcRow lrcRow = new LrcRow();//创建每行封装歌词的对象
                //获取新的解析封装好的歌词 添加到集合中
                LrcRow lrcRow2 = lrcRow.getRow(line);
//                Log.i("guochunhong","LrcRows BuildList lrcRow2 :" + lrcRow2);

                if (lrcRow2 != null) {
                    list.add(lrcRow2);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
        // TODO: handle exception
            Log.i("guochunhong","LrcRows BuildList Exception e :" + e);
        }
        return list;

    }

}
