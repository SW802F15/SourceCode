package com.example.sw802f15.tempoplayer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class SettingsActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final ListView listview = (ListView) findViewById(R.id.listView);

        

    }
}
