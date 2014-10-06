package sana.com.plugin.mockSana;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class READING extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ListView listview;
    String[] filenames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        listview = (ListView)findViewById(R.id.listView);
        listview.setOnItemClickListener(this);
        getFileNames();
    }

    private void getFileNames() {
        filenames = getApplicationContext().fileList();
        List<String> list = new ArrayList<String>();
        for(int i = 0; i<filenames.length; i++){
            list.add(filenames[i]);
        }
        ArrayAdapter<String> filenameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        listview.setAdapter(filenameAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reading, menu);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (filenames[i].indexOf(".") == -1) {
            Intent intent = new Intent("sana.com.plugin.mockSana.LIST");
            intent.putExtra(Intent.EXTRA_TEXT, filenames[i]);
            startActivity(intent);
        } else {
            showToast("No preview available.");
        }
    }

    private void showToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
