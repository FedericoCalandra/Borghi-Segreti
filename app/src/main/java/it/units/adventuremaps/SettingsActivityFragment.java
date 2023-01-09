package it.units.adventuremaps;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivityFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
