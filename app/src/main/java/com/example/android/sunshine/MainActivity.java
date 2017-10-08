/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mWeatherDisplay;
    TextView mErrorMessageDisplay;
    ProgressBar mProgresDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mWeatherDisplay = (TextView) findViewById(R.id.tv_weather_data);
        mErrorMessageDisplay=(TextView)findViewById(R.id.error_msg);
        mProgresDisplay=(ProgressBar)findViewById(R.id.progres_bar);
        loadWeatherData();

    }

    private void loadWeatherData() {
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }
    private void showWeatherDataView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mWeatherDisplay.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage(){
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mWeatherDisplay.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mWeatherDisplay.setText("");
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //sending location as string and getting array of string with weathe conditions
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }
            String location = params[0];
            //creating url for this location
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                //creating connection and getting response as json string
                String jsonWeatherResponse =
                        NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                //parsing json string to create array of conditions
                String[] simpleJsonWeatherData =
                        OpenWeatherJsonUtils
                                .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
                return simpleJsonWeatherData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPreExecute() {
            mProgresDisplay.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String[] weatherConditions) {
            mProgresDisplay.setVisibility(View.INVISIBLE);
            if (weatherConditions != null) {
                showWeatherDataView();
                for (String condition : weatherConditions) {
                    mWeatherDisplay.append(condition + "\n\n\n");
                }

            }
            else {
                showErrorMessage();
            }
        }
    }
}