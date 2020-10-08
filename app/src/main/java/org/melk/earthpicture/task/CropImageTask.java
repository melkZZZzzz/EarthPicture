package org.melk.earthpicture.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.WindowManager;
import android.widget.ImageView;

import org.melk.earthpicture.helpers.PictureHelpers;
import org.melk.earthpicture.helpers.ViewHelpers;

public class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {

	protected ImageView bmImage;
	protected ViewHelpers.ProgressView progressView;

	public CropImageTask(ImageView bmImage) {
		this.bmImage = bmImage;
		this.progressView = ViewHelpers.getProgressView(bmImage.getContext());
	}

	@Override
	public void onPreExecute() {
		WindowManager windowManager = (WindowManager) bmImage.getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(progressView.view, progressView.windowParams);
	}

	@Override
	protected Bitmap doInBackground(Bitmap... bmps) {
		Bitmap bmp = bmps[0];
		Bitmap background = PictureHelpers.AdaptToDisplay(bmImage.getContext(), bmp);

		return background;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		WindowManager windowManager = (WindowManager) bmImage.getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(progressView.view);
		bmImage.setImageBitmap(result);
	}

}