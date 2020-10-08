package org.melk.earthpicture.worker;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.melk.earthpicture.services.LiveWallpaperService;
import org.melk.earthpicture.work.EarthPictureWorker;


public class SetLiveWallpaperWorker extends EarthPictureWorker {

	private Context context;

	public SetLiveWallpaperWorker(@NonNull Context context, @NonNull WorkerParameters params) {
		super(context, params);
		this.context = context;
	}

	@Override
	public boolean doJob() {
		LiveWallpaperService.sendBroadcast(context, LiveWallpaperService.NOTIFY_CHANGE);

		return true;
	}

}