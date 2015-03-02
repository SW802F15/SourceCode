package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;
import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import com.example.sw802f15.tempoplayer.R;

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
        final ImageView playButton = (ImageView) _activity.findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mService.play();
                changePlayPauseButton();
                startSeekBarPoll();
            }
        });
    }



    private void initializeOnClickPause() {
        final ImageView pauseButton = (ImageView) _activity.findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mService.pause();
                changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickStop() {
        ImageView stopButton = (ImageView) _activity.findViewById(R.id.stopButton);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSeekBar();
                _activity.mService.stop();
                changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickPrevious() {
        ImageView previousButton = (ImageView) _activity.findViewById(R.id.previousButton);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mService.previous();
                _activity.setSongDurationText(DynamicQueue.getInstance().getCurrentSong().getDurationInSec());
                changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickNext() {
        ImageView nextButton = (ImageView) _activity.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mService.next();
                _activity.setSongDurationText(DynamicQueue.getInstance().getCurrentSong().getDurationInSec());
                changePlayPauseButton();
                nextAlbumCover();
            }
        });
    }

    private void initializeOnClickSettings() {
        ImageView settingsButton = (ImageView) _activity.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Start settings activity
            }

        });
    }

    private void changePlayPauseButton(){
        new CountDownTimer(50, 1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                final ImageView playButton = (ImageView) _activity.findViewById(R.id.playButton);
                final ImageView pauseButton = (ImageView) _activity.findViewById(R.id.pauseButton);
                if (_activity.mService.musicPlayer.isPlaying()) {
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
        DynamicQueue.getInstance().selectNextSong();
//        _activity.mService.loadSong(DynamicQueue.getInstance().getCurrentSong().getUri());
        _activity.setSongDurationText(DynamicQueue.getInstance().getCurrentSong().getDurationInSec());
    }

    public void initializeOnClickSeekBar() {
        SeekBar sb = (SeekBar)_activity.findViewById(R.id.seekBar);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                _isPlaying = _activity.mService.musicPlayer.isPlaying();
                _activity.mService.musicPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _activity.mService.musicPlayer.seekTo(seekBar.getProgress() * 1000);

                if(_isPlaying) {
                    _activity.mService.musicPlayer.start();
                }else{
                    _activity.mService.musicPlayer.pause();
                }
            }
        });
    }

    public void initializeCoverFlow() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(R.id.coverflow);
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);
        coverFlow.setAdapter(coverImageAdapter);
        coverFlow.setSpacing(-10);
        coverFlow.setMaxZoom(-200);


        setCoverFlowImages();
    }



    private void setCoverFlowImages() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(R.id.coverflow);
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);


        List<Bitmap> allAlbumCovers = new ArrayList<>();
        for (Song song : _activity.allSongsShouldBeDeleted)
        {
            allAlbumCovers.add(getBitmapFromUri(song.getAlbumUri()));
        }

        ((ResourceImageAdapter) coverImageAdapter).setResources(allAlbumCovers);

        coverFlow.setAdapter(coverImageAdapter);
    }

    private void nextAlbumCover() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(R.id.coverflow);
        BaseAdapter coverImageAdapter = (BaseAdapter) coverFlow.getAdapter();
        List<Bitmap> newBitmapList = new ArrayList<>();
        newBitmapList.add((Bitmap) coverImageAdapter.getItem(2));
        newBitmapList.add((Bitmap) coverImageAdapter.getItem(3));
        newBitmapList.add((Bitmap) coverImageAdapter.getItem(4));
        newBitmapList.add((Bitmap) coverImageAdapter.getItem(5));

        ((ResourceImageAdapter) coverImageAdapter).setResources(newBitmapList);


        coverFlow.setAdapter(coverImageAdapter);
        //coverFlow.getChildAt(0).scrollTo(300,0);

    }


    private Bitmap getBitmapFromUri(Uri albumUri) {
        try {
            return BitmapFactory.decodeFile(albumUri.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_activity, "Should return default album cover for unknown albums.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void startSeekBarPoll()
    {
        durationHandler.postDelayed(updateSeekBarTime, POLL_RATE);
    }

    Handler durationHandler = new Handler();

    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            int timeElapsed = _activity.mService.musicPlayer.getCurrentPosition() / 1000;
            SeekBar sb = (SeekBar)_activity.findViewById(R.id.seekBar);
            sb.setProgress(timeElapsed);
            _activity.setSongProgressText(timeElapsed);
            durationHandler.postDelayed(this, POLL_RATE);
        }
    };

    private void resetSeekBar() {
        _activity.mService.musicPlayer.seekTo(0);
    }
}
