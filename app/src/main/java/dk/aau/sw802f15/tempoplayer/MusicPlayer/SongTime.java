package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.text.format.Time;

/**
 * Created by Draegert on 16-03-2015.
 */
public class SongTime {
    private int _hour = 0;
    private int _minute = 0;
    private int _seconds = 0;
/*
    public int getHour(){ return _hour; }
    public int getMinute() { return _minute; }
    public int getSeconds() {return _seconds; }

    public void setHour(int hour) {_hour = hour; }
    public void setMinute(int minute) {_minute = minute; }
    public void setSeconds(int seconds) { _seconds = seconds; }
*/
    public SongTime(int duration) {
        if (duration < 0) {
            throw new IndexOutOfBoundsException("Duration of song must be 0 or longer.");
        }

        if (duration >= 3600) {
            _hour = duration / 3600;
            duration = duration % 3600;
        }
        if (duration >= 60) {
            _minute = duration / 60;
            duration = duration % 60;
        }
        _seconds = duration;
    }

    public String getFormattedSongTime() {
        String formattedSongTime;
        Time time = new Time();
        time.hour = _hour;
        time.minute = _minute;
        time.second = _seconds;

        if (time.hour > 0) {
            formattedSongTime = time.format("%H:%M:%S");
        }
        else {
            formattedSongTime = time.format("%M:%S");
        }

        return formattedSongTime;
    }
}
