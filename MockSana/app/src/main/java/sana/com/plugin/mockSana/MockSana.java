package sana.com.plugin.mockSana;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.support.v4.content.FileProvider.getUriForFile;


public class MockSana extends ActionBarActivity {

    static final int IMAGE_REQUEST = 0;
    static final int TEXT_DATA_REQUEST = 1;
    static final int AUDIO_REQUEST = 2;
    static final int VIDEO_REQUEST = 3;
    static final String ERROR_MESSAGE_FORMAT = "Mimetype %s is wrong, %s expected";

    private RadioButton textRadio;
    private RadioButton shakeRadio;
    private RadioButton imageRadio;
    private RadioButton audioRadio;
    private RadioButton videoRadio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_sana);

        File imagePath = new File(this.getFilesDir(), "images");
        File audioPath = new File(this.getFilesDir(), "audio");
        File videoPath = new File(this.getFilesDir(), "videos");

        imagePath.mkdir();
        audioPath.mkdir();
        videoPath.mkdir();

        textRadio = (RadioButton)findViewById(R.id.radioButton1);
        imageRadio = (RadioButton)findViewById(R.id.radioButton2);
        audioRadio = (RadioButton)findViewById(R.id.radioButton3);
        videoRadio = (RadioButton)findViewById(R.id.radioButton4);
        shakeRadio = (RadioButton)findViewById(R.id.radioShaking);
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // show captured text on the screen
//            showToast("Heart rate is: " + sharedText);
            TextView heartRateView = (TextView)findViewById(R.id.textView3);
            heartRateView.setText("Heart rate is: " + sharedText);
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
        switch (requestCode) {
            case TEXT_DATA_REQUEST:
                if (resultCode == RESULT_OK) {
                    //react when plain text is received
                    System.out.println("++++++++++++++++Text data intent received.");
                    handleSendText(data);
                }
                break;
            case IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    System.out.println("++++++++++++++++Binary data intent received.");
                    showToast("Image saved.");
                }
                break;
            case AUDIO_REQUEST:
                if (resultCode == RESULT_OK) {
                    System.out.println("++++++++++++++++Binary data intent received.");
                    showToast("Audio saved.");
                }
                break;
            case VIDEO_REQUEST:
                if (resultCode == RESULT_OK) {
                    System.out.println("++++++++++++++++Binary data intent received.");
                    showToast("Video saved.");
                }
                break;
            default:
                showToast("Unexpected intent!");
                break;
        }
    }

    /*
        called when the user clicks the launch button
     */
    public void launchMockApp(View view) {
        if (textRadio.isChecked()) {
            launchMockAppWithRequiredText("com.example.heart_rate_monitor.HEART_BEAT");
        }
        else if (imageRadio.isChecked()) {
            createLaunchIntent("sana.com.plugin.mockApp.PICTURE", "image/jpeg" , "jpg", "images", IMAGE_REQUEST);
        }
        else if (audioRadio.isChecked()) {
            System.out.println("++++++++++++++++++++++++audio intent launched");
            createLaunchIntent("sana.com.plugin.mockApp.AUDIO", "audio/3gpp" , "3gp", "audio", AUDIO_REQUEST);
        }
        else if (videoRadio.isChecked()) {
            createLaunchIntent("sana.com.plugin.mockApp.VIDEO", "video/3gpp" , "3gp", "videos", VIDEO_REQUEST);
        }
        else if (shakeRadio.isChecked()) {
            launchMockAppWithRequiredText("com.sana.android.plugin.examples.SHAKING_PATTERN");
        }
        else {
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("sana.com.plugin.mockApp");
            startActivity(LaunchIntent);
        }
    }

    private void launchMockAppWithRequiredText(String action) {
        Intent LaunchIntent = new Intent();
        LaunchIntent.setAction(action);
        LaunchIntent.setType("text/plain");
        startActivityForResult(LaunchIntent, TEXT_DATA_REQUEST);
    }

    private String generateRandomFileName(String ext) {
        String fileName = String.format("%s.%s", UUID.randomUUID().toString().substring(0, 8), ext);
        return fileName;
    }

    private void createLaunchIntent(String action, String type, String ext, String subfolder, int requestCode) {
        Intent LaunchIntent = new Intent();
        LaunchIntent.setAction(action);
        LaunchIntent.setType(type);
        Uri contentUri = getContentUri(generateRandomFileName(ext), subfolder);
        System.out.println("---------------------------" + getContentResolver().getType(contentUri));
        grantUriPermission("sana.com.plugin.mockApp", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        grantUriPermission("sana.com.plugin.mockApp", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        LaunchIntent.setData(contentUri);
        if (LaunchIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(LaunchIntent, requestCode);
        } else {
            showToast("No activity in MockApp handle this intent.");
        }
    }

    public void readData(View view) {
        Intent intent = new Intent("sana.com.plugin.mockSana.READING");
        startActivity(intent);
    }

    private Uri getContentUri(String fileName, String folder) {
        File imagePath = new File(this.getFilesDir(), folder);
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
