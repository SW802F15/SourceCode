package dk.aau.sw802f15.tempoplayer.Settings;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import dk.aau.sw802f15.tempoplayer.R;


public class SettingsActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final ListView listview = (ListView) findViewById(R.id.listView);

        

    }
}
