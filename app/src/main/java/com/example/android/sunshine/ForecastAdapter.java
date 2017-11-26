package com.example.android.sunshine;

/**
 * Created by ivan on 14.10.2017 г..
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

// Create the basic adapter extending from RecyclerView.Adapter
// the custom ViewHolder which gives us access to our views
//The adapter's role is to convert an object at a position into a list row item to be inserted.
//However with a RecyclerView the adapter requires the existence of a "ViewHolder" object
//which describes and provides access to all the views within each item row.

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    final private ForecastAdapterOnClickHandler mClickHandler;

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTemp;
        final TextView lowTemp;
        final ImageView weatherIcon;

        ForecastAdapterViewHolder(View view) {
            super(view);
            dateView=(TextView)view.findViewById(R.id.date);
            descriptionView=(TextView)view.findViewById(R.id.weather_description);
            highTemp=(TextView)view.findViewById(R.id.high_temperature);
            lowTemp=(TextView)view.findViewById(R.id.low_temperature);
            weatherIcon=(ImageView)view.findViewById(R.id.weatherIcon);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
//          Instead of passing the String for the clicked item, pass the date from the cursor
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }

    //creating adapter with OnClickHandler in particular context
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext=context;
    }
    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutIdForListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        view.setFocusable(true);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        //move the cursor
        mCursor.moveToPosition(position);

        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
         /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        forecastAdapterViewHolder.dateView.setText(dateString);


         /* Use the weatherId to obtain the proper description */
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImage=SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
        forecastAdapterViewHolder.weatherIcon.setImageResource(weatherImage);

        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        forecastAdapterViewHolder.descriptionView.setText(description);
        String descripionAlly = mContext.getString(R.string.a11y_forecast,description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descripionAlly);

         /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highTempString=SunshineWeatherUtils.formatTemperature(mContext,highInCelsius);
        String highTemp=mContext.getString(R.string.a11y_high_temp,highTempString);
        forecastAdapterViewHolder.highTemp.setText(highTempString);
        forecastAdapterViewHolder.highTemp.setContentDescription(highTemp);

         /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        String lowTempString=SunshineWeatherUtils.formatTemperature(mContext,lowInCelsius);
        String lowTemp=mContext.getString(R.string.a11y_low_temp,lowTempString);
        forecastAdapterViewHolder.lowTemp.setText(lowTempString);
        forecastAdapterViewHolder.lowTemp.setContentDescription(lowTemp);


    }

    @Override
    public int getItemCount() {

        if(mCursor==null){
            return 0;
        }
        return mCursor.getCount();

    }

    public void swapCursor(Cursor c) {

        mCursor=c;
        notifyDataSetChanged();

    }

}