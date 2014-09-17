package sana.com.plugin.mockApp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.hardware.BluetoothDevice;


public class MockApp extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_app);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                CommManager cm = CommManager.getInstance();
                cm.respondToIntent(intent);
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello, MockSana.");
//                sendIntent.setType("text/plain");
//                startActivity(Intent.createChooser(sendIntent, "Share text to.."));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mock_app, menu);
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

    public void testGit(){

    }
    /*
        Called when the user clicks send text button
     */
    public void sendTextToSana(View view) {
        CommManager cm = CommManager.getInstance();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType(cm.getMimeType().toString());
        startActivity(Intent.createChooser(sendIntent, "Share text to.."));
    }

    /*
        Called when the user clicks send binary data button
     */
    public void sendBinaryToSana(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share binary data to.."));
    }

    public void bluetoothRecord(){
        Intent intent = new Intent(this, AudioRecordingActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the record audio from mic button */
    public void recordAudioFromMic(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, AudioRecordActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the take photo button */
    public void takePhotoOrVideo(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, TakePhotoOrVideoActivity.class);
        startActivity(intent);

    }
}
