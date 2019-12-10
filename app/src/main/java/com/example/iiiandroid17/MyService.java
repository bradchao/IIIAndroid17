package com.example.iiiandroid17;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private MediaPlayer mediaPlayer;
    private File sdroot;
    private Timer timer;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    private class MyTask extends TimerTask {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()){
                int pos = mediaPlayer.getCurrentPosition();
                sendBroadcast(new Intent("PLAY_NOW").putExtra("now", pos));
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(new MyTask(),0, 100);
        try {
            sdroot = Environment.getExternalStorageDirectory();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(sdroot.getAbsolutePath() + "/soda.mp3");
            mediaPlayer.prepare();
        }catch (Exception e){
            Log.v("brad", e.toString());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String cmd = intent.getStringExtra("cmd");
        if (mediaPlayer.isPlaying() && cmd.equals("pause")){
            mediaPlayer.pause();
        }else if (!mediaPlayer.isPlaying() && cmd.equals("play")){
            mediaPlayer.start();
            sendBroadcast(new Intent("PLAY_NOW").putExtra("len", mediaPlayer.getDuration()));
        }else if (cmd.equals("seekto")){
            int nowpos = intent.getIntExtra("nowpos", -1);
            if (nowpos >= 0) mediaPlayer.seekTo(nowpos);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
        }

        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
