package com.zhixin.flappybird.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;


import com.zhixin.flappybird.entity.User;

import java.util.ArrayList;
import java.util.List;

public class FactorySql extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Grade_data";
    private static final String TABLE_NAME = "gradelist";
    private static final int VERSION = 1;
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "username";
    private static final String KEY_GRADE = "grade";
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + KEY_ID +
            " integer primary key autoincrement," + KEY_NAME + " text not null," +
            KEY_GRADE + " int default 0);";
    SQLiteDatabase db;

    public FactorySql(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.i("数据库：", "创建成功");
    }


    /**
     * 添加用户
     */
    public void adduser(User user) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getUsername());
        values.put(KEY_GRADE, user.getGrade());
        db.insert(TABLE_NAME, null, values);
    }

    /**
     * 更新用户
     */
    public void updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getUsername());
        values.put(KEY_GRADE, user.getGrade());
        db.update(TABLE_NAME, values, KEY_NAME + "=?", new String[]{user.getUsername()});
    }

    /**
     * 获取十个用户信息
     */
    public List<User> getTenUser(int i) {
        List<User> users = new ArrayList<>();
        String select_sql = "select *  from " + TABLE_NAME + " limit " + (i * 10 - 10) + "," + 10;
        Cursor cursor = db.rawQuery(select_sql, null);
        while (cursor.moveToNext()) {
            User u = new User();
            u.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            u.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            u.setGrade(cursor.getInt(cursor.getColumnIndex("grade")));
            users.add(u);
        }
        return users;
    }

    /**
     * 获取用户总数量
     */
    public int getUserCount() {
        String select_sql = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(select_sql, null);
        int anInt = cursor.getCount();
        return anInt / 10 + 1;
    }

    /**
     * 成绩查询
     */
    public List<User> findbygrade(int i) {
        List<User> users = new ArrayList<>();
        String select_sql = "select *  from " + TABLE_NAME + " where grade =" + i;
        Cursor cursor = db.rawQuery(select_sql, null);
        while (cursor.moveToNext()) {
            User u = new User();
            u.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            u.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            u.setGrade(cursor.getInt(cursor.getColumnIndex("grade")));
            users.add(u);
        }
        return users;
    }

    /**
     * 用户名查询
     */
    public List<User> findbyname(String name) {
        List<User> users = new ArrayList<>();
        String select_sql = "select *  from " + TABLE_NAME + " where username like '%" + name + "%'";
        Cursor cursor = db.rawQuery(select_sql, null);
        while (cursor.moveToNext()) {
            User u = new User();
            u.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            u.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            u.setGrade(cursor.getInt(cursor.getColumnIndex("grade")));
            users.add(u);
        }
        return users;
    }


    /**
     * 升级方法，删除旧表增加新表
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("ALTER TABLE gradelist ALTER column grade int default 0");
        onCreate(sqLiteDatabase);
    }

    /**
     * 最高分用户查询
     */
    public List<User> findtop() {
        List<User> users = new ArrayList<>();
        String select_sql = "select *  from " + TABLE_NAME + " where grade = (select max(grade) from " + TABLE_NAME + ")";
        Cursor cursor = db.rawQuery(select_sql, null);
        while (cursor.moveToNext()) {
            User u = new User();
            u.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            u.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            u.setGrade(cursor.getInt(cursor.getColumnIndex("grade")));
            users.add(u);
        }
        return users;
    }

    /**
     * 用户名查询重复
     */
    public boolean findsamename(String name) {
        boolean temp = false;
        List<User> users = new ArrayList<>();
        String select_sql = "select *  from " + TABLE_NAME + " where username = '" + name + "'";
        Cursor cursor = db.rawQuery(select_sql, null);
        if (cursor.getCount() >= 1) {
            temp = true;
        }
        return temp;
    }

}
