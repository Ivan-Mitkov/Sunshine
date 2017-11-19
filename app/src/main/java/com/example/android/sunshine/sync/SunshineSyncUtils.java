
package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.sunshine.data.WeatherContract;

public class SunshineSyncUtils{

//  Declare a private static boolean field called sInitialized

    private static boolean sInitialized;

    // Create a synchronized public static void method called initialize
    synchronized public static void initialize(final Context context){
        if(sInitialized)return;
        //   Only execute this method body if sInitialized is false
        //  If the method body is executed, set sInitialized to true

        sInitialized=true;

        //  Check to see if our weather ContentProvider is empty
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
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
                return null;
            }
        }.execute();

    }


    public static void startImmediateSync(final Context context){
        Intent intent=new Intent(context,SunshineSyncIntentService.class);
        context.startService(intent);
    }
}



//  Create a class called SunshineSyncUtils
    // Create a public static void method called startImmediateSync
    //  Within that method, start the SunshineSyncIntentService