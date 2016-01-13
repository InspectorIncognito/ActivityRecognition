package api.activity.activityrecognition.server;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import api.activity.activityrecognition.R;

/**
 * Created by brahim on 12-01-16.
 */
public class HttpRequest {

    private String defaultURL = "http://127.0.0.1:8000/android/receiveFile/";

    private String url;
    private String filename;
    private File path;
    private ArrayList<Object> data;

    public HttpRequest(Context context, String filename, File path){
        this.filename = filename;
        this.path = path;
        url = context.getString(R.string.server_address)
                .concat(context.getString(R.string.server_file_destination));
        data = new ArrayList<>();
    }

    public String getURL(){
        return url;
    }

    public void addParam(Object param){
        data.add(param);
    }

    public String getFilename(){
        return filename;
    }

    public File getPath(){
        return path;
    }
}
