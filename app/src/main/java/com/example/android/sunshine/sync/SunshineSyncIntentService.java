
package com.example.android.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class SunshineSyncIntentService extends IntentService {
    public SunshineSyncIntentService(){
        super("SunshineSyncIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        SunshineSyncTask.syncWeather(this);
    }
}


//  (5) Create a new class called SunshineSyncIntentService that extends IntentService
//   (6) Create a constructor that calls super and passes the name of this class
//   (7) Override onHandleIntent, and within it, call SunshineSyncTask.syncWeather