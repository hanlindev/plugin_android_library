package sana.com.plugin.mockApp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.sana.android.plugin.application.CommManager;
//import com.sana.android.plugin.hardware.BluetoothDevice;
import android.bluetooth.BluetoothDevice;

public class MockApp extends ActionBarActivity {

    private static final String TAG = "BluetoothRecordTest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_app);
        // zhaoyue's code start here
        //setUpReceiver();
        // zhaoyue's code end here

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
    //zhaoyue's code to set up receiver
    /*
    private void setUpReceiver(){
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);
    }
    private static boolean bluetoothConnected = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            bluetoothConnected= BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)?true:false;
            context.unregisterReceiver(this);
        }
    };
    */
    //zhaoyue's bluetooth code finishes here
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

    // called when the user clicks the record from bluetooth mic button
    private static boolean bluetoothConnected = false;
    private void testBluetoothMic(){
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                bluetoothConnected= BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)?true:false;
                context.unregisterReceiver(this);
            }
        };
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);
    }
    public void bluetoothRecord(View view){
        testBluetoothMic();
        if(bluetoothConnected){
            Intent intent = new Intent(this, BluetoothRecordingActivity.class);
            startActivity(intent);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth Not Connected")
                    .setMessage("Please go to settings, turn on bluetooth and try to pair with a bluetooth mic")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /** Called when the user clicks the record audio from mic button */
    public void recordAudioFromMic(View view) {
        // Do something in response to button
        testBluetoothMic();
        if(!bluetoothConnected) {
            Intent intent = new Intent(this, AudioRecordActivity.class);
            startActivity(intent);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth Connected!")
                    .setMessage("System detects a connected bluetooth device. Please use bluetooth mic to record.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /** Called when the user clicks the take photo button */
    public void takePhotoOrVideo(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, TakePhotoOrVideoActivity.class);
        startActivity(intent);
    }
}
