package api.activity.activityrecognition.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by brahim on 12-01-16.
 */
public class Server {

    public static Server server = null;
    private final String TAG = "Server";

    private Context context;

    private Server(){}

    public static Server getInstance(Context context){
        if(server == null) {
            server = new Server();
            server.context = context;
        }

        return server;
    }

    public void sendHttpRequest(final HttpRequest request){
        class ServerAsyncTask extends AsyncTask<Void, Integer, String> {

            @Override
            protected String doInBackground(Void... params) {

                String boundary = "*****";
                String crlf = "\r\n";
                String twoHyphens = "--";

                try{
                    ConnectivityManager connManager =
                            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

                    if(activeNetwork == null) {
                        Log.e(TAG, "No network found");
                        return null;
                    }

                    /*use only WiFi*/
                    if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {
                        Log.e(TAG, "WiFi is not turned on");
                        return null;
                    }

                    URL url = new URL(request.getURL());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    File file = new File(request.getPath() + File.separator + request.getFilename());

                    int responseCode = conn.getResponseCode();
                    if(responseCode != 200){
                        Log.e(TAG, String.format("Connection failed with status: %d", responseCode));
                        return null;
                    }

                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty(
                            "Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream outputStream = new DataOutputStream(
                            conn.getOutputStream());

                    byte[] buf = new byte[8192];

                    InputStream fileStream = new FileInputStream(file);

                    outputStream.writeBytes(twoHyphens + boundary + crlf);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                            request.getFilename() + "\";filename=\"" +
                            request.getFilename() + ".txt" + "\"" + crlf);
                    outputStream.writeBytes(crlf);


                    //WRITE THE FILE HERE
                    //HOPE IT WORKS; ONLY NEEDS SERVER SIDE READING ROUTINE
                    int c = 0;

                    while ((c = fileStream.read(buf, 0, buf.length)) > 0) {
                        outputStream.write(buf, 0, c);
                        outputStream.flush();
                    }

                    outputStream.writeBytes(crlf);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

                    outputStream.flush();
                    outputStream.close();

                    conn.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        new ServerAsyncTask().execute();
    }
}
