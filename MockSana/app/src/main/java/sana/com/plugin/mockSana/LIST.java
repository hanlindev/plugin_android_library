package sana.com.plugin.mockSana;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LIST extends ActionBarActivity {

    ListView listview;
    TextView title;
    String folderName;
    File[] files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();
        folderName = intent.getStringExtra(Intent.EXTRA_TEXT);
        listview = (ListView)findViewById(R.id.listView);
        title = (TextView)findViewById(R.id.textView1);
        title.setText(folderName);
        getFileNames();
    }

    private void getFileNames() {
        File imagePath = new File(this.getFilesDir(), "images");
        files = imagePath.listFiles();
        List<String> list = new ArrayList<String>();
        for(int i = 0; i<files.length; i++){
            list.add(files[i].getName());
        }
        ArrayAdapter<String> filenameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        listview.setAdapter(filenameAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
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
