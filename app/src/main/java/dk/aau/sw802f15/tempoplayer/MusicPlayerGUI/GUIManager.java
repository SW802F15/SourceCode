package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
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
import dk.aau.sw802f15.tempoplayer.R;

/**
 * Created by Draegert on 06-05-2015.
 */
public class GUIManager {
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static GUIManager instance = null;
    private static MusicPlayerActivity _activity;
    private static CoverFlowManager _coverFlowManager;
    private static SeekBarManager _seekBarManager;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    protected GUIManager(){
        //Empty because singleton
    }

    public static GUIManager getInstance(Context context){
        if (instance == null){
            _activity = (MusicPlayerActivity) context;
            _seekBarManager = new SeekBarManager(_activity);
            _coverFlowManager = new CoverFlowManager(_activity);

            instance = new GUIManager();
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
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
    public SeekBar findSeekBar() {
        return (SeekBar)_activity.findViewById(R.id.seekBar);
    }

    public ImageView findPlayButton() {
        return (ImageView) _activity.findViewById(R.id.playButton);
    }

    public ImageView findPauseButton() {
        return (ImageView) _activity.findViewById(R.id.pauseButton);
    }

    public ImageView findPreviousButton() {
        return (ImageView) _activity.findViewById(R.id.previousButton);
    }

    public ImageView findNextButton() {
        return (ImageView) _activity.findViewById(R.id.nextButton);
    }

    public ImageView findStopButton() {
        return (ImageView) _activity.findViewById(R.id.stopButton);
    }

    public ImageView findSettingsButton() {
        return (ImageView) _activity.findViewById(R.id.settingsButton);
    }

    public TextView findSongProgressText() {
        return  (TextView) _activity.findViewById(R.id.textView_currentPosition);
    }

    public TextView findSongDurationText() {
        return  (TextView) _activity.findViewById(R.id.textView_songDuration);
    }

    public TextView findTitleText() {
        return  (TextView) _activity.findViewById(R.id.textView_title);
    }

    public TextView findArtistText() {
        return  (TextView) _activity.findViewById(R.id.textView_artist);
    }

    public TextView findAlbumText() {
        return  (TextView) _activity.findViewById(R.id.textView_album);
    }

    public TextView findSPMText() {
        return  (TextView) _activity.findViewById(R.id.textView_spm);
    }

    public TextView findBPMText() {
        return  (TextView) _activity.findViewById(R.id.textView_bpm);
    }

    public CoverFlow findCoverFlow() {
        return (CoverFlow) _activity.findViewById(R.id.coverflow);
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public void showToast(String message) {
        Toast.makeText(_activity, message, Toast.LENGTH_SHORT).show();
    }

    public void setSongProgressText(int duration) {
        TextView songDurationTextView = findSongProgressText();
        SongTime songTime = new SongTime(duration);
        songDurationTextView.setText(songTime.getFormattedSongTime());
    }

    public void setSongDurationText(int duration) {
        TextView songDurationTextView = findSongDurationText();
        SongTime songTime = new SongTime(duration);
        songDurationTextView.setText(songTime.getFormattedSongTime());
    }

    public void setSPMText(int spm) {
        TextView SPMTextView = findSPMText();
        String SPM = Integer.toString(spm);
        SPMTextView.setText(SPM);
    }

    public Bitmap getBitmapFromUri(Uri albumUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if (!new File(albumUri.getPath()).exists()){
            return BitmapFactory.decodeResource(_activity.getResources(), R.drawable.defaultalbumcover);
        }
        BitmapFactory.Options optionsSecond = new BitmapFactory.Options();
        optionsSecond.inSampleSize = calculateInSampleSize(options, 350, 350);

        return BitmapFactory.decodeFile(albumUri.getPath(), optionsSecond);
    }

    public void previousButtonSetVisibility(boolean show) {
        CircleButton button = (CircleButton) _activity.findViewById(R.id.previousButton);
        if(show){
            button.setAlpha(1f);
            button.setEnabled(true);
        }else if (DynamicQueue.getInstance(_activity).prevSongsIsEmpty()) {
            button.setAlpha(0.3f);
            button.setEnabled(false);
        }
    }

    public void updateSongInfo() {
        Song song = DynamicQueue.getInstance(_activity).getCurrentSong();
        findTitleText().setText(song.getTitle());
        findArtistText().setText(song.getArtist());
        findAlbumText().setText(song.getAlbum());
        findBPMText().setText(song.getBpm().toString());

        setSongDurationText(song.getDurationInSec());
    }

    public void updateSeekBarAndLabels() {
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

    public void changePlayPauseButton(){
        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        final ImageView playButton = findPlayButton();
                        final ImageView pauseButton = findPauseButton();
                        if (_activity.mMusicPlayerService.isPlaying()) {
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

    public void nextAlbumCover() {
        _coverFlowManager.nextAlbumCover(findCoverFlow());
    }

    public void previousAlbumCover() {
        _coverFlowManager.previousAlbumCover(findCoverFlow());
    }

    public void setCoverFlowImages() {
        _coverFlowManager.setCoverFlowImages(findCoverFlow());
    }

    public void startSeekBarPoll() {
        _seekBarManager.startSeekBarPoll();
    }

    public void stopSeekBarPoll() {
        _seekBarManager.stopSeekBarPoll();
    }

    public void resetSeekBar() {
        _seekBarManager.resetSeekBar();
    }
    //endregion
}