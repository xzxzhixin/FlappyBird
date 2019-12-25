package com.zhixin.flappybird.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhixin.flappybird.R;
import com.zhixin.flappybird.entity.User;

import java.util.ArrayList;
import java.util.List;

public class Grade_item extends BaseAdapter {
    Context context;
    List<User> users;


    public Grade_item(Context context, List<User> users){
        this.context = context;
        this.users =users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grade_items,parent,false);
        }
        TextView usernametext = convertView.findViewById(R.id.username);
        TextView gradetext = convertView.findViewById(R.id.grade);
        usernametext.setText(users.get(position).getUsername());
        gradetext.setText(users.get(position).getGrade()+"");
        return convertView;
    }
}
