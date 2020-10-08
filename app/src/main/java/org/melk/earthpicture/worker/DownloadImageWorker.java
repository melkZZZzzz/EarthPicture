package org.melk.earthpicture.worker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.melk.earthpicture.helpers.PictureHelpers;
import org.melk.earthpicture.storage.StorageProvider;
import org.melk.earthpicture.work.EarthPictureWorker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class DownloadImageWorker extends EarthPictureWorker {

	private Context context;
	private String path;

	public DownloadImageWorker(@NonNull Context context, @NonNull WorkerParameters params ) {
		super(context, params);
		this.context = context;
		this.path = getInputData().getString("url");
	}

	@Override
	public boolean doJob() {
		boolean bOK = false;

		PictureHelpers.Result<Bitmap> fetchResult = new PictureHelpers.Result<>();

		StorageProvider sp = new StorageProvider( context );
		Date lastDate = sp.BackgroundDate();

		try {
			URL url = new URL(path);
			fetchResult = PictureHelpers.FetchBitmapIf(url, lastDate);
		} catch (MalformedURLException e) {
			Log.e("DownloadImageWorker", "Error in image url: " + e.getMessage());
		}

		switch (fetchResult.status) {
			case Success: {
				/* save to file */
				bOK = sp.SrcSave(fetchResult.output, fetchResult.date);

				break;
			}
			case UpToDate: {
				Log.i("DownloadImageWorker", "Up to date");
				bOK = true;

				break;
			}
		}

		return bOK;
	}

}