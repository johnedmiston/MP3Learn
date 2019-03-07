package com.lightsys.audioapp;

public class Note {
    String title;
    String filename;

    Note(String title, String filename){
        this.title = title;
        this.filename = filename;
    }

    Note(String filename){
        this.title = filename.replace(".txt", "");
        this.filename = filename;
    }

    public String getFilename(){
        return filename;
    }

    public String getTitle() { return title; }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }

}
