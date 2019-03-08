package com.lightsys.audioapp;

//Class to hold Lesson information
public class Lesson {
    String name;
    String mp3;
    int seekTime;
    String textData;
    String notes;
    String course;
    Lesson(){
        seekTime = 0;
        notes = "";
    }
    Lesson(String name,String course){
        this.name = name;
        this.course = course;
        seekTime = 0;
        notes= "";
    }
    Lesson(String name,String mp3,String course){
        this.name = name;
        this.mp3 = mp3;
        this.course = course;
        seekTime = 0;
        notes= "";
    }
    Lesson(String name,String mp3,String textData,String course){
        this.name = name;
        this.mp3 = mp3;
        this.textData = textData;
        this.course = course;
        seekTime = 0;
        notes= "";
    }

    public String getName() {
        return name;
    }

    public String getMp3() {
        return mp3;
    }

    public String getTextData() {
        return textData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getSeekTime() {
        return seekTime;
    }

    public void setSeekTime(int seekTime) {
        this.seekTime = seekTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
