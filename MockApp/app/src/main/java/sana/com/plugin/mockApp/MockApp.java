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

import java.util.concurrent.TimeUnit;

public class MockApp extends ActionBarActivity {

    final Context context = this;

    String message = "";

    public class UsbListener extends TimedListener {

        Object sender;

        public UsbListener(Object sender, long interval, TimeUnit unit) {
            super(sender, interval, unit);
        }

        @Override
        public void setExpectedSender(Object sender) {
            this.sender = sender;
        }

        @Override
        public void processData(Object sender, Object[] data) {
            Log.d("Herereerere","");
            for (int i=0;i<data.length;i++)
                message += data[i].toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_app);

        //11111111111111111111111111111111111111111111111111111111111//

        final UsbAccessoryDevice accessoryDevice = new UsbAccessoryDevice(this);
        final DataWithEvent dataEvent = accessoryDevice.prepare();
        //accessoryDevice.begin();

        UsbListener listener = new UsbListener(this, 1000, TimeUnit.MILLISECONDS);

        if (dataEvent != null && dataEvent.getEvent()!= null)
            dataEvent.getEvent().addListener(listener);

        Button bt = (Button) findViewById(R.id.record_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("recording...");
                //accessoryDevice.begin();
                alert.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        accessoryDevice.stop();
                        Toast.makeText(context, "MESSAGE IS |" + message + "|", Toast.LENGTH_LONG).show();
                    }
                });
                alert.show();
            }
        });

        //22222222222222222222222222222222222222222222222222222222222222//

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello, MockSana.");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share text to.."));
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

    /*
        Called when the user clicks send text button
     */
    public void sendTextToSana(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello, MockSana.");
        sendIntent.setType("text/plain");
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
}