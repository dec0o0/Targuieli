package com.example.lista.cumparaturi.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.lista.cumparaturi.R;

/**
 * Created by macbookproritena on 11/30/16.
 */

public class SettingsActivity extends Activity{

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }
    }
}