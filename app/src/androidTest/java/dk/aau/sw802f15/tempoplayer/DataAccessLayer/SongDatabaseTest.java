package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import dk.aau.sw802f15.tempoplayer.TestHelper;

import junit.framework.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SongDatabaseTest extends AndroidTestCase {

    private SongDatabase _songDatabase = null;
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
        _songDatabase = new SongDatabase(getContext());
        TestHelper.initializeTestSongs(_songDatabase);
        _song = TestHelper.getValidSong();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        _songDatabase.clearDatabase();
        _songDatabase.close();
    }

    @MediumTest
    public void testCreateDatabaseNotExists()
    {
        try {
            _songDatabase.getWritableDatabase();
        }
        catch (SQLiteException e) {
            e.printStackTrace();
            Assert.fail("Database not created. Expected: Database created.");
        }
    }

    @MediumTest
    public void testInsertValid()
    {
        try {
            _song = _songDatabase.insertSong(TestHelper.getValidSong());
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
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
        Song song = _songDatabase.insertSong(_song);
        assertTrue(song == null);
    }

    @MediumTest
    public void testInsertMultiple()
    {
        Song song = _songDatabase.insertSong(_song);
        _song = _songDatabase.insertSong(_song);
        assertEquals(_song, song);
    }

    @MediumTest
    public void testDeleteExists(){
        Song tempSong = _songDatabase.insertSong(_song);
        assertEquals(0, _songDatabase.deleteSong(tempSong));
    }

    @MediumTest
    public void testDeleteNotExists(){
        try {
            _songDatabase.deleteSong(_song);
            Assert.fail();
        }catch (SQLiteException e)
        {
            e.printStackTrace();
        }
    }

    @MediumTest
    public void testDropDatabaseExists()
    {
        boolean actual = _songDatabase.clearDatabase();
        assertEquals(true, actual);
    }

    @MediumTest
    public void testReadEntryExists(){

        long songId = _songDatabase.insertSong(_song).getID();

        Song song = _songDatabase.getSongById(songId);

        assertTrue(song != null);

        assertEquals(_song.getSongUri(), song.getSongUri());
    }

    @MediumTest
    public void testReadEntryNotExists(){
        Song song = _songDatabase.getSongById(12312); //only 6 songs in test set
        assertTrue(song == null);
    }

    @MediumTest
    public void testReadBySongPathExist(){
        _songDatabase.insertSong(_song);

        Song song = _songDatabase.getSongByPath(_song.getSongUri());
        assertTrue(song != null);

        assertEquals(_song.getSongUri(), song.getSongUri());
    }

    @MediumTest
    public void testReadBySongPathNotExist(){
        Song song = _songDatabase.getSongByPath(Uri.parse("5.")); //only 6 songs in test set
        assertTrue(song == null);
    }

    @MediumTest
    public void testGetSongById(){
        _song = _songDatabase.getSongById(1);
        assertEquals(TestHelper.getValidSong().getSongUri(), _song.getSongUri());
    }

    @MediumTest
    public void testGetSongByPath(){
        String initMusicPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/tempo/";
        String fullPath = initMusicPath + "music_sample_1.mp3";

        _song = _songDatabase.getSongByPath(Uri.parse(fullPath));

        assertEquals(TestHelper.getValidSong().getSongUri(), _song.getSongUri());
    }

    @MediumTest
    public void testGetSongsWithBPM(){
        List<Song> songs = _songDatabase.getSongsWithBPM(110, 30);
        assertEquals(5, songs.size());

        songs = _songDatabase.getSongsWithBPM(80, 10);
        assertEquals(2, songs.size());

        songs = _songDatabase.getSongsWithBPM(30, 5);
        assertEquals(0, songs.size());

        try{
            for(Integer i : _intValues){
                _songDatabase.getSongsWithBPM(i, 10);
            }
            for (Integer i : _intValues){
                _songDatabase.getSongsWithBPM(50, i);
            }
        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }

    }

    @MediumTest
    public void testConstructSongListFromCursor(){
        SQLiteDatabase db = _songDatabase.getReadableDatabase();
        List<Song> actualSongList = new ArrayList<Song>();

        Cursor cursor = db.query("Song", new String[] {"rowid", "*"}, "bpm >= ? AND bpm <= ?",
                new String[] { String.valueOf(130 - 2), String.valueOf(130 + 2) }
                , null, null, null, null);

        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.SongDatabase,
                                                                          "constructSongListFromCursor",
                                                                          getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        try {
            actualSongList = (List<Song>) privateMethod.invoke(_songDatabase, cursor);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        assertEquals(TestHelper.getValidSong().getSongUri(), actualSongList.get(0).getSongUri());
    }

    @MediumTest
    public void testGetAllSongPaths(){

    }

    @MediumTest
    public void testDeleteSongByID(){
        int actualValue = _songDatabase.deleteSongByID(1);

        assertEquals(0, actualValue);
    }

    @MediumTest
    public void testUpdateSong(){
        Song updatedSong = _songDatabase.insertSong(_song);
        updatedSong.setAlbumUri(_song.getSongUri());
        int songID = _songDatabase.updateSong(updatedSong);
        updatedSong = _songDatabase.getSongById(songID);
        assertEquals(_song.getSongUri(), updatedSong.getSongUri());
        assertNotSame(_song.getAlbumUri(), updatedSong.getAlbumUri());
    }
}