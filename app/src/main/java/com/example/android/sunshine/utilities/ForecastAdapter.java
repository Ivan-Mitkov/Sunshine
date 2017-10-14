package com.example.android.sunshine.utilities;

/**
 * Created by ivan on 14.10.2017 г..
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.R;

// Create the basic adapter extending from RecyclerView.Adapter
// the custom ViewHolder which gives us access to our views
//The adapter's role is to convert an object at a position into a list row item to be inserted.
//However with a RecyclerView the adapter requires the existence of a "ViewHolder" object
//which describes and provides access to all the views within each item row.

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    //data
    private String[] mWeatherData;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ForecastAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(String weatherForDay);
    }

    /**

     The on-click handler for this adapter. This single handler is called when an item is clicked.
     */
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public final TextView mWeatherTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview

        public ForecastAdapterViewHolder(View view) {
            super(view);

            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ForecastAdapterViewHolder(view);
    }

    /**
     * Every adapter has three primary methods:
     onCreateViewHolder to inflate the item layout and create the holder,
     onBindViewHolder to set the view attributes based on the data and
     getItemCount to determine the number of items.
     */
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        //taking individual data from data source using position
        String weatherForThisDay = mWeatherData[position];
        //binding this data to adapter
        forecastAdapterViewHolder.mWeatherTextView.setText(weatherForThisDay);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mWeatherData) return 0;
        return mWeatherData.length;
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param weatherData The new weather data to be displayed.
     */
    public void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }
}