package dk.aau.sw802f15.tempoplayer.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScanner;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import dk.aau.sw802f15.tempoplayer.R;


public class SettingsActivity extends PreferenceActivity {

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        setContentView(R.layout.activity_settings);


        SongDatabase songDatabase = new SongDatabase(this);
        SongScanner.getInstance(this).findSongs();

        List<Song> songs = songDatabase.getSongsWithBPM(100, 1000, 0);

        if(!(songs.size() >= MusicPlayerActivity.getMINIMUM_SONGS_REQUIRED())){
            Toast.makeText(this, "Not enough songs in the folder.", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

}
