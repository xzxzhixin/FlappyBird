package com.zhixin.flappybird.entity;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String username;
    private int grade;
    public User(){}
    public User(String username, int grade){
        this.username = username;
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
