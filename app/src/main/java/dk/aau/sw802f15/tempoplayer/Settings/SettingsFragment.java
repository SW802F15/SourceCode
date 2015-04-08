package dk.aau.sw802f15.tempoplayer.Settings;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.List;

import dk.aau.sw802f15.tempoplayer.R;

public class SettingsFragment extends PreferenceFragment {


    private List<Preference> pathPreferenceList = new ArrayList<>();
    private PreferenceScreen songScannerMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);

        songScannerMenu = (PreferenceScreen) findPreference("button_song_loader_category_key");

        initSongScanner();
    }

    private void initSongScanner() {
        initAddPath();
        initResetPaths();

        PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
        preferenceCategory.setTitle("Music Paths");
        pathPreferenceList.add(preferenceCategory);
        //songScannerMenu.addItemFromInflater(preferenceCategory);

    }

    private void initAddPath() {
        EditTextPreference addPath = new EditTextPreference(getActivity());
        addPath.setTitle("Add New Path");
        addPath.setDialogTitle("Add New Path");
        addPath.setSummary("");
        addPath.setDefaultValue("");
        addPath.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!newValue.toString().equals("")) {
                    addNewPath(newValue.toString());
                }
                return false;
            }
        });
        pathPreferenceList.add(addPath);
        songScannerMenu.addItemFromInflater(addPath);
    }

    private void initResetPaths() {
        Preference resetPaths = new Preference(getActivity());
        resetPaths.setTitle("Reset Paths");
        resetPaths.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                pathPreferenceList = pathPreferenceList.subList(0, 3);
                addAllPreferences();
                return false;
            }
        });
        pathPreferenceList.add(resetPaths);
        songScannerMenu.addItemFromInflater(resetPaths);

    }



    private void addNewPath(String pref) {
        EditTextPreference newPath = new EditTextPreference(getActivity());
        newPath.setTitle("Edit Path");
        newPath.setDialogTitle("Edit Path");
        newPath.setSummary(pref);
        newPath.setDefaultValue(pref);
        newPath.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("")){
                    pathPreferenceList.remove(preference);
                    addAllPreferences();
                } else {
                    preference.setSummary(newValue.toString());
                }
                return false;
            }
        });
        pathPreferenceList.add(newPath);
        addAllPreferences();
    }

    private void addAllPreferences() {
        songScannerMenu.removeAll();
        for (Preference p : pathPreferenceList){
            songScannerMenu.addItemFromInflater(p);
        }

    }
}
