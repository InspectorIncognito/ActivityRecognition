package api.activity.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import api.activity.activityrecognition.R;
import api.activity.activityrecognition.utils.Constants;
import api.activity.activityrecognition.utils.Functions;

public class DetectedActivitiesIntentService extends IntentService {

    private final String TAB = "\t";

    private long start;
    private String logName;
    private File logFile;

    protected static final String TAG = "DetectionService";

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        start = System.currentTimeMillis();
        logName = getString(R.string.activity_log_filename);
        logFile = new File(getFilesDir(), logName);

        /*try {
            if(!logFile.exists() || !logFile.isFile())
                logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "--------------------begin measurement--------------------");
        int activityType = 0;
        int confidence = 0;

        try {
            StringBuilder sb = new StringBuilder();

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbableActivity
                    = result.getMostProbableActivity();

            confidence = mostProbableActivity.getConfidence();
            activityType = mostProbableActivity.getType();

            /*Log.e(TAG, String.format(
                    "most probable is %s with %d%% confidence",
                    Functions.getActivityString(this, activityType),
                    confidence));*/

            ArrayList<DetectedActivity> detectedActivities = new ArrayList<>();
            for(DetectedActivity d : result.getProbableActivities()){
                //Log.e(TAG, d.toString());
                detectedActivities.add(d);
            }

            /* FORMAT:
             day/month/year hour:minutes:seconds \t activity1 confidence1 \t activity2= ...... \n */

            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            sb.append(currentDateTimeString);
            sb.append(Constants.TAB);

            for (DetectedActivity da : detectedActivities) {
                sb.append(Functions.getActivityString(this, da.getType()));
                sb.append(Constants.HYPHEN);
                sb.append(da.getConfidence());
                sb.append(Constants.TAB);
            }

            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(getFilesDir() + File.separator + logName, true));

            bw.write(sb.toString().trim());
            bw.newLine();

            /*bw.write(String.format(
                    "Most probable function returns %s with a %d confidence percentage",
                    Functions.getActivityString(this, activityType),
                    confidence));*/

            bw.close();

            BufferedReader br = new BufferedReader(new FileReader(new File(getFilesDir() + File.separator + logName)));
            String line = "";

            while((line = br.readLine()) != null)
                Log.e(TAG, line);

            br.close();

            Intent mainIntent = new Intent();
            mainIntent.putExtra("most_probable", activityType);
            mainIntent.putExtra("confidence", confidence);
            mainIntent.setAction(Constants.BROADCAST_ACTIVITY_UPDATE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(mainIntent);


            long currentTime = System.currentTimeMillis();

            if(currentTime - start > Constants.SEND_TO_SERVER_INTERVAL){
                startService(new Intent(this, FileSendingService.class));
                start = currentTime;
            }

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

