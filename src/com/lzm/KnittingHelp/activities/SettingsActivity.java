package com.lzm.KnittingHelp.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import com.lzm.KnittingHelp.R;

import java.util.List;

/**
 * Created by DELL on 05/11/2014.
 */
//getActionBar().setTitle(getString(R.string.settings_title));
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_RELOAD_PATTERNS = "pref_reload_pattern";
    public static final String KEY_PREF_DATE_FORMAT = "pref_date_format";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(KEY_PREF_RELOAD_PATTERNS)) {
//            System.out.println("AQUIIIIIIIIIIIIII");
////            Preference connectionPref = findPreference(key);
////            // Set summary to be the user-description for the selected value
////            connectionPref.setSummary(sharedPreferences.getString(key, ""));
//        } else if (key.equals(KEY_PREF_DATE_FORMAT)) {
//            System.out.println("********************************** change date format");
//        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
