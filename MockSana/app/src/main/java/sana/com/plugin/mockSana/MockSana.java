package sana.com.plugin.mockSana;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.sana.android.db.SanaDB;


public class MockSana extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_sana);

    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // show captured text on the screen
            showToast(sharedText);
        }
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

    private void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
//            setContentView(R.layout.main);
            ImageView imgView = (ImageView) findViewById(R.id.imageView2);
            imgView.setImageURI(imageUri);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mock_sana, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("++++++++++++onActivityResult Called++++++++++++");
        String type = data.getType();
        if (type.equals("text/plain")) {
            //react when plain text is received
            handleSendText(data);
        }
        else if (type.startsWith("image/")) {
            //react when image is received
            showToast("Binary data intent received.");
//            handleSendImage(data);
        } else {
            showToast(String.format("Mimetype %s is not handled in MockSana", type));
        }
    }

    /*
        called when the user clicks the launch button
     */
    public void launchMockApp(View view) {
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("sana.com.plugin.mockApp");
        startActivity(LaunchIntent);
    }

    public void launchMockAppWithRequiredText(View view) {
        Intent LaunchIntent = new Intent();
        LaunchIntent.setAction("sana.com.plugin.mockApp.HEART_BEAT");
        LaunchIntent.setType("text/plain");
        startActivityForResult(LaunchIntent, 1);
    }

    public void launchMockAppWithRequiredImage(View view) {
        Intent LaunchIntent = new Intent();
        LaunchIntent.setAction("sana.com.plugin.mockApp.PICTURE");
        LaunchIntent.setType("image/jpg");
        startActivityForResult(LaunchIntent, 1);
    }

    public void testButton(View view) {
        ContentValues values = new ContentValues();
        String procedureId = "testProcedure";
        values.put(SanaDB.SoundSQLFormat.ENCOUNTER_ID,
                procedureId);
        values.put(SanaDB.SoundSQLFormat.ELEMENT_ID, "testElement");
        Uri recording =
                getApplicationContext().getContentResolver().insert(
                        SanaDB.SoundSQLFormat.CONTENT_URI, values);
            showToast(recording.toString());
    }

}
