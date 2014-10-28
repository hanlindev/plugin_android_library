package sana.com.plugin.mockApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.event.BaseDataEvent;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.hardware.UsbHostDevice;

import java.util.concurrent.TimeUnit;


public class UsbHostRecordActivity extends ActionBarActivity {

    final Context context = this;

    private String message = "";

    private class UsbListener extends TimedListener {

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
            for (int i=0;i<data.length;i++)
                message += (char)Integer.parseInt(data[i].toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_host_record);

        final UsbHostDevice device = new UsbHostDevice(this);
        final DataWithEvent dataWithEvent = device.prepare();


        UsbListener listener = new UsbListener(device, 100, TimeUnit.MILLISECONDS);
        listener.startListening();

        if (dataWithEvent != null && dataWithEvent.getEvent()!= null) {
            dataWithEvent.getEvent().addListener(listener);
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("recording...");
        device.begin();
        alert.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                device.stop();
            }
        });
        alert.show();
    }

    public void displayMessage(View view) {
        TextView tv = (TextView) findViewById(R.id.caption);
        tv.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.usb_host_record, menu);
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
