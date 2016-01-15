package api.activity.activityrecognition.utils;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

import api.activity.activityrecognition.R;

/**
 * Created by brahim on 11-01-16.
 */
public class Functions {
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public static String getActivityStringInSpanish(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle_ES);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle_ES);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot_ES);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running_ES);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still_ES);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting_ES);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown_ES);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking_ES);
            default:
                return resources.getString(R.string.unidentifiable_activity_ES, detectedActivityType);
        }
    }

    public static int getActivityCode(Context context, String detectedActivityType) {
        Resources resources = context.getResources();
        if(detectedActivityType.equals(resources.getString(R.string.in_vehicle)))
            return DetectedActivity.IN_VEHICLE;
        if(detectedActivityType.equals(resources.getString(R.string.on_bicycle)))
            return DetectedActivity.ON_BICYCLE;
        if(detectedActivityType.equals(resources.getString(R.string.on_foot)))
            return DetectedActivity.ON_FOOT;
        if(detectedActivityType.equals(resources.getString(R.string.running)))
            return DetectedActivity.RUNNING;
        if(detectedActivityType.equals(resources.getString(R.string.still)))
            return DetectedActivity.STILL;
        if(detectedActivityType.equals(resources.getString(R.string.tilting)))
            return DetectedActivity.TILTING;
        if(detectedActivityType.equals(resources.getString(R.string.unknown)))
            return DetectedActivity.UNKNOWN;
        if(detectedActivityType.equals(resources.getString(R.string.walking)))
            return DetectedActivity.WALKING;

        return -1;
    }
}
