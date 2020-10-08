package org.melk.earthpicture.helpers;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Spinner;

public final class ViewHelpers {

	public static class ProgressView {
		public ProgressBar view;
		public WindowManager.LayoutParams windowParams;

		public ProgressView(Context context) {
			view = new ProgressBar(context);
			windowParams = new WindowManager.LayoutParams();
		}
	}
	public static ViewHelpers.ProgressView getProgressView(Context context) {
		ViewHelpers.ProgressView progressView = new ViewHelpers.ProgressView(context);

		progressView.view.setBackgroundColor(0x00000000);
		progressView.windowParams.gravity = Gravity.CENTER;
		progressView.windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		progressView.windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		progressView.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		progressView.windowParams.format = PixelFormat.TRANSLUCENT;
		progressView.windowParams.windowAnimations = 0;

		return progressView;
	}

}
