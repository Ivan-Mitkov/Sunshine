package com.example.android.sunshine.sync;
import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

//  Make sure you've imported the jobdispatcher.JobService, not job.JobService
//  Add a class called SunshineFirebaseJobService that extends jobdispatcher.JobService

public class SunshineFirebaseJobService extends JobService{

    //   Declare an ASyncTask field called mFetchWeatherTask
    private AsyncTask mFetchWeatherTask;
//   Override onStartJob and within it, spawn off a separate ASyncTask to sync weather data

    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchWeatherTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = SunshineFirebaseJobService.this;
                SunshineSyncTask.syncWeather(context);
                return null;
            }
            //  Once the weather data is sync'd, call jobFinished with the appropriate arguments
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                jobFinished(job,false);
            }
        };
        mFetchWeatherTask.execute();
        return true;
    }

//   Override onStopJob, cancel the ASyncTask if it's not null and return true
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchWeatherTask != null) mFetchWeatherTask.cancel(true);
        return true;
    }
}





