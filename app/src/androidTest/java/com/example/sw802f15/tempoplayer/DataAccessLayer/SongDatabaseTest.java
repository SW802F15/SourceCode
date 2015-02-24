package com.example.sw802f15.tempoplayer.DataAccessLayer;

import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.sql.SQLClientInfoException;

public class SongDatabaseTest extends AndroidTestCase {

    private SongDatabase _db = null;
    private Song _song = null;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        _db = new SongDatabase(getContext());
        _song = new Song("TestSong", "TestArtist", "TestAlbum", Uri.parse("TestFilePath"), 2);
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
        assertEquals(1, _song.getID());
        assertTrue(_song.getID() >= 0);

    }

    @MediumTest
    public void testInsertInvalid()
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

        //Try to insert again. Expect error.
        try
        {
            _db.insertSong(_song);
            Assert.fail("Expected SQLiteException, got none.");
        }
        catch (SQLiteException e)
        {
            //Do nothing
        }
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

        assertTrue(_song.getTitle().equals(song.getTitle()));
    }

    @MediumTest
    public void testReadEntryNotExists(){
        try{
            Song song = _db.readEntryById(12312);
            Assert.fail();
        }catch (SQLiteException ex)
        {
           // do nothing
        }

    }
}