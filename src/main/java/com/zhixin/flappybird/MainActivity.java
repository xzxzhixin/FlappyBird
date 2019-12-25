package com.zhixin.flappybird;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.zhixin.flappybird.entity.User;
import com.zhixin.flappybird.sql.FactorySql;
import com.zhixin.flappybird.tool.GlobeVar;
import com.zhixin.flappybird.tool.MyEnum;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FactorySql factorySql;
    private TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.blueGreen), false);
        findViewById(R.id.begin).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.imageView).setOnClickListener(this);
        factorySql = new FactorySql(this);
        show = (TextView) findViewById(R.id.show);
        int userCount = factorySql.getUserCount();
        show.setText("用户user" + userCount + "，你好，你的分数为：0");
        GlobeVar.name = "user" + userCount;
        showDialog();
        Log.i(MyEnum.tip.getName(), "MainActivity::onCreate");
    }

    private void showDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.main_dialog, null);
        dialog.setView(line);
        dialog.show();
        final EditText username = (EditText) line.findViewById(R.id.username);
        line.findViewById(R.id.use).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s = username.getText().toString();
                String str = s;
                String pattern = "^[0-9a-z]*$";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(str);
                boolean matches = m.matches();
                if (!matches) {
                    Toast.makeText(MainActivity.this, "用户名只能为小写字母加数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (s != null && s.length() > 0) {
                    boolean b = factorySql.findsamename(s);
                    if (b) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("确认对话框")
                                .setMessage("你确认使用这个用户名吗？")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.i("徐志翔：", "执行方法：work01_1，cancl");
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        List<User> findbyname = factorySql.findbyname(s);
                                        User user = findbyname.get(0);
                                        GlobeVar.name = user.getUsername();
                                        GlobeVar.grade = user.getGrade();
                                        show.setText("用户" + GlobeVar.name + "，你好，你的分数为：" + GlobeVar.grade);
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        GlobeVar.name = s;
                        factorySql.adduser(new User(s, 0));
                        show.setText("用户" + GlobeVar.name + "，你好，你的分数为：0");
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        show.setText("用户" + GlobeVar.name + "，你好，你的分数为：" + GlobeVar.grade);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin: // 开始游戏
                if (!factorySql.findsamename(GlobeVar.name))
                    factorySql.adduser(new User(GlobeVar.name, GlobeVar.grade));
                GlobeVar.grade = 0; // 初始化数据
                Intent i = new Intent(this, GameActicity.class);
                startActivity(i);
                break;
            case R.id.button2:
                Intent i1 = new Intent(this, Grade.class);
                startActivity(i1);
                break;
            case R.id.imageView:
                Intent i2 = new Intent(this, AboutActivity.class);
                startActivity(i2);
                break;
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
