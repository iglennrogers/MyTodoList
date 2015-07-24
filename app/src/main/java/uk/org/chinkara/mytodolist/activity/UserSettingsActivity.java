package uk.org.chinkara.mytodolist.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import uk.org.chinkara.mytodolist.fragments.SettingsFragment;
import uk.org.chinkara.mytodolist.model.TodoPreferences;

/**
 * Created by g_rogers on 15/07/2015.
 */
public class UserSettingsActivity extends PreferenceActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected boolean isValidFragment(String fragmentName) {

        return super.isValidFragment(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(TodoPreferences.SORT_CONFIG_KEY)) {

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
    }
}
