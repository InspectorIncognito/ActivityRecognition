package api.activity.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationResult;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import api.activity.activityrecognition.R;
import api.activity.activityrecognition.utils.Constants;
import api.activity.activityrecognition.utils.Functions;
import api.activity.activityrecognition.utils.GlobalVariables;

public class DetectedActivitiesIntentService extends IntentService {

    private static final String TAG = "DetectionIntentService";

    private String logName;
    private GlobalVariables mGlobalVariables;

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logName = getString(R.string.log_filename);
        mGlobalVariables = GlobalVariables.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(TAG, "--------------------begin measurement--------------------");

        try {
            boolean didSomething = false;
            File f = new File(getFilesDir() + File.separator + logName);

            if(!f.exists() || !f.isFile())
                f.createNewFile();

            StringBuilder sb = new StringBuilder();

            /* Log format:
             day/month/year hour:minutes:seconds \t activity1-confidence1 \t activity2- ...... \n */

            long currentTime = System.currentTimeMillis();

            if(ActivityRecognitionResult.hasResult(intent)) {

                sb.append(getString(R.string.log_activity_tag));
                sb.append(Constants.TAB);

                ActivityRecognitionResult activityResult = ActivityRecognitionResult.extractResult(intent);

                for (DetectedActivity da : activityResult.getProbableActivities()) {
                    sb.append(Functions.getActivityString(this, da.getType()));
                    sb.append(Constants.COMMA);
                    sb.append(da.getConfidence());
                    sb.append(Constants.TAB);
                }

                /* used to notify the MainActivity about activity changes
                 * in order to update the currentActivity TextView */
                Intent textIntent = new Intent();
                textIntent.putExtra("most_probable", activityResult.getMostProbableActivity().getType());
                textIntent.putExtra("confidence", activityResult.getMostProbableActivity().getConfidence());
                textIntent.setAction(Constants.BROADCAST_GUI_ACTIVITY_UPDATE);
                LocalBroadcastManager.getInstance(this).sendBroadcast(textIntent);

                int activityType = activityResult.getMostProbableActivity().getType();
                int confidence = activityResult.getMostProbableActivity().getConfidence();

                if(activityType == DetectedActivity.IN_VEHICLE &&
                        confidence >= Constants.MIN_CONFIDENCE){

                    Intent mainIntent = new Intent();
                    mainIntent.putExtra("current_time", System.currentTimeMillis());
                    mainIntent.setAction(Constants.BROADCAST_ACTIVITY_UPDATE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(mainIntent);
                }

                didSomething = true;
            }

            if(LocationResult.hasResult(intent)){

                sb.append(getString(R.string.log_location_tag));
                sb.append(Constants.TAB);

                LocationResult locationResult = LocationResult.extractResult(intent);

                Location previousLocation = mGlobalVariables.getPreviousLocation();

                long timediff = currentTime - mGlobalVariables.getLastMeasuredTime();

                Location lastLocation = locationResult.getLastLocation();

                if(lastLocation != null) {
                    sb.append(lastLocation.getLatitude());
                    sb.append(Constants.COMMA);
                    sb.append(lastLocation.getLongitude());
                }
                else{
                    sb.append(getString(R.string.warn_no_location_found));
                }

                if(previousLocation != null) {
                    double distance = Functions.getVincentyDistance(
                            previousLocation.getLatitude(),
                            previousLocation.getLongitude(),
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude()
                    );

                    double speed = distance / TimeUnit.MILLISECONDS.toSeconds(timediff);

                    if(speed > Constants.AVERAGE_BUS_SPEED) {
                        Intent mainIntent = new Intent();
                        mainIntent.setAction(Constants.BROADCAST_LOCATION_UPDATE);
                        mainIntent.putExtra("current_time", System.currentTimeMillis());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(mainIntent);
                    }

                    sb.append(Constants.TAB);
                    sb.append(speed);

                    Intent textIntent = new Intent();
                    textIntent.setAction(Constants.BROADCAST_GUI_LOCATION_UPDATE);
                    textIntent.putExtra("speed", speed);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(textIntent);
                }

                mGlobalVariables.setPreviousLocation(lastLocation);
                mGlobalVariables.setLastMeasuredTime(currentTime);
                didSomething = true;
            }

            if(didSomething) {
                Intent fileIntent = new Intent(this, FileAccessingIntentService.class);
                fileIntent.putExtra("textToLog", sb.toString().trim());
                startService(fileIntent);
            }

            /* after a certain amount of time, the log file of the app
            is sent to the application server*/
            /*long currentTime = System.currentTimeMillis();

            if(currentTime - start > Constants.SEND_TO_SERVER_INTERVAL){
                startService(new Intent(this, FileSendingService.class));
                start = currentTime;
            }*/

        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        //Log.d(TAG, "---------------------end measurement---------------------");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}

