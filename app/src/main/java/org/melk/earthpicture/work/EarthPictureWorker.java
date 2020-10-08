package org.melk.earthpicture.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public abstract class EarthPictureWorker extends Worker implements EarthPictureJob {

	public EarthPictureWorker( @NonNull Context context, @NonNull WorkerParameters params ) {
		super(context, params);
	}

	public abstract boolean doJob();

	@Override
	public Result doWork() {

		if ( doJob() ) return Result.success();

		return Result.failure();
	}

}
