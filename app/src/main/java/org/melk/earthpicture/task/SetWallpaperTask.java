package org.melk.earthpicture.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import org.melk.earthpicture.helpers.PictureHelpers;
import org.melk.earthpicture.helpers.SharedPreferencesHelpers;


public class SetWallpaperTask extends AsyncTask<Bitmap, Void, Bitmap> {

	protected boolean bStatus;
	protected Context context;

	public SetWallpaperTask(Context context) {
		this.context = context;
		this.bStatus = false;
	}

	@Override
	protected Bitmap doInBackground(Bitmap... bmps) {
		Bitmap bmp = bmps[0];
		bStatus = PictureHelpers.SetWallpaper(context, bmp);
		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (bStatus) {
			String imgName = SharedPreferencesHelpers.GetSelectedImageName(context);
			SharedPreferencesHelpers.SetWallpaperImageName(context, imgName);
			Toast.makeText(context, "Wallpaper set", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Error setting Wallpaper", Toast.LENGTH_SHORT).show();
		}
	}

}