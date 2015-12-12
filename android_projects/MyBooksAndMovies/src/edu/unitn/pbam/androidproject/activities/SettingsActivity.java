package edu.unitn.pbam.androidproject.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import edu.unitn.pbam.androidproject.R;
import edu.unitn.pbam.androidproject.utilities.App;
import edu.unitn.pbam.androidproject.utilities.Backup;
import edu.unitn.pbam.androidproject.utilities.Sync;

public class SettingsActivity extends SherlockPreferenceActivity implements
		OnSharedPreferenceChangeListener {
	public static final String KEY_PREF_USERNAME = "prefUsername";
	public static final String KEY_PREF_WIFI = "wifi_only";
	public static final String KEY_PREF_REPORTS = "anon_reports";
	public static final String KEY_PREF_ROTATE = "prefRotateScreen";
	public static final String KEY_PREF_THEME = "prefThemeColor";

	public static final String KEY_PREF_BACKUP = "backup";
	public static final String KEY_PREF_IMPORTBACKUP = "import_backup";
	public static final String KEY_PREF_SYNCALL = "sync_all";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		// Action bar customization
		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
		actionBar.setIcon(R.drawable.settings);
		actionBar.setTitle(App.getAppContext().getString(R.string.settings));
		actionBar.setHomeButtonEnabled(true);

		Preference backup = (Preference) findPreference(KEY_PREF_BACKUP);
		Preference importBackup = (Preference) findPreference(KEY_PREF_IMPORTBACKUP);
		backup.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Backup.backupDatabase(SettingsActivity.this);
				return true;
			}
		});
		importBackup
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Backup.importBackup(SettingsActivity.this);
						return true;
					}
				});
		Preference sync = (Preference) findPreference(KEY_PREF_SYNCALL);
		sync.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Sync.syncAll();
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference connectionPref;
		if (key.equals(KEY_PREF_REPORTS)) {
			connectionPref = findPreference(key);
			connectionPref.setDefaultValue(sharedPreferences.getBoolean(key,
					false));
		}
		if (key.equals(KEY_PREF_ROTATE)) {
			connectionPref = findPreference(key);
			connectionPref.setSummary(sharedPreferences.getString(key, ""));
		}
		if (key.equals(KEY_PREF_THEME)) {
			connectionPref = findPreference(key);
			connectionPref.setSummary(sharedPreferences.getString(key, ""));
		}
		if (key.equals(KEY_PREF_USERNAME)) {
			connectionPref = findPreference(key);
			connectionPref.setSummary(sharedPreferences.getString(key, ""));
		}
		if (key.equals(KEY_PREF_WIFI)) {
			connectionPref = findPreference(key);
			connectionPref.setDefaultValue(sharedPreferences.getBoolean(key,
					false));
		}
	}
}
