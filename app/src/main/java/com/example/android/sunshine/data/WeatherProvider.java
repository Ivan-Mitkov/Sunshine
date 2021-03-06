package com.example.android.sunshine.data;

/**
 * Created by ivan on 29.10.2017 г..
 */
import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;

public class WeatherProvider extends ContentProvider {

    //   Create static constant integer values named CODE_WEATHER & CODE_WEATHER_WITH_DATE to identify the URIs this ContentProvider can handle
    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;
    //  Instantiate a static UriMatcher using the buildUriMatcher method
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    WeatherDbHelper mOpenHelper;

    //   Write a method called buildUriMatcher where you match URI's to their numeric ID
    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //directory
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        //single task
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);
        return uriMatcher;

    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mOpenHelper = new WeatherDbHelper(context);
        return true;

    }
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        //Only perform our implementation of bulkInsert if the URI matches the CODE_WEATHER code
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db =mOpenHelper.getWritableDatabase();
        // Write URI match code and set a variable to return a Cursor
        int match =sUriMatcher.match(uri);
        switch (match){
            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for(ContentValues value:values){
                        long weatherData = value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                        if(!SunshineDateUtils.isDateNormalized(weatherData)){
                            throw new IllegalArgumentException("Date must be normalized to inserd");
                        }
                        long _id=db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,value);
                        if(_id!=-1){
                            rowsInserted++;
                        }

                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if(rowsInserted>0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri,values);
        }

    }
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db =mOpenHelper.getReadableDatabase();
        // Write URI match code and set a variable to return a Cursor
        int match =sUriMatcher.match(uri);
        Cursor cursor;
        switch (match){
            case CODE_WEATHER:
                // Query for the tasks directory and write a default case
                cursor=db.query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_WEATHER_WITH_DATE:
                //we need two stings one for selection and string[] for selectionArgs
                //for selection
                String mSelection= WeatherContract.WeatherEntry.COLUMN_DATE+"=?";
                //getLastPathSegment gets the decoded path segments as List<String> without '/'

                String normalizedUtcDateString =uri.getLastPathSegment();
                //initializing the array for selectionArgs
                String[] mSelectionArgs=new String[]{normalizedUtcDateString };

                cursor=db.query(WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);

        }
        //Set a notification URI on the Cursor and return that Cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;

        //Handle queries on both the weather and weather with date URI

    }
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int row;
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                // Query for the tasks directory and write a default case
                row = mOpenHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        if(row>0){
            // Notify the resolver of a change and return the number of items deleted
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return row;

    }
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Sunshine.");
    }
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "We are not implementing insert in Sunshine. Use bulkInsert instead");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in Sunshine");
    }
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}