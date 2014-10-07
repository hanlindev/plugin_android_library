package sana.com.plugin.mockSana;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.VideoView;


public class VideoPreview extends ActionBarActivity {

    private VideoView videoPreview;
    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        videoPreview = (VideoView)findViewById(R.id.videoView3);
        Intent intent = getIntent();
        videoPath = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (videoPath != null) {
            previewVideo();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_preview, menu);
        return true;
    }

    private void previewVideo() {
        try {
            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(videoPath);
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
