package dk.aau.sw802f15.tempoplayer.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScanner;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import dk.aau.sw802f15.tempoplayer.R;


public class SettingsActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        final ListView listview = (ListView) findViewById(R.id.listView);

        SongDatabase db = new SongDatabase(this);
        SongScanner.getInstance(this).findSongs();
        Button tempBackBtn = (Button) findViewById(R.id.tempBack);

        List<Song> songs = db.getSongsWithBPM(100, 1000);

        if(songs.size() >= MusicPlayerActivity.MINIMUM_SONGS_REQUIRED){
            tempBackBtn.setEnabled(true);
        }
        else{
            Toast.makeText(this, "Not enough songs in the folder.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openMusicPlayer(View v)
    {
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        startActivity(intent);
    }
}
