package com.example.mia.snorelab;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.application.CommManager;

import java.io.InputStream;


public class FirstPageActivity extends ActionBarActivity {
    private MediaPlayer mPlayer;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        // Get launch intent from MockSana
        Intent intent = getIntent();
        CommManager cm = CommManager.getInstance();
        cm.respondToIntent(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first_page, menu);
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
}
