package uk.org.chinkara.mytodolist.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import uk.org.chinkara.mytodolist.R;


public class SettingsFragment extends PreferenceFragment {

    private static String TAG = "<<Todo config>>: ";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "begin onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        Log.d(TAG, "end onCreate");
    }

    @Override
    public void onAttach(Activity activity) {

        Log.d(TAG, "begin onAttach");
        super.onAttach(activity);
        Log.d(TAG, "after onAttach");
        try {

            _listener = (SharedPreferences.OnSharedPreferenceChangeListener) activity;
        }
        catch (ClassCastException e) {

            throw new ClassCastException(activity.toString() + " must implement OnSharedPreferenceChangeListener");
        }
        Log.d(TAG, "end onAttach");
    }

    @Override
    public void onDetach() {

        Log.d(TAG, "begin onDetach");
        super.onDetach();
        _listener = null;
        Log.d(TAG, "end onDetach");
    }

    @Override
    public void onResume() {

        Log.d(TAG, "begin OnResume");
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(_listener);
        Log.d(TAG, "end OnResume");
    }

    @Override
    public void onPause() {

        Log.d(TAG, "begin OnPause");
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(_listener);
        Log.d(TAG, "end OnPause");
    }

    SharedPreferences.OnSharedPreferenceChangeListener _listener;
}
