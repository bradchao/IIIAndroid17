package com.example.iiiandroid17;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private MyReceiver receiver;
    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        seekBar = findViewById(R.id.seekBar);
        receiver = new MyReceiver();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    intent.putExtra("cmd", "seekto");
                    intent.putExtra("nowpos", progress);
                    startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("PLAY_NOW"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    public void play(View view) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("cmd", "play");
        startService(intent);
    }

    public void pause(View view) {

        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("cmd", "pause");
        startService(intent);


    }

    public void reset(View view) {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pos = intent.getIntExtra("now", -1);
            int len = intent.getIntExtra("len", -1);
            if (len>=0){
                seekBar.setMax(len);
            }else if (pos >= 0){
                seekBar.setProgress(pos);
            }
        }
    }

}
