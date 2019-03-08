package com.lightsys.audioapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AudioMediaPlayer extends Service {
    String mp3;

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        mp3 = intent.getStringExtra("lesson_mp3");




        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
