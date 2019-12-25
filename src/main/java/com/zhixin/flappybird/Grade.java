package com.zhixin.flappybird;

import android.support.v7.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhixin.flappybird.adapter.Grade_item;
import com.zhixin.flappybird.entity.User;
import com.zhixin.flappybird.sql.FactorySql;

import java.util.List;

public class Grade extends AppCompatActivity implements View.OnClickListener {
    ListView gradeItem;
    Button lastpage, nextpage, findbyname, findbygrade;
    EditText suname, sugrade;
    TextView gradepage, topusername, topgrade;
    FactorySql factorySql;
    protected int page;
    private SQLiteDatabase sd;
    private LinearLayout line1;

    private void init() {
        page = 1;
        line1 = findViewById(R.id.line1);
        gradeItem = findViewById(R.id.gradelist);
        lastpage = findViewById(R.id.lastpage);
        nextpage = findViewById(R.id.nextpage);
        gradepage = findViewById(R.id.gradepage);
        topusername = findViewById(R.id.topusername);
        topgrade = findViewById(R.id.topgrade);
        findbyname = findViewById(R.id.button1);
        findbygrade = findViewById(R.id.button2);
        suname = findViewById(R.id.editText1);
        sugrade = findViewById(R.id.editText2);
        factorySql = new FactorySql(this);

        sd = factorySql.getReadableDatabase();
        if (factorySql.getUserCount() <= 0) {
            ContentValues values = new ContentValues();
            values.put("username", "sagsdg");
            values.put("grade", 99);
            sd.insert("gradelist", null, values);
            values.clear();
            values.put("username", "dfhdsh");
            values.put("grade", 656);
            sd.insert("gradelist", null, values);
            Log.i("数据库", "插入数据成功");
        }
        List<User> users = factorySql.findtop();
        topusername.setText(users.get(0).getUsername());
        topgrade.setText(users.get(0).getGrade() + "");
        getTenUser();
        gradepage.setText(String.valueOf(page) + "/" + factorySql.getUserCount());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        init();
        lastpage.setOnClickListener(this);
        nextpage.setOnClickListener(this);
        findbyname.setOnClickListener(this);
        findbygrade.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lastpage:
                if (page == 1) {
                    Toast.makeText(this, "已经到首页了", Toast.LENGTH_SHORT).show();
                    break;
                }
                page--;
                getTenUser();
                gradepage.setText(String.valueOf(page) + "/" + factorySql.getUserCount());
                break;
            case R.id.nextpage:
                if (page == factorySql.getUserCount()) {
                    Toast.makeText(this, "已经到页尾了", Toast.LENGTH_SHORT).show();
                    break;
                }
                page++;
                getTenUser();
                gradepage.setText(String.valueOf(page) + "/" + factorySql.getUserCount());
                break;
            case R.id.button1:
                if (suname.getText().toString().equals("")) {
                    Toast.makeText(this, "搜索内容为空显示所有", Toast.LENGTH_SHORT).show();
                    getTenUser();
                    gradepage.setText(String.valueOf(page) + "/" + factorySql.getUserCount());
                    line1.setVisibility(View.VISIBLE);
                    break;
                }
                line1.setVisibility(View.GONE);
                List<User> users = factorySql.findbyname(suname.getText().toString());
                Grade_item grade_item1 = new Grade_item(this, users);
                gradeItem.setAdapter(grade_item1);
                break;
            case R.id.button2:
                if (sugrade.getText().toString().equals("")) {
                    Toast.makeText(this, "搜索内容为空显示所有", Toast.LENGTH_SHORT).show();
                    getTenUser();
                    gradepage.setText(String.valueOf(page) + "/" + factorySql.getUserCount());
                    line1.setVisibility(View.VISIBLE);
                    break;
                }
                line1.setVisibility(View.GONE);
                List<User> user = factorySql.findbygrade(Integer.valueOf(sugrade.getText().toString()));
                Grade_item grade_item2 = new Grade_item(this, user);
                gradeItem.setAdapter(grade_item2);
                break;
        }
    }

    /**
     * 获取十个用户(上、下一页)
     */
    public void getTenUser() {
        //从数据库中查找出十个学生信息
        List<User> users = factorySql.getTenUser(page);
        Log.i("user", users.size() + "");
        //绑定适配器
        Grade_item grade_item = new Grade_item(this, users);
        gradeItem.setAdapter(grade_item);
    }
}
