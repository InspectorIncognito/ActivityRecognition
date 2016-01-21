package api.activity.activityrecognition.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import api.activity.activityrecognition.services.FileAccessingIntentService;

/**
 * Created by brahim on 21-01-16.
 *
 * Broadcast receiver used to dismiss the "Getting on vehicle" notifications once the user
 * selects the appropriate action
 */
public class NotificationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(notificationId);

        Intent closeTrayIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeTrayIntent);

        Intent fileIntent = new Intent(context, FileAccessingIntentService.class);
        fileIntent.putExtra("textToLog", intent.getStringExtra("textToLog"));
        context.startService(fileIntent);
    }
}
