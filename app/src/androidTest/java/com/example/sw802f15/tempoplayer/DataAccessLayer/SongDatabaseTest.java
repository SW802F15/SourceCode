package com.example.sw802f15.tempoplayer.DataAccessLayer;

import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class SongDatabaseTest extends AndroidTestCase {

    private SongDatabase _db = null;
    private Song _song = null;
    private List<Integer> _intValues = new ArrayList<Integer>() {{
        add(Integer.MAX_VALUE);
        add(Integer.MAX_VALUE - 1);
        add(Integer.MIN_VALUE);
        add(Integer.MIN_VALUE + 1);
        add(-1);
        add(0);
        add(1);
    }};
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        _db = new SongDatabase(getContext());
        _song = new Song("TestSong", "TestArtist", "TestAlbum", null, Uri.parse("TestFilePath"), null, 2);
        initializeTestSongs();
    }

    private void initializeTestSongs() {
        String initMusicPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/";
        String initCoverPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_PICTURES + "/";

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

        _db.insertSong(test_1);
        _db.insertSong(test_2);
        _db.insertSong(test_3);
        _db.insertSong(test_4);
        _db.insertSong(test_5);
        _db.insertSong(test_6);

    }
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        _db.clearDatabase();
    }

    @MediumTest
    public void testCreateDatabaseNotExists()
    {
        try
        {
            _db.getWritableDatabase();
        }
        catch (SQLiteException e)
        {
            Assert.fail("Database not created. Expected: Database created.");
        }

    }

    @MediumTest
    public void testInsertValid()
    {
        //Insert song in db
        try
        {
            _song = _db.insertSong(_song);
        }
        catch (SQLiteException e)
        {
            Assert.fail("Got SQLiteException, expected none.");
        }

        //The updated song should have an id.
        assertTrue(_song.getID() > 0);
        assertTrue(_song.getID() >= 0);

    }

    @MediumTest
    public void testInsertInvalid()
    {
        _song.setAlbumUri(null);
        Song song = _db.insertSong(_song);
        assertTrue(song == null);
    }

    @MediumTest
    public void testInsertMultiple()
    {
        Song song = _db.insertSong(_song);
        _song = _db.insertSong(_song);
        assertEquals(_song, song);
    }

    @MediumTest
    public void testDeleteExists(){
        Song tempSong = _db.insertSong(_song);
        assertEquals(0, _db.deleteSong(tempSong));
    }

    @MediumTest
    public void testDeleteNotExists(){
        try {
            _db.deleteSong(_song);
            Assert.fail();
        }catch (SQLiteException ex)
        {
            // do nothing
        }
    }

    @MediumTest
    public void testDropDatabaseExists()
    {
        boolean actual = _db.clearDatabase();
        assertEquals(true, actual);
    }

    @MediumTest
    public void testReadEntryExists(){

        long songId = _db.insertSong(_song).getID();

        Song song = _db.readEntryById(songId);

        assertTrue(song != null);

        assertEquals(_song, song);
    }

    @MediumTest
    public void testReadEntryNotExists(){
        Song song = _db.readEntryById(12312); //only 6 songs in test set
        assertTrue(song == null);
    }

    @MediumTest
    public void testReadBySongPathExist(){
        _db.insertSong(_song);

        Song song = _db.readBySongPath(_song.getUri());
        assertTrue(song != null);

        assertEquals(_song, song);
    }

    @MediumTest
    public void testReadBySongPathNotExist(){

        Song song = _db.readBySongPath(Uri.parse("5.")); //only 6 songs in test set
        assertTrue(song == null);
    }

    @MediumTest
    public void testGetSongsWithBPM(){
        //int BMP, int tresholdBMP
        List<Song> songs = _db.getSongsWithBPM(110, 30);
        assertEquals(5, songs.size());

        songs = _db.getSongsWithBPM(80, 10);
        assertEquals(2, songs.size());

        songs = _db.getSongsWithBPM(30, 5);
        assertEquals(0, songs.size());

        try{
            for(Integer i : _intValues){
                _db.getSongsWithBPM(i,10);
            }
            for (Integer i : _intValues){
                _db.getSongsWithBPM(50, i);
            }
        }catch (Exception ignored){
            Assert.fail();
        }

    }
}