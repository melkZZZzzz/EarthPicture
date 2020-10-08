package org.melk.earthpicture.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;

import androidx.preference.PreferenceManager;

import org.melk.earthpicture.R;

public final class SharedPreferencesHelpers {

	public static final class SyncMode {
		public static final int OFF = 0;
		public static final int WALLPAPER = 1;
		public static final int LIVE_WALLPAPER = 2;
	}

	public static final class DefaultParameters {
		public static final String SelectedImageName = "EUMETSAT_MSG_RGBNatColourEnhncd_FullResolution.jpg";
		public static final boolean SynchronizationActive = false;
		public static final int SynchronizationMode = 0;
		public static final int SynchronizationPeriod = 60;
	}

	public static void InitializeIfNot(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isInit = sharedPreferences.getBoolean(context.getString(R.string.is_app_initialized_key), false);

		if (false == isInit) {
			SharedPreferencesHelpers.SetSelectedImageName(context, SharedPreferencesHelpers.DefaultParameters.SelectedImageName);
			SharedPreferencesHelpers.IsSynchronizationActive(context, SharedPreferencesHelpers.DefaultParameters.SynchronizationActive);
			SharedPreferencesHelpers.SynchronizationMode(context, SharedPreferencesHelpers.DefaultParameters.SynchronizationMode);
			SharedPreferencesHelpers.SynchronizationPeriod(context, SharedPreferencesHelpers.DefaultParameters.SynchronizationPeriod);
			sharedPreferences
					.edit()
					.putBoolean(context.getString(R.string.is_app_initialized_key), true)
					.commit();
		}
	}

	public static String GetWallpaperCandidateImageName(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String imageName = sharedPreferences.getString(context.getString(R.string.background_pref_key), null);
		if ( null == imageName ) {
			imageName = sharedPreferences.getString(context.getString(R.string.satellite_pref_key), null);
		}
		return imageName;
	}

	public static String GetSelectedImageName(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(context.getString(R.string.satellite_pref_key), null);
	}
	public static void SetSelectedImageName(Context context, String imageName) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if ( null == imageName ) {
			sharedPreferences
					.edit()
					.remove(context.getString(R.string.satellite_pref_key))
					.commit();
		} else {
			sharedPreferences
					.edit()
					.putString(context.getString(R.string.satellite_pref_key), imageName)
					.commit();
		}
	}

	public static String GetWallpaperImageName(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(context.getString(R.string.background_pref_key), null);
	}
	public static void SetWallpaperImageName(Context context, String imageName) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if ( null == imageName ) {
			sharedPreferences
					.edit()
					.remove(context.getString(R.string.background_pref_key))
					.commit();
		} else {
			sharedPreferences
					.edit()
					.putString(context.getString(R.string.background_pref_key), imageName)
					.commit();
		}
	}

	public static boolean IsSynchronizationActive(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(context.getString(R.string.wallpaper_sync_key), false);
	}
	public static void IsSynchronizationActive(Context context, boolean isActive) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences
				.edit()
				.putBoolean(context.getString(R.string.wallpaper_sync_key), isActive)
				.commit();
	}

	public static int SynchronizationMode(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt( sharedPreferences.getString(context.getString(R.string.wallpaper_sync_mode_key), "0") );
	}
	public static void SynchronizationMode(Context context, int mode) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences
				.edit()
				.putString(context.getString(R.string.wallpaper_sync_mode_key), String.valueOf(mode))
				.commit();
	}

	public static int SynchronizationPeriod(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt( sharedPreferences.getString(context.getString(R.string.wallpaper_sync_period_key), "0") );
	}
	public static void SynchronizationPeriod(Context context, int period) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences
				.edit()
				.putString(context.getString(R.string.wallpaper_sync_period_key), String.valueOf(period))
				.commit();
	}

	public static boolean IsWallpaperCropped(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(context.getString(R.string.background_crop_key),false);
	}
	public static void IsWallpaperCropped(Context context, boolean doCrop) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences
			.edit()
			.putBoolean(context.getString(R.string.background_crop_key), doCrop)
			.commit();
	}

	public static Rect GetWallpaperCrop(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Rect cropHint = new Rect();
		cropHint.left = sharedPreferences.getInt(context.getString(R.string.background_crop_left_key),0);
		cropHint.top = sharedPreferences.getInt(context.getString(R.string.background_crop_top_key),0);
		cropHint.right = sharedPreferences.getInt(context.getString(R.string.background_crop_right_key),-1);
		cropHint.bottom = sharedPreferences.getInt(context.getString(R.string.background_crop_bottom_key),-1);
		return cropHint;
	}
	public static void SetWallpaperCrop(Context context, Rect cropHint) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if ( null == cropHint ) {
			sharedPreferences
					.edit()
					.remove(context.getString(R.string.background_crop_left_key))
					.remove(context.getString(R.string.background_crop_top_key))
					.remove(context.getString(R.string.background_crop_right_key))
					.remove(context.getString(R.string.background_crop_bottom_key))
					.commit();
		} else {
			sharedPreferences
					.edit()
					.putInt(context.getString(R.string.background_crop_left_key),cropHint.left)
					.putInt(context.getString(R.string.background_crop_top_key),cropHint.top)
					.putInt(context.getString(R.string.background_crop_right_key),cropHint.right)
					.putInt(context.getString(R.string.background_crop_bottom_key),cropHint.bottom)
					.commit();
		}
	}

}
