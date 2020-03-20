package com.android.fanxinli;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcView extends View implements Runnable{
    private int width = 1080;//xml中设定的歌词宽度
    private int heght = 400;//xml中设定的歌词高度
    private Paint paint1 = null;//用于绘画当前正在播放的歌词
    private Paint paint2 = null;//用于绘画非当前播放的歌词

    private int padding = 100;//歌词的行间距
    private int textSize = 18;
    private int index = 1;//正在播放歌词段所在下标
    private int lines = 1;//总共要显示多少行歌词
    private int count = 0;//记录应该取哪一列歌词

    private boolean hasLyric = true;

    private List<Long> timeList = new ArrayList<>();
    private List<String> contentList = new ArrayList<>();
    private Queue<String> queue = new ArrayDeque<String>();//用于储存当前正在展示的歌词

    /**
     * 歌词显示时间相关的参数
     */
    private long timePastBy = 0;//歌词已经显示的时间

    public LrcView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public void init(){//初始化歌词是通过给这个view一个 String list
        count = 0;
        if(contentList.size()== 0){//没有歌词
            contentList.add("Just enjoy!");
        }
        setFocusable(true);
        lines = heght/padding;//得到可以显示多少行歌词
        index = lines/2;//正在播放歌词的所在下标

        for(int i = 0;i<lines;i++){//把队列初始化为空
            queue.add("");
        }

        for (int i = 0;i<index;i++){//开始播放的时候，第一行歌词需要在中间出现
            contentList.add("");
            queue.remove();
            queue.add(contentList.get(count));
            count++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setTextAlign(Paint.Align.CENTER);

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setTextAlign(Paint.Align.CENTER);

        paint1.setColor(Color.BLUE);//透明度以及RGB值
        paint2.setColor(Color.BLACK);

        paint1.setTextSize(50);//被播放的歌词字体设置为50px
        paint1.setTypeface(Typeface.SERIF);

        paint2.setTextSize(40);
        paint2.setTypeface(Typeface.DEFAULT);

        //设置歌词之间的行距,根据显示时间，显示歌词
        int lineCount = 1;
        for(String s:queue){
            if(lineCount == index)
                canvas.drawText(s,width/2,200+padding*lineCount,paint1);
            else
                canvas.drawText(s,width/2,200+padding*lineCount,paint2);
            lineCount++;
        }

    }

    public void move(){
        //歌词滚动
        if(count<contentList.size()){//如果还有剩余歌词
            queue.remove();//queue里面动态更新，让歌词会滚动
            queue.add(contentList.get(count));
            count++;
            postInvalidate();//刷新界面
        }else{
            return;
        }

    }

    @Override
    public void run() {
        while(true){
            try{
                if (timeList.size()>0) {
                    timePastBy+=timeList.get(0);//歌词已经显示了多久，为调整歌词进度设定（目前还没有添加这个功能）
                    Thread.sleep(timeList.remove(0));//每行歌词显示的时间
                    move();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setList(List<Long> l1, List<String > l2){
        timeList = l1;
        contentList = l2;
    }

    public void AnalyseLyricFile(InputStream inputStream){ //参数是歌词文件的路径
        BufferedReader reader = null;
        InputStreamReader inputFileReader = null;
        String tempString = null;
        //正则表达式解析出时间戳
        Pattern timePattern = Pattern.compile("(.*?)]");
        String time = "";
        long timestamp = 0;
        long timePadding = 0;//帮助得到每行歌词显示时间的变量
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//把字符串转换为时间
        try {
            inputFileReader = new InputStreamReader(inputStream, "GB2312");
            reader = new BufferedReader(inputFileReader);
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                Matcher timeMatcher = timePattern.matcher(tempString);
                if (timeMatcher.find()){//如果匹配到符合结果
                    time = timeMatcher.group(1);//不要后面的中括号
                    if((int)time.charAt(1) <= 57){//非数字选项，目前没有分析音乐的标题，作曲家
                        time = time.replace('.', ':');
                        time = time.substring(1, time.length());
                        String temp [] = time.split(":");
                        try {
                            timestamp = simpleDateFormat.parse("1970010108"+temp[0]+temp[1]).getTime();//电脑的开始计时时间是从1970年1月1日早上8点钟开始
                            timestamp+=10*Integer.parseInt(temp[2]);
                            timeList.add(timestamp-timePadding);
                            contentList.add(" "+tempString.substring(10, tempString.length()));
                            timePadding = timestamp;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

}
