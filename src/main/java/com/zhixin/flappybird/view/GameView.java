package com.zhixin.flappybird.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.zhixin.flappybird.GameActicity;
import com.zhixin.flappybird.MainActivity;
import com.zhixin.flappybird.R;
import com.zhixin.flappybird.objects.Brid;
import com.zhixin.flappybird.objects.Floor;
import com.zhixin.flappybird.objects.Pipe;
import com.zhixin.flappybird.tool.GlobeVar;
import com.zhixin.flappybird.tool.MyEnum;
import com.zhixin.flappybird.tool.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private GameActicity gameActicity;
    private Paint paint;
    private Canvas mc;
    private GameStatus status = GameStatus.RUNNING;
    private Thread gameThread;
    private Brid brid;
    private Bitmap[] bridBitmap; // 小鸟图片
    private Bitmap[] mNumBitmap; // 数字数组
    private int mSingleGradeHeight;
    private int mSingleGradeWidth;
    private List<Pipe> pipes = new ArrayList<>(); // 地图管道的集合，也是障碍物
    private final int WIDTH = Util.dp2px(getContext(), 60); // 管道的宽度
    private final int PIPSPACE = Util.dp2px(getContext(), 200); // 管道相互之间的距离
    private int SPEED = Util.dp2px(getContext(), 3); // 管道和地面向左移动的速度
    private List<Floor> floors = new ArrayList<>(); // 地面版图的集合
    private Boolean aSpeed = false; // 运行加速的标志
    private final int meijifenjiasu = 5;
    private final int xiaoniaoshangtiao = 32;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(MyEnum.tip.getName(), "onSizeChanged");
        for (int i = 0; i < 3; i++) {
            Floor f = new Floor(loadImageByResId(R.mipmap.ground), getWidth() * i);
            floors.add(f);
        }
        brid = new Brid(getContext(), getHeight(), bridBitmap);
        Pipe p = new Pipe(loadImageByResId(R.mipmap.top), loadImageByResId(R.mipmap.last), getWidth() / 2 - WIDTH / 2, getHeight());
        pipes.add(p);
        for (int i = 0; i < 5; i++) {
            int x = pipes.get(pipes.size() - 1).getX();
            Pipe p1 = new Pipe(loadImageByResId(R.mipmap.top), loadImageByResId(R.mipmap.last), x + PIPSPACE, getHeight());
            pipes.add(p1);
        }
        mSingleGradeHeight = (int) (h * 1 / 15f);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
    }

    private void initBitmap() {
        bridBitmap = initBridBitmap();
        mNumBitmap = initNumBitmap();
    }

    private Bitmap[] initNumBitmap() {
        Bitmap[] b = new Bitmap[10];
        b[0] = (loadImageByResId(R.drawable.n0));
        b[1] = (loadImageByResId(R.drawable.n1));
        b[2] = (loadImageByResId(R.drawable.n2));
        b[3] = (loadImageByResId(R.drawable.n3));
        b[4] = (loadImageByResId(R.drawable.n4));
        b[5] = (loadImageByResId(R.drawable.n5));
        b[6] = (loadImageByResId(R.drawable.n6));
        b[7] = (loadImageByResId(R.drawable.n7));
        b[8] = (loadImageByResId(R.drawable.n8));
        b[9] = (loadImageByResId(R.drawable.n9));
        return b;
    }

    private Bitmap[] initBridBitmap() {
        Bitmap[] b = new Bitmap[3];
        b[0] = (loadImageByResId(R.mipmap.g1));
        b[1] = (loadImageByResId(R.mipmap.g2));
        b[2] = (loadImageByResId(R.mipmap.g3));
        return b;
    }

    /**
     * 绘制分数
     */
    private void drawGrades(long grade) {
        String gradeString = grade + "";
        mc.save();
        mc.translate(getWidth() / 2 - gradeString.length() * mSingleGradeWidth / 2,
                1f / 8 * getHeight());
        Rect r = new Rect(0, 0, mSingleGradeWidth, mSingleGradeHeight);
        for (int i = 0; i < gradeString.length(); i++) {
            String numStr = gradeString.substring(i, i + 1);
            int num = Integer.valueOf(numStr);
            mc.drawBitmap(mNumBitmap[num], null, r, null);
            mc.translate(mSingleGradeWidth, 0);
        }
        mc.restore();
    }

    /**
     * 游戏状态
     */
    private enum GameStatus {
        PAUSE, RUNNING, STOP
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gameActicity = (GameActicity) context;
        paint = new Paint();
        paint.setAntiAlias(true); // 抗锯齿
        paint.setDither(true); // 防抖动
        getHolder().addCallback(this);
        this.setKeepScreenOn(true); // 保持屏幕常亮
        Log.i(MyEnum.tip.getName(), "GameView");
        initBitmap();
    }

    private void drawPipe() {
        movePip();
        for (Pipe p : pipes) { // 画管道
            p.draw(mc, WIDTH, getHeight());
        }
        removePipe();
        addPipe();
    }

    /**
     * 地图使用三张图片链接，当第一张图片过去屏幕左边立马移除加到最后
     */
    private void drawFloor() {
        moveFloor();
        for (int i = 0; i < floors.size(); i++) {
            Floor floor = floors.get(i);
            floor.draw(mc, Util.dp2px(getContext(), 200));
            if (floor.getX() + mc.getWidth() < 0) {
                floors.remove(floor);
                Floor f = new Floor(loadImageByResId(R.mipmap.ground), floors.get(floors.size() - 1).getX() + mc.getWidth());
                floors.add(f);
            }
        }
    }

    /**
     * 移动地板
     */
    private void moveFloor() {
        for (Floor f : floors) {
            f.setX(f.getX() - SPEED);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(MyEnum.tip.getName(), "surfaceDestroyed");
        status = GameStatus.STOP;
    }

    public void draw() {
        mc = getHolder().lockCanvas(); // 锁住画布
        mc.save();
        drawBack(); // 画背景
        drawPipe(); // 画管道
        brid.draw(mc, Util.dp2px(getContext(), 200)); // 画鸟
        drawFloor(); // 画地表
        mc.restore();
        drawGrades(GlobeVar.grade); // 画分数
        getHolder().unlockCanvasAndPost(mc); // 解锁画布
    }

    private void drawBack() {
        Bitmap bitmap = loadImageByResId(R.mipmap.bg);
        Rect r = new Rect();
        r.set(0, 0, getWidth(), getHeight());
        mc.drawBitmap(bitmap, null, r, null);
    }

    /**
     * 移除管道，如果管道已经在左边屏幕外边就移除，并且计分
     */
    private void removePipe() {
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            if (pipe.getX() + WIDTH < 0) {
                GlobeVar.grade++;
                pipes.remove(pipe);
                aSpeed = true;
            }
        }
    }

    /**
     * 添加管道，如果管道的数量少于屏幕宽度/管道宽度加间隙加上3，就添加一个管道
     */
    private void addPipe() {
        int i = getWidth() / (WIDTH + PIPSPACE) + 3;
        if (pipes.size() < i) {
            int x = pipes.get(pipes.size() - 1).getX();
            Pipe p = new Pipe(loadImageByResId(R.mipmap.top), loadImageByResId(R.mipmap.last), x + PIPSPACE, getHeight());
            pipes.add(p);
        }
    }

    /**
     * 移动管道
     */
    private void movePip() {
        for (Pipe p : pipes) {
            p.setX(p.getX() - SPEED);
        }
    }

    /**
     * 加载静态资源的方法
     *
     * @param id
     * @return
     */
    private Bitmap loadImageByResId(int id) {
        return BitmapFactory.decodeResource(getResources(), id);
    }

    public void pause() {
        this.status = GameStatus.PAUSE;
    }

    public void begin() {
        status = GameStatus.RUNNING;
        synchronized (gameThread) {
            gameThread.notify();
        }
    }

    public void stop() {
        synchronized (gameThread) {
            gameThread.notify();
        }
        status = GameStatus.STOP;
    }

    @Override
    public void run() {
        while (status != GameStatus.STOP) {
            long beginTime = System.currentTimeMillis();
            draw();
            checkhid();
            tianJiaDengJi();
            long endTime = System.currentTimeMillis();
            try {
                long l = 65 - (endTime - beginTime);
                Log.i(MyEnum.tip.getName()+"time", l + "");
                if (l < 0) l = 0;
                gameThread.sleep(l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (status == GameStatus.PAUSE) {
                try {
                    synchronized (gameThread) {
                        gameThread.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        endGame();
    }

    /**
     * 触摸响应的事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (status == GameStatus.RUNNING) {
                Log.i(MyEnum.tip.getName(), "点击了");
                brid.setY(brid.getY() + Util.dp2px(getContext(), -xiaoniaoshangtiao), getHeight(), Util.dp2px(getContext(), 200));
            }
        }
        return true;
    }

    private void checkhid() {
        for (Pipe p : pipes) {
            if (p.getX() > brid.getRect().right) {
                continue;
            }
            boolean b = p.hitTopOrDown(brid, WIDTH, getHeight());
            if (b) {
                Log.i(MyEnum.tip.getName(), "GameOver");
                status = GameStatus.STOP;
            }
        }
    }

    private void tianJiaDengJi() {
        if (GlobeVar.grade % meijifenjiasu == 0 && aSpeed) {
            SPEED += (Util.dp2px(getContext(), 1));
            aSpeed = false;
        }
        Log.i(MyEnum.tip.getName(), "当前速度为：" + SPEED);
    }

    /**
     * 结束游戏处理
     */
    private void endGame() {
        gameActicity.showDialog();
    }
}
