package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CircleButton.CircleButton;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.SeekBarManager;
import dk.aau.sw802f15.tempoplayer.R;
import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Draegert on 26-02-2015.
 */
public class Initializers {

    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private final static long TIME_BETWEEN_BUTTON_CLICKS = 100;

    private static MusicPlayerActivity _activity;
    private static GUIManager _guiManager;

    private long timeForLastPrevClick = 0;
    private long timeForLastNextClick = 0;
    private boolean _isPlaying = false;

    private enum controlAction {
        preivous,
        next
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public Initializers(MusicPlayerActivity activity) {
        _activity = activity;
        _guiManager = new GUIManager(_activity);
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private void initializeOnClickPlay() {
        final ImageView playButton = (ImageView) _activity.findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.play();
                _guiManager .changePlayPauseButton();
                _guiManager.startSeekBarPoll();
            }
        });
    }

    private void initializeOnClickPause() {
        final ImageView pauseButton = (ImageView) _activity.findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.pause();
                _guiManager.changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickStop() {
        ImageView stopButton = (ImageView) _activity.findViewById(R.id.stopButton);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _guiManager.resetSeekBar();
                _activity.mMusicPlayerService.stop();
                _guiManager.changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickPrevious() {
        final ImageView previousButton = (ImageView) _activity.findViewById(R.id.previousButton);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSong(controlAction.preivous, false);
            }
        });
    }

    private void initializeOnClickNext() {
        ImageView nextButton = (ImageView) _activity.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSong(controlAction.next, true);
            }
        });
    }

    private void changeSong(controlAction action, boolean previousVisibility) {
        long timeSinceLastClick = (action == controlAction.next) ? timeForLastNextClick
                                                                 : timeForLastPrevClick;

        if (System.currentTimeMillis() - timeSinceLastClick > TIME_BETWEEN_BUTTON_CLICKS) {
            boolean wasPlaying = _activity.mMusicPlayerService.musicPlayer.isPlaying();

            if (action == controlAction.next) {
                _activity.mMusicPlayerService.next();
                _guiManager.nextAlbumCover();
            }
            if (action == controlAction.preivous) {
                _activity.mMusicPlayerService.previous();
                _guiManager.previousAlbumCover();
            }

            _guiManager.updateSongInfo();
            _guiManager.previousButtonSetVisibility(previousVisibility);

            if (wasPlaying) {
                _activity.mMusicPlayerService.play();
            }
        }

        if (action == controlAction.next) {
            timeForLastNextClick = System.currentTimeMillis();
        }
        if (action == controlAction.preivous) {
            timeForLastPrevClick = System.currentTimeMillis();
        }
    }

    private void initializeOnClickSettings() {
        ImageView settingsButton = _guiManager.findSettingsButton();
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_activity, SettingsActivity.class);
                _activity.startActivity(intent);
            }

        });
    }

    private void initializeOnClickSeekBar() {
        SeekBar seekBar = _guiManager.findSeekBar();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

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
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public void initializeOnClickListeners() {
        initializeOnClickPlay();
        initializeOnClickPause();
        initializeOnClickStop();
        initializeOnClickPrevious();
        initializeOnClickNext();
        initializeOnClickSettings();
        initializeOnClickSeekBar();
    }

    public void initializeDynamicQueue() {
        DynamicQueue.getInstance(_activity).selectNextSong();
        _guiManager.updateSongInfo();
        _guiManager.previousButtonSetVisibility(false);
    }

    public void initializeCoverFlow() {
        final CoverFlow coverFlow = (CoverFlow) _activity.findViewById(R.id.coverflow);
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);
        coverFlow.setAdapter(coverImageAdapter);
        coverFlow.setSpacing(-10);
        coverFlow.setMaxZoom(-200);

        _guiManager.setCoverFlowImages();
    }

    //endregion
}
