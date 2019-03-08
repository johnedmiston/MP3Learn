package com.lightsys.audioapp;

public class Note {
    String title;
    String filename;
    Lesson lesson;

    Note(String title, String filename, Lesson lesson){
        this.title = title;
        this.filename = filename;
        this.lesson = lesson;
    }

    Note(String title, String filename){
        this.title = title;
        this.filename = filename;
        this.lesson = null;
    }

    Note(String filename){
        this.title = filename.replace(".txt", "");
        this.filename = filename;
        this.lesson = null;
    }

    public String getFilename(){
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
        lesson.setNotes(this.filename);
    }
}
