package org.melk.earthpicture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import org.melk.earthpicture.helpers.ImageViewGestures;
import org.melk.earthpicture.helpers.PictureHelpers;
import org.melk.earthpicture.helpers.SharedPreferencesHelpers;
import org.melk.earthpicture.services.LiveWallpaperService;
import org.melk.earthpicture.storage.StorageProvider;
import org.melk.earthpicture.task.CropImageTask;
import org.melk.earthpicture.task.DownloadImageTask;
import org.melk.earthpicture.task.SetWallpaperTask;
import org.melk.earthpicture.work.EarthPictureWorkManager;

public class EarthPictureActivity extends AppCompatActivity {

	private EarthPictureWorkManager earthPictureWorkManager;

	private ImageViewGestures imageViewGestures;

	protected static final String _domain = "https://eumetview.eumetsat.int/";
	protected static final String _service = "static-images/latestImages/";
	public static final String imgURL = EarthPictureActivity._domain + EarthPictureActivity._service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.earth_picture_activity);

		Toolbar toolbar = findViewById(R.id.toolbar) ;
		setSupportActionBar(toolbar);

		earthPictureWorkManager = new EarthPictureWorkManager(this);

		ImageView imageView = findViewById(R.id.imageView);
		imageViewGestures = new ImageViewGestures(imageView);

		StorageProvider sp = new StorageProvider(this);
		Bitmap imageBitmap = sp.SrcBitmap();
		if (null == imageBitmap) {
			SharedPreferencesHelpers.InitializeIfNot(this);
			downloadImage();
		} else {
			imageView.setImageBitmap( imageBitmap );
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		imageViewGestures.onTouchEvent(motionEvent);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.picture_menu, menu);
		return true;
	}

	public void downloadImage() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		ImageView imageView = (ImageView) findViewById( R.id.imageView );
		String imgName = sharedPreferences.getString(getString(R.string.satellite_pref_key), null);

		if ( null != imgName ) {
			Toast.makeText( this, "downloading the image", Toast.LENGTH_SHORT).show();
			new DownloadImageTask( imageView ).execute( imgURL + imgName );
		} else {
			Toast.makeText( this, getString(R.string.satellite_pref_title) + " should be configured", Toast.LENGTH_SHORT).show();
		}
	}

	public void cropPicture(Rect visibleRect) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageViewGestures.imageView.getDrawable();
		Bitmap croppedBitmap = PictureHelpers.CropBitmap(bitmapDrawable.getBitmap(), visibleRect);

		imageViewGestures.imageView.setImageBitmap( croppedBitmap );
		imageViewGestures.resetScale();

		SharedPreferencesHelpers.SetWallpaperCrop(this, visibleRect);
	}
	public void cropPictureAuto() {
		ImageView imageView = (ImageView) findViewById( R.id.imageView );

		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
		new CropImageTask( imageView ).execute( bitmapDrawable.getBitmap() );
	}

	public void setAsWallpaper() {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageViewGestures.imageView.getDrawable();
		new SetWallpaperTask( this ).execute( bitmapDrawable.getBitmap() );
	}
	public void setAsLiveWallpaper() {
		LiveWallpaperService.SetLiveWallpaperIfNotSet(this);
	}
	public void gotoAbout() {
		Intent intent = new Intent(this, AboutActivity.class);
		intent.putExtra("help", "");
		startActivity( intent );
	}

	public void gotoSettings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity( intent );
	}

	/* menu actions */

	public void onSyncAction(MenuItem mi) {
		downloadImage();
	}

	public void onCropAction(MenuItem mi) {
		if ( imageViewGestures.scaleFactor > 1.f ) {
			Rect visibleRect = imageViewGestures.getVisibleRect();
			cropPicture(visibleRect);
		} else {
			cropPictureAuto();
		}
	}

	public void onWallpaperAction(MenuItem mi) {
		setAsWallpaper();
	}

	public void onLiveWallpaperAction(MenuItem mi) {
		setAsLiveWallpaper();
	}

	public void onAbout(MenuItem mi) {
		gotoAbout();
	}

}
