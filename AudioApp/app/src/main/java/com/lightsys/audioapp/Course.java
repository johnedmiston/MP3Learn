package com.lightsys.audioapp;

import java.util.ArrayList;

public class Course {
    String name;
    ArrayList lessons;

    Course(String name){
        this.name = name;
        lessons = null;
    }
    void addLesson(Lesson lesson){
        if(lessons == null){
            lessons = new ArrayList();
        }
        lessons.add(lesson);
    }
    ArrayList getLessons(){
        return lessons;
    }
    public String getName() {
        return name;
    }
}
