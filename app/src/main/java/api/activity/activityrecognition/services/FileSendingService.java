package api.activity.activityrecognition.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;

import api.activity.activityrecognition.server.HttpRequest;
import api.activity.activityrecognition.server.Server;

/**
 * Created by brahim on 12-01-16.
 */
public class FileSendingService extends Service{

    private static final String TAG = "FileSending";
    private String filename;
    private File path;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        filename = "ActivityLog";
        path = getFilesDir();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Server server = Server.getInstance(this);
        HttpRequest request = new HttpRequest(this, filename, path);
        server.sendHttpRequest(request);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
