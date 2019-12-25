package com.zhixin.flappybird.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.zhixin.flappybird.tool.MyEnum;

/**
 * 地图使用三张图片链接，当第一张图片过去屏幕左边立马移除加到最后
 */
public class Floor {

    private Bitmap bitmap;
    private int x; // x轴位置

    public Floor(Bitmap bitmap,int x){
        this.bitmap=bitmap;
        this.x=x;
    }

    public void draw(Canvas mc,int HEIGHT){
        Rect rect=new Rect(x,mc.getHeight()-HEIGHT,mc.getWidth(),mc.getHeight());
        mc.drawBitmap(bitmap,null,rect,null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
