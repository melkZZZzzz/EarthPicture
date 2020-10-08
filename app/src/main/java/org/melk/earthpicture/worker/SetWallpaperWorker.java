package org.melk.earthpicture.worker;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.melk.earthpicture.helpers.PictureHelpers;
import org.melk.earthpicture.storage.StorageProvider;
import org.melk.earthpicture.work.EarthPictureWorker;


public class SetWallpaperWorker extends EarthPictureWorker {

	private Context context;

	public SetWallpaperWorker(@NonNull Context context, @NonNull WorkerParameters params) {
		super(context, params);
		this.context = context;
	}

	@Override
	public boolean doJob() {
		StorageProvider sp = new StorageProvider(context);
		Bitmap bmp = sp.BackgroundBitmap();

		return PictureHelpers.SetWallpaper(context, bmp);
	}

}