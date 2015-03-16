package dk.aau.sw802f15.tempoplayer.MusicPlayer;

/**
 * Created by Draegert on 16-03-2015.
 */
public class SongTime {
    int _hour;
    int _minute;
    int _seconds;

    public int getHour(){ return _hour; }
    public int getMinute() { return _minute; }
    public int getSeconds() {return _seconds; }

    public void setHour(int hour) {_hour = hour; }
    public void setMinute(int minute) {_minute = minute; }
    public void setSeconds(int seconds) { _seconds = seconds; }
}
