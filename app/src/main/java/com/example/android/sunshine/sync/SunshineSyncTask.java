package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask{
    synchronized public static void syncWeather(Context context){
        //perform network request
            //get url
        URL requestedURL= NetworkUtils.getUrl(context);

        try {
            //uses url to retrieve json
            String jsonURL= NetworkUtils.getResponseFromHttpUrl(requestedURL);

            //parse jsonURL into a list of values
            ContentValues[] contentValues=
                    OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context,jsonURL);

            //insert contentValues in Content Provider
                    //check if there ara any values
            if(contentValues!=null&&contentValues.length!=0){
                ContentResolver contentResolver=context.getContentResolver();
                //   (4) If we have valid results, delete the old data and insert the new
                contentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI,null,null);
                contentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,contentValues);
            }

        }
       catch (Exception e){
           e.printStackTrace();
       }


    }
}

//   (1) Create a class called SunshineSyncTask
//   (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
//   (3) Within syncWeather, fetch new weather data
