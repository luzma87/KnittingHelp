package com.lzm.KnittingHelp.activities;

import android.app.Activity;
import android.os.Bundle;
import com.lzm.KnittingHelp.R;

/**
 * Created by DELL on 05/11/2014.
 */
public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        getActionBar().setTitle(getString(R.string.settings_title));
    }
}