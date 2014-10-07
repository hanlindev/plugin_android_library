package sana.com.plugin.mockApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sana.android.plugin.*;
import com.sana.android.plugin.data.*;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.hardware.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;


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

    private static final String BLUETOOTH_ERROR_TITLE = "Bluetooth not connected!";
    private static final String BUILTIN_ERROR_TITLE = "Bluetooth connected!";
    private static final String BLUETOOTH_ERROR_MESSAGE= "Please go to settings, turn on bluetooth and try to pair with a bluetooth mic";
    private static final String BUILTIN_ERROR_MESSAGE= "System detects a connected bluetooth device. Please use bluetooth mic to record.";
    private FeatureChecker fc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fc = new FeatureChecker(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_app);

        // Get intent
        Intent intent = getIntent();
        CommManager cm = CommManager.getInstance();
        cm.respondToIntent(intent);
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

    /**
     * Called when the user clicks send text button
     */
    public void sendTextToSana(View view) {
        CommManager cm = CommManager.getInstance();
        cm.sendData(this, getSendText());
    }

    /**
     * Capture plain text
     */
    private String getSendText() {
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        return message;
    }

    /**
     * Show toast on the screen, can be error message
     * @param message
     */
    private void showToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Called when the user clicks send binary data button
     */
    public void sendBinaryToSana(View view) {
        CommManager cm = CommManager.getInstance();
        writeDataToUri(cm);
        System.out.println("Byte array write already");
        cm.sendData(this);
        System.out.println("Intent send back already");
    }

    private void writeDataToUri(CommManager cm) {
        OutputStream os = null;
        try{
            os = getContentResolver().openOutputStream(cm.getUri());
            byte[] byteData = new byte[]{1,1,1,1,1,1,1,1,1,1,0};
            try {
                os.write(byteData);
            } catch (IOException e) {
                System.out.println("+++++++++++++++++++++++Fail write to file uri!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("++++++++++++++++++++++++++++++File not found!");
        } finally {
            if (os != null) try {
                os.close();
            } catch (IOException e) {
                showToast("IO exception");
            }
        }
    }

    public void bluetoothRecord(View view){
        if(fc.isConnected(Feature.BLUETOOTH)){
            Intent intent = new Intent(this, BluetoothRecordingActivity.class);
            startActivity(intent);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(BLUETOOTH_ERROR_TITLE)
                    .setMessage(BLUETOOTH_ERROR_MESSAGE)
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
        if(!fc.isConnected(Feature.BLUETOOTH)){
            Intent intent = new Intent(this, AudioRecordActivity.class);
            startActivity(intent);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(BUILTIN_ERROR_TITLE)
                    .setMessage(BUILTIN_ERROR_MESSAGE)
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

    // Called when accessory record button is clicked
    public void accessoryRecord(View view) {
        Intent intent = new Intent(this, UsbAccessoryRecordActivity.class);
        startActivity(intent);
    }

    /**
     * Called when start accelerometer button is clicked
     * @param view
     */
    public void startAccelerometer(View view) {
        Intent intent = new Intent(this, AccelerometerActivity.class);
        startActivity(intent);
    }
}
