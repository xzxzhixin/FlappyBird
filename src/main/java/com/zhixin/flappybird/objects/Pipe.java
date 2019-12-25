package com.zhixin.flappybird.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.zhixin.flappybird.tool.GlobeVar;
import com.zhixin.flappybird.tool.MyEnum;
import com.zhixin.flappybird.tool.Util;

import java.util.Random;

import lombok.AllArgsConstructor;

public class Pipe {

    private static final float RATIO_BETWEEN_UP_DOWN = 1 / 5F;//上下管道间隙比例
    private static final float RATIO_PIPE_MAX_HEIGHT = 5 / 10F;//管道最大高度比例
    private static final float RATIO_PIPE_MIN_HEIGHT = 1 / 10F;//管道最小高度比例
    private static Random random = new Random();//随机数

    private Bitmap top; // 上面的管道
    private Bitmap down; // 下面的管道
    private Rect rectTop = new Rect(); // 上管道矩阵
    private Rect rectDown = new Rect(); // 下管道矩阵
    private int space; // 上下管道的间隙
    private int x; // 管道在X轴的位置
    private int pipeHeight; // 上管道的高度

    /**
     * @param top        上面的管道
     * @param down       下面的管道
     * @param x          管道在X轴的位置
     * @param gameHeight 游戏界面的高度
     */
    public Pipe(Bitmap top, Bitmap down, int x, int gameHeight) {
        this.top = top;
        this.down = down;
        this.x = x;
        space = (int) (gameHeight * RATIO_BETWEEN_UP_DOWN);
        randomHeight(gameHeight);
    }


    /**
     * 柱子的高度随机
     *
     * @param gameHeight
     */
    private void randomHeight(int gameHeight) {
        pipeHeight = random
                .nextInt((int) (gameHeight * (RATIO_PIPE_MAX_HEIGHT - RATIO_PIPE_MIN_HEIGHT)));
        pipeHeight = (int) (pipeHeight + gameHeight * RATIO_PIPE_MIN_HEIGHT);
    }

    public void draw(Canvas mc, int width, int gameHeight) {
        mc.save();
        mc.translate(x, 0);
        rectTop.set(0, 0, width, pipeHeight);
        mc.drawBitmap(top, null, rectTop, null);
        mc.translate(0, rectTop.bottom + space);
        rectDown.set(0, 0, width, gameHeight - pipeHeight - space);
        mc.drawBitmap(down, null, rectDown, null);
        mc.restore();
    }

    public boolean hitTopOrDown(Brid b, int width, int gameHeight) {
        rectTop.set(x, 0, width, pipeHeight);
        rectDown.set(x, pipeHeight + space, width, gameHeight);
        if (b.getRect().intersect(rectTop) || b.getRect().intersect(rectDown)) {
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
