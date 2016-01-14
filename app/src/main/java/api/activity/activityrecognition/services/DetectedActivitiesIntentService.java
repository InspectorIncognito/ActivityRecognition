package api.activity.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import api.activity.activityrecognition.R;
import api.activity.activityrecognition.utils.Constants;
import api.activity.activityrecognition.utils.Functions;

public class DetectedActivitiesIntentService extends IntentService {

    private static final String TAG = "DetectionService";

    private String logName;

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logName = getString(R.string.activity_log_filename);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "--------------------begin measurement--------------------");

        try {
            StringBuilder sb = new StringBuilder();

            /* Log format:
             day/month/year hour:minutes:seconds \t activity1-confidence1 \t activity2- ...... \n */

            /* date and time of the current measurement */
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            sb.append(currentDateTimeString);
            sb.append(Constants.TAB);

            if(ActivityRecognitionResult.hasResult(intent)) {

                ActivityRecognitionResult activityResult = ActivityRecognitionResult.extractResult(intent);

                for (DetectedActivity da : activityResult.getProbableActivities()) {
                    sb.append(Functions.getActivityString(this, da.getType()));
                    sb.append(Constants.HYPHEN);
                    sb.append(da.getConfidence());
                    sb.append(Constants.TAB);
                }
            }

            if(LocationResult.hasResult(intent)){

                LocationResult locationResult = LocationResult.extractResult(intent);

                Location lastLocation = locationResult.getLastLocation();

                if(lastLocation != null) {
                    sb.append(lastLocation.getLatitude());
                    sb.append(Constants.COMMA);
                    sb.append(lastLocation.getLongitude());
                }
                else{
                    sb.append(getString(R.string.warn_no_location_found));
                }
            }

            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(getFilesDir() + File.separator + logName, true));

            bw.write(sb.toString().trim());
            bw.newLine();

            bw.close();

            /* debug code */
            BufferedReader br = new BufferedReader(new FileReader(new File(getFilesDir() + File.separator + logName)));
            String line;

            while((line = br.readLine()) != null)
                Log.e(TAG, line);

            br.close();
            /* end debug code */

            /* used to notify the MainActivity about activity changes
             * in order to update the currentActivity TextView */
            /*Intent mainIntent = new Intent();
            mainIntent.putExtra("most_probable", activityType);
            mainIntent.putExtra("confidence", confidence);
            mainIntent.setAction(Constants.BROADCAST_ACTIVITY_UPDATE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(mainIntent);

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

        Log.e(TAG, "---------------------end measurement---------------------");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}

