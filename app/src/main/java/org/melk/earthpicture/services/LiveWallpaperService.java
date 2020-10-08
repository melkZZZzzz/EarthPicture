package org.melk.earthpicture.services;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import org.melk.earthpicture.R;
import org.melk.earthpicture.helpers.SharedPreferencesHelpers;
import org.melk.earthpicture.storage.StorageProvider;
import org.melk.earthpicture.work.EarthPictureWorkManager;
import org.melk.earthpicture.worker.LiveWallpaperContinuationWorker;

import java.util.concurrent.TimeUnit;

public class LiveWallpaperService extends WallpaperService {

	public static final String NOTIFY_CHANGE = LiveWallpaperService.class.getName() + ".NOTIFY_CHANGE";

	private Context _context;
	private EarthPictureWorkManager _workManager;
	private boolean _syncing;

	@Override
	public void onCreate() {
		_context = this.getApplicationContext();
		_workManager = new EarthPictureWorkManager(_context);
		_syncing = false;
		super.onCreate();

		SharedPreferencesHelpers.InitializeIfNot(_context);
	}

	@Override
	public Engine onCreateEngine() {
		SharedPreferencesHelpers.SynchronizationMode(_context, SharedPreferencesHelpers.SyncMode.LIVE_WALLPAPER);
		SharedPreferencesHelpers.IsSynchronizationActive(_context, true);
		return new LiveWallpaperEngine();
	}

	private class LiveWallpaperEngine extends Engine {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};

		private LiveWallpaperBroadcastReceiver broadcastReceiver;

		private int width = 0;
		private int height = 0;
		private boolean visible = true;
		private boolean touchEnabled = false;

		public LiveWallpaperEngine() {
			broadcastReceiver = new LiveWallpaperBroadcastReceiver(LiveWallpaperService.this);
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(LiveWallpaperService.NOTIFY_CHANGE);
			registerReceiver(broadcastReceiver, intentFilter);

			_workManager.QueueUnique(
					getString(R.string.earth_picture_unique_work_key),
					EarthPictureWorkManager.WorkRequestPeriodicBuilder(LiveWallpaperContinuationWorker.class, SharedPreferencesHelpers.SynchronizationPeriod(_context), TimeUnit.MINUTES)
							.setConstraints(EarthPictureWorkManager.GetRecommendedConstraints())
							.build()
			);

			handler.post(drawRunner);
		}

		@Override
		public void onDestroy() {
			if (broadcastReceiver != null) {
				unregisterReceiver(broadcastReceiver);
			}
			broadcastReceiver = null;

			super.onDestroy();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			this.visible = false;
			handler.removeCallbacks(drawRunner);
			super.onSurfaceDestroyed(holder);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			this.width = width;
			this.height = height;
			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (touchEnabled) {
				float x = event.getX();
				float y = event.getY();
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas();
					if (canvas != null) {
						// TODO: code here ...
					}
				} finally {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}
				super.onTouchEvent(event);
			}
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					StorageProvider sp = new StorageProvider(_context);
					Bitmap bmp = sp.BackgroundBitmap();

					if (null != bmp) {
						canvas.drawBitmap(bmp, null, new Rect(0, 0, width, height), null);
					} else if (!_syncing) {
						_syncing = true;
						_workManager.cancelUnique(getString(R.string.earth_picture_unique_work_key));
						_workManager.QueueUnique(
								getString(R.string.earth_picture_unique_work_key),
								EarthPictureWorkManager.WorkRequestPeriodicBuilder(LiveWallpaperContinuationWorker.class, SharedPreferencesHelpers.SynchronizationPeriod(_context), TimeUnit.MINUTES)
										.setConstraints(EarthPictureWorkManager.GetRecommendedConstraints())
										.build()
						);
					}
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			handler.removeCallbacks(drawRunner);
		}

		private class LiveWallpaperBroadcastReceiver extends BroadcastReceiver {
			private LiveWallpaperService _service;

			public LiveWallpaperBroadcastReceiver(LiveWallpaperService service) {
				_service = service;
			}

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (action.equals(NOTIFY_CHANGE)) {
					_syncing = false;
					handler.postDelayed(drawRunner, 100);
				}
			}
		}

	}

	public static boolean SetLiveWallpaperIfNotSet(Context context) {
		boolean isSet = IsLiveWallpaperSet(context);
		if ( !isSet ) {
			Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
			intent.putExtra(
					WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
					new ComponentName(context, LiveWallpaperService.class)
			);
			context.startActivity(intent);
		}
		return isSet;
	}
	public static boolean IsLiveWallpaperSet(Context context) {
		WallpaperManager manager = WallpaperManager.getInstance(context);
		WallpaperInfo info = manager.getWallpaperInfo();
		boolean isSet = false;

		if (null != info) {
			String pName = info.getPackageName();
			String _Name = context.getPackageName();
			isSet = (0 == pName.compareTo(_Name));
		}

		return isSet;
	}

	public static void sendBroadcast(Context context, String action) {
		if (action.startsWith(LiveWallpaperService.class.getName())) {
			Intent intent = new Intent();
			intent.setAction(action);
			context.sendBroadcast(intent);
		}
	}

}
