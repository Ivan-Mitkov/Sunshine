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
        final TextView weatherSummary;

        ForecastAdapterViewHolder(View view) {
            super(view);
            weatherSummary = (TextView) view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(weatherSummary.getText().toString());
        }
    }

    //creating adapter with OnClickHandler in particular context
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        mContext=context;
    }
    public interface ForecastAdapterOnClickHandler {
        void onClick(String weatherForDay);
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

        long millisecondsDate = mCursor.getLong(MainActivity.INDEX_COLUMN_DATE);
        String date= SunshineDateUtils.getFriendlyDateString(mContext,millisecondsDate,false);

        int weatherId = mCursor.getInt(MainActivity.INDEX_COLUMN_WEATHER_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);

        double maxTemp = mCursor.getDouble(MainActivity.INDEX_COLUMN_MAX_TEMP);
        double minTemp = mCursor.getDouble(MainActivity.INDEX_COLUMN_MIN_TEMP);

        String highAndLowTemperature =
                SunshineWeatherUtils.formatHighLows(mContext, maxTemp, minTemp);

        String weatherSummary = date + " - " + description + " - " + highAndLowTemperature;
        // Display the summary
        forecastAdapterViewHolder.weatherSummary.setText(weatherSummary);
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