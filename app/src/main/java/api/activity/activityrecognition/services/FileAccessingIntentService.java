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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import api.activity.activityrecognition.R;
import api.activity.activityrecognition.utils.Constants;

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
                textToLog = getString(R.string.log_error);
        }

        try {

            String line = "";

            BufferedWriter bw = new BufferedWriter(new FileWriter(
                    getFilesDir() + File.separator + getString(R.string.log_filename), true));

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);

            line += dateFormat.format(new Date());
            line += Constants.TAB;
            line += textToLog;

            bw.write(line);
            bw.newLine();

            bw.close();

            Log.d(TAG, line);

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
