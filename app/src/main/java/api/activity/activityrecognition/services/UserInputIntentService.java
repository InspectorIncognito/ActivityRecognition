package api.activity.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

/**
 * Created by brahim on 13-01-16.
 */
public class UserInputIntentService extends IntentService {

    private static final String TAG = "UserInput";

    public UserInputIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String activity = "";
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            if(bundle.containsKey("activity"))
                activity = bundle.getString("activity");
            else
                activity = getString(R.string.activity_log_error);
        }

        StringBuilder sb = new StringBuilder();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        sb.append(currentDateTimeString);
        sb.append(Constants.TAB);
        sb.append(activity);

        try {
            BufferedWriter bw = new BufferedWriter( new FileWriter(
                    getFilesDir() + File.separator + getString(R.string.activity_log_filename), true));

            bw.write(sb.toString());
            bw.newLine();

            bw.close();


            BufferedReader br = new BufferedReader(new FileReader(
                    new File(getFilesDir() + File.separator + getString(R.string.activity_log_filename))));
            String line = "";

            while((line = br.readLine()) != null)
                Log.e(TAG, line);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
