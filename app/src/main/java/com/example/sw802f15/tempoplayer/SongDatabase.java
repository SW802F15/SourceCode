package com.example.sw802f15.tempoplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;

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
            //"id INTEGER PRIMARY KEY," +
            "title TEXT NOT NULL," +
            "artist TEXT NOT NULL," +
            "album TEXT NOT NULL,"+
            "bpm INTEGER," +
            "path TEXT UNIQUE" +
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
        values.put("path", song.getPath());

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
}