package dk.aau.sw802f15.tempoplayer;


import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScanner;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;

public class TestHelper {

    public static Song getValidSong() {
        String initMusicPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/tempo/";
        String initCoverPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_PICTURES + "/tempo/";

        Song test_1 = new Song("Tristram", "Matt Uemen", //Title, Artist
                "Diablo SoundTrack", 130,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_1.mp3"),
                Uri.parse(initCoverPath + "cover_sample_1.jpg"), //SongUri, CoverUri
                7*60 + 40);

        return test_1;
    }

    public static Song getInvalidSong() {
        return new Song(null, null, null, null, null, null, 0);
    }

    public static void initializeTestSongs(SongDatabase db) {
        db.clearDatabase();

        String initMusicPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/tempo/";
        String initCoverPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_PICTURES + "/tempo/";

        Song test_1 = getValidSong();

        Song test_2 = new Song("Let It Go", "Idina Menzel", //Title, Artist
                "Frozen SoundTrack", 137,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_2.mp3"),
                Uri.parse(initCoverPath + "cover_sample_2.jpg"), //SongUri, CoverUri
                3*60 + 40);

        Song test_3 = new Song("Runnin'", "Adam Lambert", //Title, Artist
                "Trespassing", 81,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_3.mp3"),
                Uri.parse(initCoverPath + "cover_sample_3.jpg"), //SongUri, CoverUri
                3*60 + 48);

        Song test_4 = new Song("Sex on Fire", "Kings of Leon", //Title, Artist
                "Only by the Night", 81,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_4.mp3"),
                Uri.parse(initCoverPath + "cover_sample_4.jpg"), //SongUri, CoverUri
                3*60 + 26);

        Song test_5 = new Song("T.N.T.", "AC/DC", //Title, Artist
                "T.N.T.", 126,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_5.mp3"),
                Uri.parse(initCoverPath + "cover_sample_5.jpg"), //SongUri, CoverUri
                3*60 + 34);

        Song test_6 = new Song("Still Counting", "Volbeat", //Title, Artist
                "Guitar Gangstars & Cadillac Blood", null,  //Album , BPM
                Uri.parse(initMusicPath + "music_sample_6.mp3"),
                Uri.parse(initCoverPath + "cover_sample_6.jpg"), //SongUri, CoverUri
                4*60 + 21);

        db.insertSong(test_1);
        db.insertSong(test_2);
        db.insertSong(test_3);
        db.insertSong(test_4);
        db.insertSong(test_5);
        db.insertSong(test_6);
    }


    public static Method testPrivateMethod(Classes className, String methodName, Context context) {

        Method[] privateMethods = new Method[0];

        switch (className) {
            case Song:
                throw new UnsupportedOperationException();
                //privateMethods =  Song.class.getDeclaredMethods();
                //break;
            case SongDatabase:
                privateMethods = new SongDatabase(context).getClass().getDeclaredMethods();
                break;
            case SongScanner:
                privateMethods = SongScanner.getInstance(context).getClass().getDeclaredMethods();
                break;
            case DynamicQueue:
                throw new UnsupportedOperationException();
                //privateMethods =  DynamicQueue.class.getDeclaredMethods();
                //break;
            case Initializers:
                throw new UnsupportedOperationException();
                //privateMethods =  Initializers.class.getDeclaredMethods();
                //break;
            case MusicPlayerActivity:
                //throw new UnsupportedOperationException();
                //privateMethods =  ((MusicPlayerActivity) context).getClass().getDeclaredMethods();
                privateMethods = MusicPlayerActivity.class.getDeclaredMethods();
                break;
            case MusicPlayerService:
                throw new UnsupportedOperationException();
                //privateMethods =  MusicPlayerService.class.getDeclaredMethods();
                //break;
            case CircleButton:
                throw new UnsupportedOperationException();
                //privateMethods =  CircleButton.class.getDeclaredMethods();
                //break;
            case AbstractCoverFlowImageAdapter:
                throw new UnsupportedOperationException();
                //privateMethods =  AbstractCoverFlowImageAdapter.class.getDeclaredMethods();
                //break;
            case CoverFlow:
                throw new UnsupportedOperationException();
                //privateMethods =  CoverFlow.class.getDeclaredMethods();
                //break;
            case ResourceImageAdapter:
                throw new UnsupportedOperationException();
                //privateMethods =  ResourceImageAdapter.class.getDeclaredMethods();
                //break;
            case SettingsActivity:
                throw new UnsupportedOperationException();
                //privateMethods =  SettingsActivity.class.getDeclaredMethods();
                //break;

            default:
                throw new NoSuchElementException();
                //break;
        }

        for (Method privateMethod : privateMethods) {
            String privateMethodName = privateMethod.getName();
            if (privateMethodName.equals(methodName)) {
                privateMethod.setAccessible(true);
                return privateMethod;
            }
        }

        return null;
    }

    public enum Classes {
        Song,
        SongDatabase,
        SongScanner,
        DynamicQueue,
        Initializers,
        MusicPlayerActivity,
        MusicPlayerService,
        CircleButton,
        AbstractCoverFlowImageAdapter,
        CoverFlow,
        ResourceImageAdapter,
        SettingsActivity
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
//                Examples for testing private methods                                            //
////////////////////////////////////////////////////////////////////////////////////////////////////
/*
    public void testPrivateMethodWithoutParametersExample() {
        Boolean expectedValue = true;
        Boolean actualValue = false;

        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.ClassName,
                                                            "MethodName",
                                                            getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        try {
            actualValue = (Boolean) privateMethod.invoke(songScannerClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        assertEquals(expectedValue, actualValue);
    }

    public void testPrivateMethodWithParametersExample() {
        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        String parameter = path;
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.ClassName,
                                                            "MethodName",
                                                            getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        try {
            privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }
*/