package com.example.sw802f15.tempoplayer.DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;

import java.io.File;

/**
 * Created by Dan on 18-02-2015.
 */
public class SongDatabase extends SQLiteOpenHelper
{
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TempoPlayer.db";
    public static final String TABLE_NAME = "Song";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
            //"songid INTEGER PRIMARY KEY," +
            "title TEXT NOT NULL," +
            "artist TEXT NOT NULL," +
            "album TEXT NOT NULL,"+
            "bpm INTEGER," +
            "path TEXT UNIQUE," +
            "duration INTEGER" +
            ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public SongDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /**
     * Drops the Songs table.
     * */
    public boolean clearDatabase()
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES);
            db.close();
            return true;
        }
        catch(SQLiteException e)
        {
            //TODO: Display error message
            return false;
        }
    }

    public Song insertSong(Song song) throws SQLiteException
    {
        //If song already exists
        if (song.getID() >= 0)
        {
            throw new SQLiteException("Song already exists in database.");
        }

        //Otherwise insert in db and return new song.
        ContentValues values = new ContentValues();
        values.put("title", song.getTitle());
        values.put("artist", song.getArtist());
        values.put("album", song.getAlbum());
        values.put("bpm", song.getBpm());
        values.put("path", song.getUri().toString());
        values.put("duration", song.getDurationInSec());

//        SQLiteDatabase db1 = this.getReadableDatabase();

//        Cursor c = db1.rawQuery("SELECT * FROM Songs",null);
//
//        c.moveToFirst();

        SQLiteDatabase db = this.getWritableDatabase();

        long rowId = db.insertOrThrow(TABLE_NAME, null, values);

        db.close();

        song.setID(rowId);

        return song;

    }

    public int deleteSong(Song song) throws SQLiteException
    {
        //If song already exists
        if (song.getID() == -2)
        {
            throw new SQLiteException("Song doesn't exist in database.");
        }

        try{
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_NAME, "ROWID = " + song.getID(), null);

            db.close();

            return 0;
        }catch (SQLiteException ex)
        {
            throw new SQLiteException();
        }
    }

    public Song readEntryById(long songId) throws SQLiteException{
        Song resultSong = null;

        try{
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_NAME, new String[] {"*"}, "ROWID" + "=?",
                    new String[] { String.valueOf(songId) }, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();
            if(cursor != null && cursor.moveToFirst())
            {
                cursor.moveToFirst();
                resultSong = new Song(songId,
                           cursor.getString(cursor.getColumnIndex("title")),
                           cursor.getString(cursor.getColumnIndex("artist")),
                           cursor.getString(cursor.getColumnIndex("album")),
                           cursor.getInt(cursor.getColumnIndex("bpm")),
                           Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex("path")))),
                           cursor.getInt(cursor.getColumnIndex("duration")));
                cursor.close();
            }else {
                throw new SQLiteException();
            }
            db.close();

        }catch (SQLiteException ex)
        {
            throw new SQLiteException();
        }

        return resultSong;
    }
}