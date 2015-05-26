package dk.aau.sw802f15.tempoplayer.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.aau.sw802f15.tempoplayer.Libraries.DirectoryChooserDialog;
import dk.aau.sw802f15.tempoplayer.R;

public class SettingsFragment extends PreferenceFragment {

    private static String DEFAULT_DIRECTORY = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC;
    private List<Preference> pathPreferenceList = new ArrayList<>();
    private PreferenceScreen songScannerMenu;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        addPreferencesFromResource(R.xml.preference);
        songScannerMenu = (PreferenceScreen) findPreference("button_song_loader_category_key");

        initSongScanner();
    }

    private void initSongScanner() {
        initResetPaths();
        initAddPath();
        displaySavedPaths();

        PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
        preferenceCategory.setTitle("Music Paths");
        preferenceCategory.setOrder(2);
        pathPreferenceList.add(preferenceCategory);

        addAllPreferences();
    }

    private void displaySavedPaths() {
        for (String path : getSavedPaths(getActivity())) {
            displayPath(path);
        }
    }

    public static Set<String> getSavedPaths(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getStringSet("paths",
                new HashSet<String>() {{
                    add(DEFAULT_DIRECTORY);
                }});
    }

    private void savePath(String path) {
        Set<String> stringSet = new HashSet<>();
        stringSet.addAll(sharedPreferences.getStringSet("paths",
                new HashSet<String>() {{
                    add(DEFAULT_DIRECTORY);
                }}));
        stringSet.add(path);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("paths", stringSet);
        editor.apply();
    }

    private void deletePath(String path){
        Set<String> stringSet = new HashSet<>();
        stringSet.addAll(sharedPreferences.getStringSet("paths",
                new HashSet<String>() {{
                    add(DEFAULT_DIRECTORY);
                }}));
        stringSet.remove(Environment.getExternalStorageDirectory().toString() + path);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("paths", stringSet);
        editor.apply();
    }

    private void resetPath(){
        Set<String> stringSet = new HashSet<String>() {{ add(DEFAULT_DIRECTORY); }};

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("paths", stringSet);
        editor.apply();
    }

    private void initAddPath() {
        Preference AddButton = new Preference(getActivity());
        AddButton.setTitle("Add New Path");
        AddButton.setSummary("");
        AddButton.setDefaultValue("");
        AddButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(getActivity(),
                                new DirectoryChooserDialog.ChosenDirectoryListener()
                                {
                                    @Override
                                    public void onChosenDir(String chosenDir)
                                    {
                                        displayPath(chosenDir);
                                        savePath(chosenDir);
                                    }
                                });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(false);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory("");
                //m_newFolderEnabled = ! m_newFolderEnabled;

                return false;
            }
        });
 /*       AddButton.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!newValue.toString().equals("")) {
                    displayPath(newValue.toString());
                    savePath(newValue.toString());
                }
                return false;
            }
        });*/
        AddButton.setOrder(10000); //Should never reach this value
        pathPreferenceList.add(AddButton);
    }

    private void initResetPaths() {
        final Preference resetPaths = new Preference(getActivity());
        resetPaths.setTitle("Reset Paths");
        resetPaths.setOrder(1);
        resetPaths.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Reset Paths to Default")
                        .setMessage("All paths will be removed.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pathPreferenceList = new ArrayList<>();
                                resetPath();
                                initSongScanner();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return false;
            }
        });
        pathPreferenceList.add(resetPaths);
    }


    private void displayPath(String value) {
        Preference newPath = new Preference(getActivity());
        newPath.setTitle(value.replace(Environment.getExternalStorageDirectory().toString(), ""));
        newPath.setSummary("Click to delete.");
        newPath.setOrder(10);
        newPath.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Path")
                        .setMessage("Are you sure you want to delete this path?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pathPreferenceList.remove(preference);
                                deletePath(preference.getTitle().toString());
                                addAllPreferences();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return false;
            }
        });
        pathPreferenceList.add(newPath);
        addAllPreferences();
    }

    private void addAllPreferences() {
        songScannerMenu.removeAll();
        Collections.sort(pathPreferenceList, new Comparator<Preference>(){
            @Override
            public int compare(Preference lhs, Preference rhs) {
                return lhs.getTitle().toString().compareToIgnoreCase(rhs.getTitle().toString());
            }
        });
        int i = 10; //after the header
        for (Preference p : pathPreferenceList) {
            if (p.getOrder() >= 10 && p.getOrder() < 10000){
                p.setOrder(i);
                i++;
            }
            songScannerMenu.addItemFromInflater(p);
        }
    }
}
