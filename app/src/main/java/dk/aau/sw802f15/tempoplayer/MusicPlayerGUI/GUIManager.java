package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.DynamicQueue;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.SongTime;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CircleButton.CircleButton;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import dk.aau.sw802f15.tempoplayer.R;

/**
 * Created by Draegert on 06-05-2015.
 */
public class GUIManager {
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static MusicPlayerActivity _activity;
    private static CoverFlowManager _coverFlowManager;
    private static SeekBarManager _seekBarManager;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public GUIManager(MusicPlayerActivity activity) {
        _activity = activity;
        _coverFlowManager = new CoverFlowManager(_activity);
        _seekBarManager = new SeekBarManager(_activity);
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
    public static SeekBar findSeekBar() {
        return  (SeekBar)_activity.findViewById(R.id.seekBar);
    }

    public static ImageView findPlayButton() {
        return (ImageView) _activity.findViewById(R.id.playButton);
    }

    public static ImageView findPauseButton() {
        return (ImageView) _activity.findViewById(R.id.pauseButton);
    }

    public static ImageView findPreviousButton() {
        return (ImageView) _activity.findViewById(R.id.previousButton);
    }

    public static ImageView findNextButton() {
        return (ImageView) _activity.findViewById(R.id.nextButton);
    }

    public static ImageView findStopButton() {
        return (ImageView) _activity.findViewById(R.id.stopButton);
    }

    public static ImageView findSettingsButton() {
        return (ImageView) _activity.findViewById(R.id.settingsButton);
    }

    public static TextView findSongProgressText() {
        return  (TextView) _activity.findViewById(R.id.textView_currentPosition);
    }

    public static TextView findSongDurationText() {
        return  (TextView) _activity.findViewById(R.id.textView_songDuration);
    }

    public static TextView findTitleText() {
        return  (TextView) _activity.findViewById(R.id.textView_title);
    }

    public static TextView findArtistText() {
        return  (TextView) _activity.findViewById(R.id.textView_artist);
    }

    public static TextView findAlbumText() {
        return  (TextView) _activity.findViewById(R.id.textView_album);
    }

    public static TextView findSPMText() {
        return  (TextView) _activity.findViewById(R.id.textView_spm);
    }

    public static TextView findBPMText() {
        return  (TextView) _activity.findViewById(R.id.textView_bpm);
    }

    public static CoverFlow findCoverFlow() {
        return (CoverFlow) _activity.findViewById(R.id.coverflow);
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public static void showToast(String message) {
        Toast.makeText(_activity, message, Toast.LENGTH_SHORT).show();
    }

    public static void setSongProgressText(int duration) {
        TextView songDurationTextView = findSongProgressText();
        SongTime songTime = new SongTime(duration);
        songDurationTextView.setText(songTime.getFormattedSongTime());
    }

    public static void setSongDurationText(int duration) {
        TextView songDurationTextView = findSongDurationText();
        SongTime songTime = new SongTime(duration);
        songDurationTextView.setText(songTime.getFormattedSongTime());
    }

    public static void setSPMText(int spm) {
        TextView SPMTextView = findSPMText();
        String SPM = Integer.toString(spm);
        SPMTextView.setText(SPM);
    }

    public static Bitmap getBitmapFromUri(Uri albumUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if (!new File(albumUri.getPath()).exists()){
            return BitmapFactory.decodeResource(_activity.getResources(), R.drawable.defaultalbumcover);
        }
        BitmapFactory.Options optionsSecond = new BitmapFactory.Options();
        optionsSecond.inSampleSize = calculateInSampleSize(options, 350, 350);

        return BitmapFactory.decodeFile(albumUri.getPath(), optionsSecond);
    }

    public static void previousButtonSetVisibility(boolean show) {
        CircleButton button = (CircleButton) _activity.findViewById(R.id.previousButton);
        if(show){
            button.setAlpha(1f);
            button.setEnabled(true);
        }else if (DynamicQueue.getInstance(_activity).prevSongsIsEmpty()) {
            button.setAlpha(0.3f);
            button.setEnabled(false);
        }
    }

    public static void updateSongInfo() {
        Song song = DynamicQueue.getInstance(_activity).getCurrentSong();
        findTitleText().setText(song.getTitle());
        findArtistText().setText(song.getArtist());
        findAlbumText().setText(song.getAlbum());
        findBPMText().setText(song.getBpm().toString());

        setSongDurationText(song.getDurationInSec());
    }

    public static void updateSeekBarAndLabels() {
        if(_activity == null) {
            return;
        }

        final Song song = DynamicQueue.getInstance(_activity).getCurrentSong();
        SeekBar seekBar = findSeekBar();
        final TextView minLabel = findSongProgressText();
        final TextView maxLabel = findSongDurationText();
        Handler handler = new Handler();

        seekBar.setMax(song.getDurationInSec());

        handler.post(new Runnable(){
            public void run() {
                minLabel.setText("00:00");
                maxLabel.setText(song.getDurationInMinAndSec());
            }
        });
    }

    public static void changePlayPauseButton(){
        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        final ImageView playButton = findPlayButton();
                        final ImageView pauseButton = findPauseButton();
                        if (_activity.mMusicPlayerService.musicPlayer.isPlaying()) {
                            pauseButton.setVisibility(View.VISIBLE);
                            playButton.setVisibility(View.GONE);
                        }
                        else {
                            pauseButton.setVisibility(View.GONE);
                            playButton.setVisibility(View.VISIBLE);
                        }
                    }
                }, 200) ;
    }

    public static void nextAlbumCover() {
        _coverFlowManager.nextAlbumCover(findCoverFlow());
    }

    public static void previousAlbumCover() {
        _coverFlowManager.previousAlbumCover(findCoverFlow());
    }

    public static void setCoverFlowImages() {
        _coverFlowManager.setCoverFlowImages(findCoverFlow());
    }

    public static void startSeekBarPoll() {
        _seekBarManager.startSeekBarPoll();
    }

    public static void stopSeekBarPoll() {
        _seekBarManager.stopSeekBarPoll();
    }

    public static void resetSeekBar() {
        _seekBarManager.resetSeekBar();
    }
    //endregion
}
