package dk.aau.sw802f15.tempoplayer;


import android.net.Uri;
import android.os.Environment;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;

public class TestHelper {
    public static void initializeTestSongs(SongDatabase db) {
        db.clearDatabase();

        String initMusicPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/tempo/";
        String initCoverPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_PICTURES + "/tempo/";

        Song test_1 = new Song("Tristram", "Matt Uemen", //Title, Artist
                "Diablo SoundTrack", 130,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_1.mp3"),
                Uri.parse(initCoverPath + "cover_sample_1.jpg"), //SongUri, CoverUri
                7*60 + 40);

        Song test_2 = new Song("Let It Go", "Idina Menzel", //Title, Artist
                "Frozen SoundTrack", 137,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_2.mp3"),
                Uri.parse(initCoverPath + "cover_sample_2.jpg"), //SongUri, CoverUri
                3*60 + 40);

        Song test_3 = new Song("Runnin'", "Adam Lambert", //Title, Artist
                "Trespassing", 81,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_3.mp3"),
                Uri.parse(initCoverPath + "cover_sample_3.jpg"), //SongUri, CoverUri
                3*60 + 48);

        Song test_4 = new Song("Sex on Fire", "Kings of Leon", //Title, Artist
                "Only by the Night", 81,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_4.mp3"),
                Uri.parse(initCoverPath + "cover_sample_4.jpg"), //SongUri, CoverUri
                3*60 + 26);

        Song test_5 = new Song("T.N.T.", "AC/DC", //Title, Artist
                "T.N.T.", 126,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_5.mp3"),
                Uri.parse(initCoverPath + "cover_sample_5.jpg"), //SongUri, CoverUri
                3*60 + 34);

        Song test_6 = new Song("Still Counting", "Volbeat", //Title, Artist
                "Guitar Gangstars & Cadillac Blood", null,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_6.mp3"),
                Uri.parse(initCoverPath + "cover_sample_6.jpg"), //SongUri, CoverUri
                4*60 + 21);

        db.insertSong(test_1);
        db.insertSong(test_2);
        db.insertSong(test_3);
        db.insertSong(test_4);
        db.insertSong(test_5);
        db.insertSong(test_6);
    }
}

