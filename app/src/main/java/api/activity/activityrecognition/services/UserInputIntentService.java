package api.activity.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import java.text.DateFormat;
import java.util.Date;

import api.activity.activityrecognition.R;
import api.activity.activityrecognition.utils.Constants;

/**
 * Created by brahim on 13-01-16.
 *
 * IntentService used to receive and handle the user input, and send it to the file writing service
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

        Intent fileIntent = new Intent(this, FileAccessingIntentService.class);
        fileIntent.putExtra("textToLog", sb.toString().trim());
        startService(fileIntent);
    }
}
