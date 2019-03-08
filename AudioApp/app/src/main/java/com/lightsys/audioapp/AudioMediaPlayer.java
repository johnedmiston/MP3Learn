package com.lightsys.audioapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * A Service class for playing audio in the background
 *
 * This class is where we would add in a service to play in the background.
 */
public class AudioMediaPlayer extends Service {
    String mp3;

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
