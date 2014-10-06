package sana.com.plugin.mockSana;

import android.app.Activity;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.support.v4.content.FileProvider.getUriForFile;


public class MockSana extends ActionBarActivity {

    static final int BINARY_DATA_REQUEST = 0;
    static final int TEXT_DATA_REQUEST = 1;
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

    private void handleSendBinary(Intent intent) {
        Uri imageUri = (Uri) intent.getData();
        if (imageUri != null) {
            // Update UI to reflect image being shared
//            setContentView(R.layout.main);
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                showToast("File not found");
            }
            byte[] data = new byte[11];
            if (is != null) {
                try {
                    int count = is.read(data, 0, 11);
                    is.close();
                } catch (IOException e) {
                    showToast("IO exception");
                }
            }
            System.out.println(data);
        }
        showToast("binary data recieved");
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
        if (requestCode == BINARY_DATA_REQUEST) {
            if (resultCode == RESULT_OK) {
                System.out.println("++++++++++++++++Binary data intent received.");
                handleSendBinary(data);
            }
        }
        else if (requestCode == TEXT_DATA_REQUEST) {
            if (resultCode == RESULT_OK) {
                String type = data.getType();
                if (type.equals("text/plain")) {
                    //react when plain text is received
                    System.out.println("++++++++++++++++Text data intent received.");
                    handleSendText(data);
                } else {
                    showToast(String.format("Mimetype %s is wrong, text/plain expected", type));
                }
            }
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
        startActivityForResult(LaunchIntent, TEXT_DATA_REQUEST);
    }

    private String generateRandomFileName(String ext) {
        String fileName = String.format("%s.%s", UUID.randomUUID().toString().substring(0, 8), ext);
        return fileName;
    }

    public void launchMockAppWithRequiredBinary(View view) {
        Intent LaunchIntent = new Intent();
        LaunchIntent.setAction("sana.com.plugin.mockApp.PICTURE");
        LaunchIntent.setType("image/jpeg");
        Uri contentUri = getContentUri(generateRandomFileName("jpg"));
        grantUriPermission("sana.com.plugin.mockApp", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        LaunchIntent.setData(contentUri);
        if (LaunchIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(LaunchIntent, BINARY_DATA_REQUEST);
        } else {
            showToast("No activity in MockApp handle this intent.");
        }
    }

    public void readData(View view) {
        Intent intent = new Intent("sana.com.plugin.mockSana.READING");
        startActivity(intent);
    }

    private Uri getContentUri(String fileName) {
        File imagePath = new File(this.getFilesDir(), "images");
        File newFile = new File(imagePath, fileName);
        try {
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
        } catch (IOException e) {
            System.out.println("++++++++++++++++ failed creating new file");
        }
        Uri contentUri = getUriForFile(this, "sana.com.plugin.mockSana.fileprovider", newFile);
        return contentUri;
    }

}
