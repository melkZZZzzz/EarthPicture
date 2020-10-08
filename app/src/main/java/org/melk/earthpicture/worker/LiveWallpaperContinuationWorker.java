package org.melk.earthpicture.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import org.melk.earthpicture.EarthPictureActivity;
import org.melk.earthpicture.helpers.SharedPreferencesHelpers;
import org.melk.earthpicture.work.EarthPictureWorkManager;
import org.melk.earthpicture.work.EarthPictureWorker;


public class LiveWallpaperContinuationWorker extends EarthPictureWorker {

	static final protected String JobChainTag = "update_background";

	private Context context;

	public LiveWallpaperContinuationWorker(@NonNull Context context, @NonNull WorkerParameters params ) {
		super(context, params);
		this.context = context;
	}

	@Override
	public boolean doJob() {
		EarthPictureWorkManager earthPictureWorkManager = new EarthPictureWorkManager(context);
		String imageName = SharedPreferencesHelpers.GetWallpaperCandidateImageName(context);
		Data workProperties = new Data.Builder()
				.putString("imageName", imageName)
				.putString("url", EarthPictureActivity.imgURL + imageName)
				.build();

		earthPictureWorkManager.workManager
				.beginWith(EarthPictureWorkManager.WorkRequestSimpleBuilder(DownloadImageWorker.class)
						.setInputData(workProperties)
						.addTag(JobChainTag)
						.build()
				)
				.then(EarthPictureWorkManager.WorkRequestSimpleBuilder(CropImageWorker.class)
						.setInputData(workProperties)
						.addTag(JobChainTag)
						.build()
				)
				.then(EarthPictureWorkManager.WorkRequestSimpleBuilder(SetLiveWallpaperWorker.class)
						.setInputData(workProperties)
						.addTag(JobChainTag)
						.build()
				)
				.enqueue();

		return true;
	}

}