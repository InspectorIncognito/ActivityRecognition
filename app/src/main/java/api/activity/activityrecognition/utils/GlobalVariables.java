package api.activity.activityrecognition.utils;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by brahim on 18-01-16.
 */
public class GlobalVariables {

    private static final String TAG = "GlobalVariables";
    private static GlobalVariables instance = null;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long lastMeasuredTime = Long.MAX_VALUE;
    private Location previousLocation = null;

    public static GlobalVariables getInstance() {
        if (instance == null) {
            instance = new GlobalVariables();
        }

        return instance;
    }

    private GlobalVariables() {
    }

    public GoogleApiClient getApiClient() {
        return mGoogleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient client){
        mGoogleApiClient = client;
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }

    public void setLocationRequest(LocationRequest request){
        mLocationRequest = request;
    }

    public Location getPreviousLocation(){
        return previousLocation;
    }

    public void setPreviousLocation(Location location){
        previousLocation = location;
    }

    public void setLastMeasuredTime(long time){
        lastMeasuredTime = time;
    }

    public long getLastMeasuredTime(){
        return lastMeasuredTime;
    }
}
