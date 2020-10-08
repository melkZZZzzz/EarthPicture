package org.melk.earthpicture.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.WorkerParameters;

import org.melk.earthpicture.R;
import org.melk.earthpicture.helpers.PictureHelpers;
import org.melk.earthpicture.helpers.SharedPreferencesHelpers;
import org.melk.earthpicture.storage.StorageProvider;
import org.melk.earthpicture.work.EarthPictureWorker;

import java.util.Date;

public class CropImageWorker extends EarthPictureWorker {

	private Context context;
	private String imageName;

	public CropImageWorker(@NonNull Context context, @NonNull WorkerParameters params ) {
		super(context, params);
		this.context = context;
		this.imageName = getInputData().getString("imageName");
	}

	@Override
	public boolean doJob() {
		boolean bOK = false;

		StorageProvider sp = new StorageProvider( context );
		Date srcDate = sp.SrcDate();
		Date bgDate = sp.BackgroundDate();

		String bgName = SharedPreferencesHelpers.GetWallpaperImageName(context);

		boolean obsolete = true;

		if ( null != imageName && null != bgName && imageName == bgName ) {
			obsolete = false;
		}

		if ( null != srcDate && ( null == bgDate || srcDate.after(bgDate) || obsolete ) ) {
			Bitmap bmp = sp.SrcBitmap();
			Bitmap background = null;

			if ( SharedPreferencesHelpers.IsWallpaperCropped(context) ) {
				Rect cropHint = SharedPreferencesHelpers.GetWallpaperCrop(context);
				background = PictureHelpers.CropBitmap(bmp, cropHint);
			} else {
				background = PictureHelpers.AdaptToDisplay(context, bmp);
			}

			bOK = sp.BackgroundSave(background, bgDate);

			if ( bOK ) {
				SharedPreferencesHelpers.SetWallpaperImageName(context, imageName);
			}
		}

		return bOK;
	}

}