package com.android.fanxinli;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    //计算播放时间
    public static String calculateTime(int time) {
        int minute;
        int second;
        if (time > 60) {
            minute = time / 60;
            second = time % 60;
            //分钟再0~9
            if (minute >= 0 && minute < 10) {
                //判断秒
                if (second >= 0 && second < 10) {
                    return "0" + minute + ":" + "0" + second;
                } else {
                    return "0" + minute + ":" + second;
                }
            } else {
                //分钟大于10再判断秒
                if (second >= 0 && second < 10) {
                    return minute + ":" + "0" + second;
                } else {
                    return minute + ":" + second;
                }
            }
        } else if (time < 60) {
            second = time;
            if (second >= 0 && second < 10) {
                return "00:" + "0" + second;
            } else {
                return "00:" + second;
            }
        }
        return null;
    }

    public static String getToTimems(long l) {
        String format = "mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(new Date(l));
    }

    public static String layoutToImage(ViewGroup v, int width, int height) {
        int h = 0;
        //测量使得view指定大小
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        v.measure(measuredWidth, measuredHeight);
        //调用layout方法布局后，可以得到view的尺寸大小
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        //获取当前控件的所以子控件的高
        for (int i =0; i < v.getChildCount(); i++) {
            h += v.getChildAt(i).getHeight();
        }
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), h, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.TRANSPARENT);
        v.draw(c);

        byte[] bitmapBytes = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bmp != null) {
                baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                bitmapBytes = baos.toByteArray();
//				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Base64.encodeToString(bitmapBytes,Base64.DEFAULT);
//		return bmp;
    }
}
