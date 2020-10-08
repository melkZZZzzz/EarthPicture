package org.melk.earthpicture.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.melk.earthpicture.helpers.PictureHelpers;
import org.melk.earthpicture.helpers.ViewHelpers;
import org.melk.earthpicture.storage.StorageProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

	protected boolean upToDate;
	protected ImageView bmImage;
	protected ViewHelpers.ProgressView progressView;

	public DownloadImageTask(ImageView bmImage) {
		this.upToDate = false;
		this.bmImage = bmImage;
		this.progressView = ViewHelpers.getProgressView(bmImage.getContext());
	}

	@Override
	public void onPreExecute() {
		WindowManager windowManager = (WindowManager) bmImage.getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(progressView.view, progressView.windowParams);
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		Bitmap bitmap = null;
		String path = urls[0];

		PictureHelpers.Result<Bitmap> fetchResult = new PictureHelpers.Result<>();

		StorageProvider sp = new StorageProvider( bmImage.getContext() );
		Date srcDate = sp.SrcDate();

		try {
			URL url = new URL(path);
			fetchResult = PictureHelpers.FetchBitmapIf(url, srcDate);
		} catch (MalformedURLException e) {
			Log.e("DownloadImageTask", "Error in image url: " + e.getMessage());
		}

		switch (fetchResult.status) {
			case Success: {
				/* save to file */
				sp.SrcSave(fetchResult.output, fetchResult.date);
				bitmap = fetchResult.output;

				break;
			}
			case UpToDate: {
				upToDate = true;
				bitmap = sp.SrcBitmap();

				Log.i("DownloadImageTask", "Up to date");
				break;
			}
		}

		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		WindowManager windowManager = (WindowManager) bmImage.getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(progressView.view);
		if ( null != result ) {
			bmImage.setImageBitmap(result);
			if ( upToDate ) {
				Toast.makeText(bmImage.getContext(), "Up to date", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(bmImage.getContext(), "Download error", Toast.LENGTH_SHORT).show();
		}
	}
}