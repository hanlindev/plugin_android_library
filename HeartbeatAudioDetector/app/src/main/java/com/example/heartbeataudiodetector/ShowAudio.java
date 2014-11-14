package com.example.heartbeataudiodetector;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sana.android.plugin.application.CommManager;


public class ShowAudio extends Activity {

    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_audio);
        // Get launch intent from MockSana
        Intent intent = getIntent();
        CommManager cm = CommManager.getInstance();
        cm.respondToIntent(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void play (View view) {
        mPlayer = new MediaPlayer();
        try{
            mPlayer.setDataSource(getApplicationContext(), getIntent().getData());
            mPlayer.prepare();
            mPlayer.start();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stopPlaying(View view){
        try{
            mPlayer.release();
            mPlayer = null;
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
