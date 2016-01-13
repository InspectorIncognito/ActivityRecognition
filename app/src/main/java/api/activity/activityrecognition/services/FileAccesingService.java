package api.activity.activityrecognition.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import api.activity.activityrecognition.R;

/**
 * Created by brahim on 13-01-16.
 */
public class FileAccesingService extends Service{

    private final String TAG = "FileAccesingService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
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

            BufferedReader br = new BufferedReader(new FileReader(
                    new File(getFilesDir() + File.separator + getString(R.string.activity_log_filename))));
            String line = "";

            while((line = br.readLine()) != null)
                Log.e(TAG, line);

            br.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
