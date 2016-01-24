package api.activity.activityrecognition.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import api.activity.activityrecognition.MainActivity;
import api.activity.activityrecognition.R;
import api.activity.activityrecognition.services.FileAccessingIntentService;
import api.activity.activityrecognition.utils.Constants;

/**
 * Created by brahim on 20-01-16.
 *
 * Broadcast receiver used to detect changes in activities and position of the user that could
 * possibly indicate a transition between being on foot to getting on a vehicle, and to gather and
 * process said information by applying hardcoded heuristics in order to filter false positives and
 * false negatives given by the Google ActivityRecognition API and the GPS location readings.
 */

public class HeuristicsReceiver extends BroadcastReceiver{

    private boolean activityOnBus = false;
    private boolean locationOnBus = false;
    private int activityCounter = 0;
    private int locationCounter = 0;
    private long activityTime = Long.MAX_VALUE;
    private long locationTime = Long.MAX_VALUE;

    private MainActivity ma;

    public HeuristicsReceiver(MainActivity activity){
        ma = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long currentTime = intent.getLongExtra("current_time", Long.MAX_VALUE);

        if(intent.getAction().equals(Constants.BROADCAST_ACTIVITY_UPDATE)) {
            updateActivityCounter(currentTime);
            if(getLocationOnBus() && currentTime - getLocationTime() > Constants.MAX_DELAY){
                setLocationOnBus(false, Long.MAX_VALUE);
            }
        }

        if(intent.getAction().equals(Constants.BROADCAST_LOCATION_UPDATE)) {
            updateLocationCounter(currentTime);
            if(getActivityOnBus() && currentTime - getActivityTime() > Constants.MAX_DELAY){
                setActivityOnBus(false, Long.MAX_VALUE);
            }
        }

        if(getActivityOnBus() && getLocationOnBus()) {
            NotificationManager mNotificationManager =
                    (NotificationManager) ma.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, ma.mBuilder.build());

            Intent fileIntent = new Intent(ma, FileAccessingIntentService.class);
            fileIntent.putExtra("textToLog",
                    ma.getString(R.string.log_notification_tag)
                            + Constants.TAB
                            + "Status,Sent");

            ma.startService(fileIntent);

            setActivityOnBus(false, Long.MAX_VALUE);
            setLocationOnBus(false, Long.MAX_VALUE);
        }
    }

    public boolean getActivityOnBus(){
        return activityOnBus;
    }

    public long getActivityTime(){
        return  activityTime;
    }

    public void setActivityOnBus(boolean bool, long time){
        activityOnBus = bool;
        activityTime = time;
    }

    public boolean getLocationOnBus(){
        return locationOnBus;
    }

    public long getLocationTime(){
        return locationTime;
    }

    public void setLocationOnBus(boolean bool, long time){
        locationOnBus = bool;
        locationTime = time;
    }

    public void updateActivityCounter(long currentTime){
        activityCounter++;
        if(activityCounter == Constants.MIN_ACTIVITY_REPETITIONS){
            activityCounter = 0;
            setActivityOnBus(true, currentTime);
        }
    }

    public void updateLocationCounter(long currentTime){
        locationCounter++;
        if(locationCounter == Constants.MIN_SPEED_REPETITIONS){
            locationCounter = 0;
            setLocationOnBus(true, currentTime);
        }
    }
}
