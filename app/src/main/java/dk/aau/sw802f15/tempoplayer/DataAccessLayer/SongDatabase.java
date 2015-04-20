package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dan on 18-02-2015.
 */
public class SongDatabase extends SQLiteOpenHelper
{
    // If you change the database schema, you must increment the database version.
    Context _context;
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
            "album_path TEXT NOT NULL," +
            "duration INTEGER" +
            ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public SongDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _context = context;
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
            e.printStackTrace();
            return false;
        }
    }

    public Song insertSong(Song song) throws SQLiteException
    {
        //If song already exists
        if (song.getID() >= 0)
        {
            Toast.makeText(_context, "Song already exist.", Toast.LENGTH_SHORT).show();
            song = getSongById(song.getID());
            return song;
        }

        //Otherwise insert in db and return new song.
        ContentValues values = new ContentValues();
        try {
            values.put("title", song.getTitle());
            values.put("artist", song.getArtist());
            values.put("album", song.getAlbum());
            values.put("bpm", song.getBpm());
            values.put("path", song.getSongUri().toString());
            values.put("album_path", song.getAlbumUri().toString());
            values.put("duration", song.getDurationInSec());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_context, "Song not valid, not added to library.", Toast.LENGTH_SHORT).show();
            return null;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        try{
            long rowId = db.insertOrThrow(TABLE_NAME, null, values);
            song.setID(rowId);
        }
        catch (SQLiteException e){
            e.printStackTrace();
            Toast.makeText(_context, "Song already exists.", Toast.LENGTH_SHORT).show();
            song = getSongByPath(song.getSongUri());
        }

        db.close();

        return song;
    }

    public int deleteSong(Song song) throws SQLiteException
    {
        //If song already exists
        if (song.getID() == -1)
        {
            throw new SQLiteException("Song doesn't exist in database.");
        }

        try{
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_NAME, "ROWID = " + song.getID(), null);

            db.close();

            return 0;
        }catch (SQLiteException e)
        {
            e.printStackTrace();
            throw new SQLiteException();
        }
    }

    public Song getSongById(long songId) throws SQLiteException{
        return getSong("ROWID", songId);
    }

    public Song getSongByPath(Uri uri){
        return getSong("path", uri);
    }

    private Song getSong(String searchRow, Object searchParameter) {
        Song resultSong = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {"rowid", "*"}, searchRow + "=?",
                new String[] { String.valueOf(searchParameter) }, null, null, null, null);

        try{
            resultSong = constructSongListFromCursor(cursor).get(0);
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            //No songs by that parameter.
        }

        db.close();

        return resultSong;
    }

    public List<Song> getSongsWithBPM(int BMP, int tresholdBMP){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {"rowid", "*"}, "bpm >= ? AND bpm <= ?",
                new String[] { String.valueOf(BMP - tresholdBMP), String.valueOf(BMP + tresholdBMP) }
                , null, null, null, null);

        return constructSongListFromCursor(cursor);
    }

    private List<Song> constructSongListFromCursor(Cursor cursor){
        List<Song> resultSongs = new ArrayList<>();

        if(cursor != null)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                resultSongs.add(new Song(
                        cursor.getInt(cursor.getColumnIndex("rowid")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("artist")),
                        cursor.getString(cursor.getColumnIndex("album")),
                        cursor.getInt(cursor.getColumnIndex("bpm")),
                        Uri.parse(cursor.getString(cursor.getColumnIndex("path"))),
                        Uri.parse(cursor.getString(cursor.getColumnIndex("album_path"))),
                        cursor.getInt(cursor.getColumnIndex("duration"))));
                cursor.moveToNext();
            }
            cursor.close();
        }else {
            throw new SQLiteException();
        }
        return resultSongs;
    }

    public Map<Integer, String> getAllSongPaths() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<Integer, String> result = new HashMap<>();

        Cursor cursor = db.query(TABLE_NAME, new String[] {"ROWID ,path"}, null,
                null, null, null, null, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.put(cursor.getInt(cursor.getColumnIndex("rowid")),
                           cursor.getString(cursor.getColumnIndex("path")));
                cursor.moveToNext();
            }
            cursor.close();
        }else {
            throw new SQLiteException();
        }

        return result;
    }

    public int deleteSongByID(Integer id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, "ROWID = " + id, null);
            db.close();

            return 0;
        }catch (SQLiteException e)
        {
            e.printStackTrace();
            throw new SQLiteException();
        }
    }

    public int updateSong(Song song) {
        ContentValues values = new ContentValues();
        values.put("album_path", song.getAlbumUri().toString());
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.update(TABLE_NAME, values, "ROWID = " + song.getID(), null);
        db.close();
        return ret;
    }
}