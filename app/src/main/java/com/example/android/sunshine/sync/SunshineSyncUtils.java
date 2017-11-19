
package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.sunshine.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class SunshineSyncUtils{
    //   Add constant values to sync Sunshine every 3 - 4 hours
    private static final int REMINDER_INTERVAL_HOURS = 3;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(REMINDER_INTERVAL_HOURS));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;
//  Declare a private static boolean field called sInitialized
    private static boolean sInitialized;
    //   Add a sync tag to identify our sync job
    private static final String SYNC_TAG = "sync_tag";
    //  Create a method to schedule our periodic weather sync
    // Create a synchronized public static void method called initialize

    synchronized public static void scheduleChargingReminder(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync data*/
        Job constraintSyncJob = dispatcher.newJobBuilder()
                /* The Service that will be used to write to preferences */
                .setService(SunshineFirebaseJobService.class)

                .setTag(SYNC_TAG)

                .setConstraints(Constraint.ON_ANY_NETWORK)

                .setLifetime(Lifetime.FOREVER)

                .setRecurring(true)

                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(constraintSyncJob);

    }

    synchronized public static void initialize(final Context context){
        if(sInitialized)return;
        //   Only execute this method body if sInitialized is false
        //  If the method body is executed, set sInitialized to true

        sInitialized=true;
        scheduleChargingReminder(context);
        // Call the method you created to schedule a periodic weather sync

        //  Check to see if our weather ContentProvider is empty
        Thread checkIfEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
     /*Cursor query(@NonNull Uri uri, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder)*/
                Uri chek= WeatherContract.WeatherEntry.CONTENT_URI;
                //we just need to project the ID of each row
                String [] projectionsColumns = {WeatherContract.WeatherEntry._ID};
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                Cursor cursor=
                        context.getContentResolver().query(chek,projectionsColumns,selection,null,null);
                //  If it is empty or we have a null Cursor, sync the weather now!

                if (cursor==null||cursor.getCount()==0){
                    SunshineSyncUtils.startImmediateSync(context);
                }
                cursor.close();
            }
        });
        checkIfEmpty.run();

    }


    public static void startImmediateSync(final Context context){
        Intent intent=new Intent(context,SunshineSyncIntentService.class);
        context.startService(intent);
    }
}

//  Create a class called SunshineSyncUtils
    // Create a public static void method called startImmediateSync
    //  Within that method, start the SunshineSyncIntentService