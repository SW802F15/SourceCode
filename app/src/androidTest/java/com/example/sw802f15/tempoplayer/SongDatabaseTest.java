package com.example.sw802f15.tempoplayer;

import android.database.sqlite.SQLiteException;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

public class SongDatabaseTest extends AndroidTestCase {

    private SongDatabase _db = null;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        _db = new SongDatabase(getContext());
        _db.onCreate(_db.getWritableDatabase());
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
        Song song = new Song("TestSong", "TestArtist", "TestAlbum", "TestFilePath");

        //Insert song in db
        try
        {
            song = _db.insertSong(song);
        }
        catch (SQLiteException e)
        {
            Assert.fail("Got SQLiteException, expected none.");
        }

        //The updated song should have an id.
        assertEquals(1, song.getID());
        assertTrue(song.getID() >= 0);

    }

    @MediumTest
    public void testInsertInvalid()
    {
        Song song = new Song("TestSong", "TestArtist", "TestAlbum", "TestFilePath");

        //Insert song in db
        try
        {
            song = _db.insertSong(song);

        }
        catch (SQLiteException e)
        {
            Assert.fail("Got SQLiteException, expected none.");
        }

        //Try to insert again. Expect error.
        try
        {
            _db.insertSong(song);
            Assert.fail("Expected SQLiteException, got none.");
        }
        catch (SQLiteException e)
        {
            //Do nothing
        }
    }

    @MediumTest
    public void testDeleteExists(){}

    @MediumTest
    public void testDeleteNotExists(){}

    @MediumTest
    public void testDropDatabaseExists()
    {
        boolean actual = _db.clearDatabase();
        assertEquals(true, actual);
    }

    @MediumTest
    public void testReadEntryExists(){}

    @MediumTest
    public void testReadEntryNotExists(){}
}