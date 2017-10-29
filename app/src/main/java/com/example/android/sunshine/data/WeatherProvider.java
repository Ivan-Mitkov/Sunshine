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
        throw new RuntimeException("Student, you need to implement the bulkInsert mehtod!");
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
                String mSelection="_id=?";//for selection
                //getPathSegments gets the decoded path segments as List<String> without '/'
                // than get() returns the element on position in uri
                //position 0 is table name, 1 is id wich we need
                String id=uri.getPathSegments().get(1);
                String[] mSelectionArgs=new String[]{id};//initializing the array for selectionArgs
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
        throw new RuntimeException("Student, you need to implement the delete method!");
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