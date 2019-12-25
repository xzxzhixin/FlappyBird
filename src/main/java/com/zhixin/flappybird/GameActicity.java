package com.zhixin.flappybird;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.zhixin.flappybird.entity.User;
import com.zhixin.flappybird.server.MyService;
import com.zhixin.flappybird.sql.FactorySql;
import com.zhixin.flappybird.tool.GlobeVar;
import com.zhixin.flappybird.tool.MyEnum;
import com.zhixin.flappybird.view.GameView;

public class GameActicity extends AppCompatActivity implements View.OnClickListener {

    private FactorySql factorySql;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
                findViewById(R.id.imageView2).setOnClickListener(GameActicity.this);
                findViewById(R.id.pause).setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.blueGreen), false);
        factorySql = new FactorySql(this);
        Intent it = new Intent(this, MyService.class);
        startService(it);
        final Button pause = (Button) findViewById(R.id.pause);
        final GameView gameView = (GameView) findViewById(R.id.gameView);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameActicity.this, MyService.class);
                if (pause.getText().toString().equals("暂停")) {
                    startService(it);
                    gameView.pause();
                    pause.setText("继续");
                } else {
                    startService(it);
                    gameView.begin();
                    pause.setText("暂停");
                }
            }
        });
    }

    public void showDialog() {
        factorySql.updateUser(new User(GlobeVar.name, GlobeVar.grade));
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2: // 弹框图片点击事件，结束这个页面，因为主界面没有销毁，所以直接过去
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Intent it = new Intent(GameActicity.this, MyService.class);
        stopService(it);
        super.onDestroy();
    }
}
