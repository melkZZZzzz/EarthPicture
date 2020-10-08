package org.melk.earthpicture;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import org.melk.earthpicture.helpers.SharedPreferencesHelpers;
import org.melk.earthpicture.storage.StorageProvider;
import org.melk.earthpicture.work.EarthPictureWorkManager;
import org.melk.earthpicture.worker.LiveWallpaperContinuationWorker;
import org.melk.earthpicture.worker.WallpaperContinuationWorker;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_activity);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.settings, new SettingsFragment())
				.commit();

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	public static class SettingsFragment extends PreferenceFragmentCompat {

		protected EarthPictureWorkManager earthPictureWorkManager;

		private Class GetCurrentWorker() {
			int mode = Integer.parseInt( ((ListPreference)findPreference(getString(R.string.wallpaper_sync_mode_key))).getValue() );
			return GetWorker(mode);
		}
		private Class GetWorker(int mode) {
			switch( mode ) {
				case SharedPreferencesHelpers.SyncMode.WALLPAPER:		return WallpaperContinuationWorker.class;
				case SharedPreferencesHelpers.SyncMode.LIVE_WALLPAPER:	return LiveWallpaperContinuationWorker.class;
				case SharedPreferencesHelpers.SyncMode.OFF:
				default:												return null;
			}
		}

		private void CancelWork() {
			earthPictureWorkManager.cancelUnique(getString(R.string.earth_picture_unique_work_key));
		}
		private void ScheduleWork(Class EarthPictureWorkerClass, int period, boolean doReplaceIfNeeded) {
			if (true == doReplaceIfNeeded) {
				CancelWork();
			}
			if (0 != period) {
				earthPictureWorkManager.QueueUnique(
						getString(R.string.earth_picture_unique_work_key),
						EarthPictureWorkManager.WorkRequestPeriodicBuilder(EarthPictureWorkerClass, period, TimeUnit.MINUTES)
								.setConstraints(EarthPictureWorkManager.GetRecommendedConstraints())
								.build()
				);
			}
		}

		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.root_preferences, rootKey);

			earthPictureWorkManager = new EarthPictureWorkManager(getContext());

			findPreference(getString(R.string.satellite_pref_key))
					.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(Preference preference, Object newValue) {
							StorageProvider sp = new StorageProvider(getContext());
							sp.SrcDelete();
							sp.BackgroundDelete();
							((ListPreference)findPreference(getString(R.string.background_pref_key))).setValue(null);
							return true;
						}
					});

			findPreference(getString(R.string.wallpaper_sync_key))
					.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(Preference preference, Object newValue) {
							boolean bSync = (boolean) newValue;
							if (bSync) {
								String imgName = SharedPreferencesHelpers.GetSelectedImageName(getContext());
								if (null != imgName) {
									int period = Integer.parseInt( ((ListPreference)findPreference(getString(R.string.wallpaper_sync_period_key))).getValue() );
									((ListPreference)findPreference(getString(R.string.background_pref_key))).setValue(null);
									ScheduleWork(GetCurrentWorker(), period, true);
									Toast.makeText(getContext(), "wallpaper auto sync ON", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(getContext(), getString(R.string.satellite_pref_title) + " should be configured", Toast.LENGTH_SHORT).show();
									return false;
								}
							} else {
								CancelWork();
								Toast.makeText(getContext(), "wallpaper auto sync OFF", Toast.LENGTH_SHORT).show();
							}
							return true;
						}
					});

			findPreference(getString(R.string.wallpaper_sync_mode_key))
					.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(Preference preference, Object newValue) {
							int syncMode = Integer.parseInt((String) newValue);
							boolean isSyncActive = ( null != GetWorker(syncMode) );

							((SwitchPreferenceCompat)findPreference(getString(R.string.wallpaper_sync_key))).setChecked(isSyncActive);
							return true;
						}
					});

			findPreference(getString(R.string.wallpaper_sync_period_key))
					.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(Preference preference, Object newValue) {
							int period = Integer.parseInt((String) newValue);
							ScheduleWork(GetCurrentWorker(), period, true);
							return true;
						}
					});

			findPreference(getString(R.string.background_crop_key))
					.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(Preference preference, Object newValue) {
							StorageProvider sp = new StorageProvider(getContext());
							sp.BackgroundDelete();
							return true;
						}
					});
		}
	}
}