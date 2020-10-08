package org.melk.earthpicture.helpers;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class PictureHelpers {

	public enum Status { Success, UpToDate, Failed }
	public static class Result<T> {
		public Status status;
		public Date date;
		public T output;
		public Result() {
			this.status = Status.Failed;
			this.date = null;
			this.output = null;
		}
		public Result(Status status, Date date, T output) {
			this.status = status;
			this.date = date;
			this.output = output;
		}
	}

	private static Bitmap FetchBitmap(URL url) {
		Bitmap bitmap = null;

		try {
			InputStream in = url.openStream();
			/* fetch bitmap */
			bitmap = BitmapFactory.decodeStream(in);
		} catch (IOException e) {
			Log.e("FetchImage", "Error downloading image: " + e.getMessage());
		}

		return bitmap;
	}
	public static Result<Bitmap> FetchBitmapIf(URL url, Date srcDate) {
		Result<Bitmap> result = new Result<>();

		try {
			URLConnection connection = url.openConnection();
			String lastModified = connection.getHeaderField("Last-Modified");
			SimpleDateFormat fmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
			Date lastDate = (lastModified == null) ? null : fmt.parse(lastModified);

			if ( null == lastDate || null == srcDate || srcDate.before(lastDate) ) {
				/* fetch bitmap */
				result.output = FetchBitmap(url);
				result.date = lastDate;
				result.status = Status.Success;
			} else {
				Log.i("FetchBitmapIf", "Up to date");
				result.output = FetchBitmap(url);
				result.status = Status.UpToDate;
			}
		} catch (ParseException | IOException e) {
			Log.e("FetchBitmapIf", "Error dating remote image: " + e.getMessage());
		}

		return result;
	}

	public static Bitmap AdaptToDisplay(Context context, Bitmap inputBitmap) {
		/* FIXME: crop EUMSAT copyright is probably illegal */
		Bitmap imageBitmap = Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.getWidth(), inputBitmap.getHeight() - 80 );
		float iw = imageBitmap.getWidth();
		float ih = imageBitmap.getHeight();

		/* Screen dimensions */
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int rotation = display.getRotation();
		Point size = new Point();
		display.getRealSize(size);
		float vw = ( rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180 ) ? size.x : size.y;
		float vh = ( rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180 ) ? size.y : size.x;
		float vwRatio = ( vw / vh > 1.0f ) ? ( vw / vh ) : 1.0f;
		float vhRatio = ( vh / vw > 1.0f ) ? ( vh / vw ) : 1.0f;

		/* Background Image dimensions */
		float owMin = Math.max( vw, iw );
		float ohMin = Math.max( vh, ih );
		float owMinRatio = ( owMin / ohMin > 1.0f ) ? ( owMin / ohMin ) : 1.0f;
		float ohMinRatio = ( ohMin / owMin > 1.0f ) ? ( ohMin / owMin ) : 1.0f;
		float ow = iw;
		float oh = ih;

		if ( owMinRatio < vwRatio ) {
			ow = ohMin * vwRatio;
		}
		if ( ohMinRatio < vhRatio ) {
			oh = owMin * vhRatio;
		}

		Bitmap background = Bitmap.createBitmap( Math.round(ow), Math.round(oh), Bitmap.Config.ARGB_8888 );
		background.eraseColor( imageBitmap.getPixel(0,0) );

		Canvas canvas = new Canvas( background );
		float left = ( ow - iw ) / 2;
		float top = ( oh - ih ) / 2;

		canvas.drawBitmap( imageBitmap, left, top, new Paint() );

		return background;
	}

	public static boolean SetWallpaper(Context context, Bitmap inputBitmap) {
		boolean bOK = false;

		WallpaperManager wallpaperManager = WallpaperManager.getInstance( context );
		try {
			wallpaperManager.setBitmap( inputBitmap );
			bOK = true;
		} catch (Exception e) {
			Log.e("SetWallpaper", "Error setting Wallpaper: " + e.getMessage());
		}

		return bOK;
	}

	public static Bitmap CropBitmap(Bitmap bitmap, Rect cropHint) {
		return Bitmap.createBitmap(bitmap, cropHint.left, cropHint.top, cropHint.right-cropHint.left, cropHint.bottom-cropHint.top );
	}
}
