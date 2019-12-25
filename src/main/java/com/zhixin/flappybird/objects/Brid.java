package com.zhixin.flappybird.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.zhixin.flappybird.tool.MyEnum;
import com.zhixin.flappybird.tool.Util;

import lombok.Data;

@Data
public class Brid {

    private Bitmap[] bitmap; // 小鸟图片
    private int bitmapindex; // 画那一张图片
    private int y; // 位置
    private int speed; // 下落加速度
    private int downSpeed; // 下落的速度
    private int mBirdWidth; // 图片的宽度
    private int mBirdHeight; // 图片的高度
    private Rect rect = new Rect(); // 矩形

    /**
     * @param context    上下文
     * @param gameHeight 游戏界面的高度
     * @param bitmap     // 图片数组
     */
    public Brid(Context context, int gameHeight, Bitmap[] bitmap) {
        this.bitmap = bitmap;
        y = gameHeight / 2 - bitmap[0].getWidth() / 2;
        speed = Util.dp2px(context, 2);
        mBirdWidth = Util.dp2px(context, 35);
        mBirdHeight = (int) (mBirdWidth * 1.0f / bitmap[0].getWidth() * bitmap[0].getHeight());
    }

    /**
     * @param mc     画板的引用
     * @param HEIGHT 地板的高度
     */
    public void draw(Canvas mc, int HEIGHT) {
        Bitmap bitmap = this.bitmap[(bitmapindex++) % this.bitmap.length];
        downSpeed += speed;
        int yy = (y += downSpeed);
        if (yy < 0) {
            yy = 0;
            Log.i(MyEnum.tip.getName(), "yy<：" + yy);
            downSpeed -= speed;
        } else if (yy > mc.getHeight() - mBirdWidth - HEIGHT) {
            yy = mc.getHeight() - mBirdWidth - HEIGHT;
            downSpeed -= speed;
        }
        rect.set(0, yy, mBirdWidth, mBirdHeight + yy);
        mc.drawBitmap(this.bitmap[(bitmapindex++) % this.bitmap.length], null, rect, null);
    }

    /**
     * @param y          小鸟的位置
     * @param gameHeight 游戏界面的高度
     * @param HEIGHT     地板的高度
     */
    public void setY(int y, int gameHeight, int HEIGHT) {
        downSpeed = speed;
        if (y < 0) {
            return;
        }
        if (y > gameHeight - mBirdWidth - HEIGHT) {
            y = gameHeight - mBirdWidth - HEIGHT;
        }
        this.y = y;
    }
}
