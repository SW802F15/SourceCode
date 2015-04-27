package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CircleButton.CircleButton;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Draegert on 26-02-2015.
 */
public class Initializers {

    public static MusicPlayerActivity _activity;
    private boolean _isPlaying = false;
    private final int POLL_RATE = 100;

    public Initializers(MusicPlayerActivity activity) {
        _activity = activity;
    }

    public void initializeOnClickListeners() {
        initializeOnClickPlay();
        initializeOnClickPause();
        initializeOnClickStop();
        initializeOnClickPrevious();
        initializeOnClickNext();
        initializeOnClickSettings();
        initializeOnClickSeekBar();
    }

    private void initializeOnClickPlay() {
        final ImageView playButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.play();
                changePlayPauseButton();
                startSeekBarPoll();
            }
        });
    }



    private void initializeOnClickPause() {
        final ImageView pauseButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.pause();
                changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickStop() {
        ImageView stopButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.stopButton);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSeekBar();
                _activity.mMusicPlayerService.stop();
                changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickPrevious() {
        final ImageView previousButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.previousButton);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.previous();
                _activity.setSongDurationText(DynamicQueue.getInstance(_activity).getCurrentSong().getDurationInSec());
                changePlayPauseButton();
                previousAlbumCover();
                updateSongInfo();
                previousButtonSetVisibility(false);
            }
        });
    }

    private void initializeOnClickNext() {
        ImageView nextButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.next();
                _activity.setSongDurationText(DynamicQueue.getInstance(_activity).getCurrentSong().getDurationInSec());
                changePlayPauseButton();
                int currentIndex = nextAlbumCover();
                updateAlbumCovers(currentIndex);
                updateSongInfo();
                previousButtonSetVisibility(true);
                //ToDo "If 'next' pressed multiple times while dynamic queue loads? an offset is created.
            }
        });
    }

    private void updateSongInfo() {
        Song song = DynamicQueue.getInstance(_activity).getCurrentSong();
        ((TextView)_activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_title)).setText(song.getTitle());
        ((TextView)_activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_artist)).setText(song.getArtist());
        ((TextView)_activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_album)).setText(song.getAlbum());
        ((TextView)_activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_bpm)).setText(song.getBpm()+"");
    }

    private void previousButtonSetVisibility(boolean show) {
        CircleButton button = (CircleButton) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.previousButton);
        if(show){
            button.setAlpha(1f);
            button.setEnabled(true);
        }else if (DynamicQueue.getInstance(_activity).prevSongsIsEmpty()) {
            button.setAlpha(0.3f);
            button.setEnabled(false);
        }
    }

    private void initializeOnClickSettings() {
        ImageView settingsButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_activity, SettingsActivity.class);
                _activity.startActivity(intent);
            }

        });
    }

    public static void changePlayPauseButton(){
        new CountDownTimer(100, 1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                final ImageView playButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.playButton);
                final ImageView pauseButton = (ImageView) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.pauseButton);
                if (_activity.mMusicPlayerService.musicPlayer.isPlaying()) {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }
                else {
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }

    public void initializeDynamicQueue() {
        DynamicQueue.getInstance(_activity).selectNextSong();
        _activity.setSongDurationText(DynamicQueue.getInstance(_activity).getCurrentSong().getDurationInSec());
        updateSongInfo();
        previousButtonSetVisibility(false);
    }

    public void initializeOnClickSeekBar() {
        SeekBar seekBar = (SeekBar)_activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                _isPlaying = _activity.mMusicPlayerService.musicPlayer.isPlaying();
                _activity.mMusicPlayerService.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _activity.mMusicPlayerService.musicPlayer.seekTo(seekBar.getProgress() * 1000);

                if (_isPlaying) {
                    _activity.mMusicPlayerService.play();
                } else {
                    _activity.mMusicPlayerService.pause();
                }
            }
        });
    }

    public void initializeCoverFlow() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.coverflow);
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);
        coverFlow.setAdapter(coverImageAdapter);
        coverFlow.setSpacing(-10);
        coverFlow.setMaxZoom(-200);


        setCoverFlowImages();
    }



    private void setCoverFlowImages() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.coverflow);
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);

        ((ResourceImageAdapter) coverImageAdapter).setResources(getDynamicQueueAsList());

        coverFlow.setAdapter(coverImageAdapter);
    }


    private List<Bitmap> getDynamicQueueAsList() {
        DynamicQueue dynamicQueue = DynamicQueue.getInstance(_activity);
        List<Bitmap> allAlbumCovers = new ArrayList<>();

        for (Song song : dynamicQueue.getPrevSongs())
        {
            allAlbumCovers.add(getBitmapFromUri(song.getAlbumUri()));
        }

        allAlbumCovers.add(getBitmapFromUri(dynamicQueue.getCurrentSong().getAlbumUri()));

        for (Song song : dynamicQueue.getNextSongs())
        {
            allAlbumCovers.add(getBitmapFromUri(song.getAlbumUri()));
        }

        return allAlbumCovers;
    }

    private void updateAlbumCovers(int currentIndex) {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.coverflow);
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);
        DynamicQueue dynamicQueue = DynamicQueue.getInstance(_activity);

        //Updates coverflow to use new album covers.
        ((ResourceImageAdapter) coverImageAdapter).setResources(getDynamicQueueAsList());
        coverFlow.setAdapter(coverImageAdapter);

        //Sets the middle cover to current song.
        if (dynamicQueue.getPrevSongsSizeBeforeAdd() == dynamicQueue.getPrevSize()) {
            currentIndex--;
        }
        coverFlow.setSelection(currentIndex);
    }


    private int nextAlbumCover() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.coverflow);

        int nextPosition = coverFlow.getSelectedItemPosition() + 1;

        if (nextPosition < coverFlow.getCount()) {
            coverFlow.setSelection(nextPosition);
        }
        else {
            Toast.makeText(_activity, "No next song.", Toast.LENGTH_SHORT).show();
        }

        return coverFlow.getSelectedItemPosition();
    }

    private void previousAlbumCover() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.coverflow);

        int previousPosition = coverFlow.getSelectedItemPosition() - 1;

        if (coverFlow.getItemAtPosition(previousPosition) != null) {
            coverFlow.setSelection(previousPosition);
        }
        else {
            Toast.makeText(_activity, "No previous song.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromUri(Uri albumUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if (!new File(albumUri.getPath()).exists()){
            return BitmapFactory.decodeResource(_activity.getResources(), dk.aau.sw802f15.tempoplayer.R.drawable.defaultalbumcover);
        }
        BitmapFactory.Options optionsSecond = new BitmapFactory.Options();
        optionsSecond.inSampleSize = calculateInSampleSize(options, 350, 350);

        return BitmapFactory.decodeFile(albumUri.getPath(), optionsSecond);
    }

    Handler durationHandler = new Handler();

    public void startSeekBarPoll()
    {
        durationHandler.postDelayed(updateSeekBarTime, POLL_RATE);
    }

    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if(_activity.mMusicPlayerService == null || _activity.mMusicPlayerService.musicPlayer == null){
                return;
            }
            int timeElapsed = _activity.mMusicPlayerService.musicPlayer.getCurrentPosition() / 1000;
            SeekBar sb = (SeekBar) _activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.seekBar);
            sb.setProgress(timeElapsed);
            _activity.setSongProgressText(timeElapsed);
            durationHandler.postDelayed(this, POLL_RATE);
        }
    };

    private void resetSeekBar() {
        if (_activity.mMusicPlayerService.musicPlayer.isPlaying() ||
                _activity.mMusicPlayerService.isPaused) {
            _activity.mMusicPlayerService.musicPlayer.seekTo(0);
        }
    }

    public void stopSeekBarPoll(){
        durationHandler.removeCallbacks(updateSeekBarTime);
    }
    
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
}
