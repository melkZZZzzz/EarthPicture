package org.melk.earthpicture.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;


public class EarthPictureWorkManager {

	public WorkManager workManager;

	public EarthPictureWorkManager( @NonNull Context context ) {
		this.workManager = WorkManager.getInstance(context);
	}

	public static OneTimeWorkRequest.Builder WorkRequestSimpleBuilder(Class T) {
		return new OneTimeWorkRequest.Builder(T);
	}

	public static PeriodicWorkRequest.Builder WorkRequestPeriodicBuilder(Class T, int interval, TimeUnit unit) {
		return new PeriodicWorkRequest.Builder(T, interval, unit);
	}

	public static Constraints GetRecommendedConstraints() {
		return new Constraints.Builder()
				.setRequiresBatteryNotLow(true)
				.setRequiredNetworkType(NetworkType.CONNECTED)
				.build();
	}

	public void Queue(OneTimeWorkRequest workRequest) {
		workManager.enqueue(workRequest);
	}
	public void QueueUnique(String workLabel, PeriodicWorkRequest workRequest) {
		workManager.enqueueUniquePeriodicWork(workLabel, ExistingPeriodicWorkPolicy.KEEP, workRequest);
	}
	public void cancelUnique(String workLabel) {
		workManager.cancelUniqueWork(workLabel);
	}

}
