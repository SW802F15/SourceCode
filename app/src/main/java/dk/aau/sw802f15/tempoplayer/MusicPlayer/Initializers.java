package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;

import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;
import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;

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
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private void initializeOnClickPlay() {
        final ImageView playButton = GUIManager.getInstance(_activity).findPlayButton();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.play();
                GUIManager.getInstance(_activity).changePlayPauseButton();
                GUIManager.getInstance(_activity).startSeekBarPoll();
            }
        });
    }

    private void initializeOnClickPause() {
        final ImageView pauseButton = GUIManager.getInstance(_activity).findPauseButton();

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.mMusicPlayerService.pause();
                GUIManager.getInstance(_activity).changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickStop() {
        ImageView stopButton = GUIManager.getInstance(_activity).findStopButton();

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GUIManager.getInstance(_activity).resetSeekBar();
                _activity.mMusicPlayerService.stop();
                GUIManager.getInstance(_activity).changePlayPauseButton();
            }
        });
    }

    private void initializeOnClickPrevious() {
        final ImageView previousButton = GUIManager.getInstance(_activity).findPreviousButton();

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSongGUIActions(controlAction.preivous, false);
            }
        });
    }

    private void initializeOnClickNext() {
        ImageView nextButton = GUIManager.getInstance(_activity).findNextButton();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSongGUIActions(controlAction.next, true);
            }
        });
    }

    private void changeSongGUIActions(controlAction action, boolean previousVisibility) {
        long timeSinceLastClick = (action == controlAction.next) ? timeForLastNextClick
                                                                 : timeForLastPrevClick;

        if (System.currentTimeMillis() - timeSinceLastClick > TIME_BETWEEN_BUTTON_CLICKS) {
            boolean wasPlaying = _activity.mMusicPlayerService.isPlaying();

            if (action == controlAction.next) {
                _activity.mMusicPlayerService.next();
                GUIManager.getInstance(_activity).nextAlbumCover();
            }
            if (action == controlAction.preivous) {
                _activity.mMusicPlayerService.previous();
                GUIManager.getInstance(_activity).previousAlbumCover();
            }

            GUIManager.getInstance(_activity).updateSongInfo();
            GUIManager.getInstance(_activity).previousButtonSetVisibility(previousVisibility);

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
        ImageView settingsButton = GUIManager.getInstance(_activity).findSettingsButton();
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_activity, SettingsActivity.class);
                _activity.startActivity(intent);
            }

        });
    }

    private void initializeOnClickSeekBar() {
        SeekBar seekBar = GUIManager.getInstance(_activity).findSeekBar();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                _isPlaying = _activity.mMusicPlayerService.isPlaying();
                _activity.mMusicPlayerService.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                _activity.mMusicPlayerService.seekTo(seekBar.getProgress() * 1000);

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
        GUIManager.getInstance(_activity).updateSongInfo();
        GUIManager.getInstance(_activity).previousButtonSetVisibility(false);
    }

    public void initializeCoverFlow() {
        CoverFlow coverFlow = GUIManager.getInstance(_activity).findCoverFlow();
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);
        coverFlow.setAdapter(coverImageAdapter);
        coverFlow.setSpacing(-10);
        coverFlow.setMaxZoom(-200);

        GUIManager.getInstance(_activity).setCoverFlowImages();
    }
    //endregion
}