package api.activity.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import api.activity.activityrecognition.R;

/**
 * Created by brahim on 13-01-16.
 *
 * IntentService used to access and write to the log file of this app
 */
public class FileAccessingIntentService extends IntentService {

    private static final String TAG = "FileAccessingService";

    public FileAccessingIntentService() {
        super(TAG);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String textToLog = "";
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            if(bundle.containsKey("textToLog"))
                textToLog = bundle.getString("textToLog");
            else
                textToLog = getString(R.string.activity_log_error);
        }

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(
                    getFilesDir() + File.separator + getString(R.string.activity_log_filename), true));

            bw.write(textToLog);
            bw.newLine();

            bw.close();

            Log.e(TAG, textToLog);


        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
