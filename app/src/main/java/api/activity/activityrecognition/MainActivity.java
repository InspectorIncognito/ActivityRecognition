package api.activity.activityrecognition;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;

import api.activity.activityrecognition.email.AutomaticEmailSender;
import api.activity.activityrecognition.receivers.HeuristicsReceiver;
import api.activity.activityrecognition.receivers.NotificationReceiver;
import api.activity.activityrecognition.services.DetectionService;
import api.activity.activityrecognition.services.UserInputIntentService;
import api.activity.activityrecognition.utils.Constants;
import api.activity.activityrecognition.utils.Functions;

public class MainActivity extends AppCompatActivity {

    //private int selectedInterval;
    private boolean wasLogSent;
    private int notificationId;
    private Button saveButton;
    private Button sendByEmailButton;
    private Button exitButton;
    private ImageButton settingsButton;
    private TextView activityText;
    private TextView accuracyText;
    private TextView speedText;
    private EditText customActivityText;
    private RadioButton otherRadioButton;
    private RadioGroup radioGroup;
    private Intent measurementService;
    private UpdateReceiver updateReceiver;
    private HeuristicsReceiver heuristicsReceiver;
    public NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wasLogSent = false;
        notificationId = 0;

        //selectedInterval = -1;

        /* opens the settings dialog */
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        /*settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsDialog.newInstance(selectedInterval, MainActivity.this)
                        .show(getFragmentManager(), "settings");
            }
        });*/
        /*settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(getFilesDir() + File.separator + getString(R.string.activity_log_filename));
                f.delete();
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/

        /* BUTTON NOT USED ANYMORE
         * NOT REMOVED JUST IN CASE */
        //settingsButton.setEnabled(false);
        //settingsButton.setFocusable(false);
        //settingsButton.setVisibility(View.GONE);

        /* disabled temporarily for screen space issues */
        activityText = (TextView) findViewById(R.id.currentActivityTextView);
        accuracyText = (TextView) findViewById(R.id.currentAccuracyTextView);
        speedText = (TextView) findViewById(R.id.currentSpeedTextView);
        //activityText.setEnabled(false);
        //activityText.setFocusable(false);
        //activityText.setVisibility(View.GONE);

        /* closes the application, killing its background processes */
        exitButton = (Button) findViewById(R.id.exitAppButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(measurementService);
                cleanUp();
                finish();
            }
        });

        /* textfield for custom activity input */
        customActivityText = (EditText) findViewById(R.id.customActivityText);
        customActivityText.setEnabled(false);
        customActivityText.setFocusable(false);

        /* radiobutton listener for custom activity */
        otherRadioButton = (RadioButton) findViewById(R.id.otherRadioButton);
        otherRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customActivityText = (EditText) findViewById(R.id.customActivityText);
                customActivityText.setEnabled(true);
                customActivityText.setFocusable(true);
                /*this part has a bug, because disabling the EditText will cause it to
                lose the softkeyboard binding
                it's solved by calling the function below */
                customActivityText.setFocusableInTouchMode(true);
            }
        });

        /* radiogroup listener for fixed activities */
        radioGroup = (RadioGroup) findViewById(R.id.activityRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() != R.id.otherRadioButton) {
                    customActivityText = (EditText) findViewById(R.id.customActivityText);
                    customActivityText.setEnabled(false);
                    customActivityText.setFocusable(false);
                }
            }
        });

        /* send the activity log file to an email */
        sendByEmailButton = (Button) findViewById(R.id.sendByEmailButton);
        sendByEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutomaticEmailSender.sendEmail(
                        MainActivity.this,
                        getString(R.string.email_sender_address),
                        getString(R.string.email_password),
                        false
                );
                wasLogSent = true;
            }
        });

        /* saves the user input regarding current activity */
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            private String activity;
            private String logText;

            @Override
            public void onClick(View v) {
                int selected = radioGroup.getCheckedRadioButtonId();

                switch (selected) {
                    case R.id.otherRadioButton:
                        activity = customActivityText.getText().toString();
                        customActivityText.setText("");
                        break;
                    case R.id.stillRadioButton:
                        activity = "Still";
                        break;
                    case R.id.walkingRadioButton:
                        activity = "Walking";
                        break;
                    case R.id.gettingOnVehicleRadioButton:
                        activity = "Getting on vehicle";
                        break;
                    case R.id.onVehicleRadioButton:
                        activity = "In a vehicle";
                        break;
                    case R.id.gettingOffVehicleRadioButton:
                        activity = "Getting off vehicle";
                }

                Intent userIntent = new Intent(MainActivity.this, UserInputIntentService.class);
                //logText = String.format(getString(R.string.activity_log_base_text), activity);
                //userIntent.putExtra("activity", logText);
                userIntent.putExtra("activity", activity);
                startService(userIntent);
            }
        });

        /* default text for activity textview */
        changeText(activityText, 4);
        accuracyText.setText(getString(R.string.text_activity_accuracy_short, 0));
        speedText.setText(getString(R.string.text_activity_speed_short, 0));

        /* starts the detection service */
        measurementService = new Intent(this, DetectionService.class);
        startService(measurementService);

        /* updates the UI whenever an activity change occurs
        * NOT USED FOR NOW*/
        updateReceiver = new UpdateReceiver(this);
        IntentFilter updateFilter = new IntentFilter();
        updateFilter.addAction(Constants.BROADCAST_GUI_ACTIVITY_UPDATE);
        updateFilter.addAction(Constants.BROADCAST_GUI_LOCATION_UPDATE);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(updateReceiver, updateFilter);

        /* broadcast receiver used to get updates on activity and location changes */
        heuristicsReceiver = new HeuristicsReceiver(this);
        IntentFilter heuristicsFilter = new IntentFilter();
        heuristicsFilter.addAction(Constants.BROADCAST_LOCATION_UPDATE);
        heuristicsFilter.addAction(Constants.BROADCAST_ACTIVITY_UPDATE);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(heuristicsReceiver, heuristicsFilter);

        /* notification sent when the app detects a possible change of state from
        being on foot to getting on a moving vehicle
         */
        Intent affirmativeNotificationIntent = new Intent(this, NotificationReceiver.class);
        affirmativeNotificationIntent.putExtra("textToLog", getString(R.string.notification_affirmative_answer));
        affirmativeNotificationIntent.putExtra("notificationId", notificationId);

        Intent negativeNotificationIntent = new Intent(this, NotificationReceiver.class);
        negativeNotificationIntent.putExtra("textToLog", getString(R.string.notification_negative_answer));
        negativeNotificationIntent.putExtra("notificationId", notificationId);

        PendingIntent affirmativeNotificationPendingIntent =
                PendingIntent.getBroadcast(this, 0, affirmativeNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent negativeNotificationPendingIntent =
                PendingIntent.getBroadcast(this, 1, negativeNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_transapp)
                .setContentTitle(getString(R.string.notification_title_ES))
                .setContentText(getString(R.string.info_getting_on_bus_ES))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .addAction(R.mipmap.ic_true, getString(R.string.notification_affirmative_ES), affirmativeNotificationPendingIntent)
                .addAction(R.mipmap.ic_false, getString(R.string.notification_negative_ES), negativeNotificationPendingIntent);
    }

    /* broadcast receiver used to receive notifications about activity changes detected
    * NOT USED FOR NOW*/
    private static class UpdateReceiver extends BroadcastReceiver {

        private MainActivity mainActivity;

        public UpdateReceiver(MainActivity activity){
            mainActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            if(intent.getAction().equals(Constants.BROADCAST_GUI_ACTIVITY_UPDATE)) {
                int activityType = bundle.getInt("most_probable");
                int confidence = bundle.getInt("confidence");
                mainActivity.changeText(mainActivity.activityText, activityType);
                mainActivity.accuracyText.setText(
                        mainActivity.getString(R.string.text_activity_accuracy_short, confidence)
                );
            }

            if(intent.getAction().equals(Constants.BROADCAST_GUI_LOCATION_UPDATE)){
                double speed = bundle.getDouble("speed");
                mainActivity.speedText.setText(
                        mainActivity.getString(R.string.text_activity_speed_short, Math.round(speed))
                );
            }
        }
    }

    /* sends the remaining log file when the app terminates */
    private void cleanUp(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(heuristicsReceiver);

        File f = new File(getFilesDir() + File.separator + getString(R.string.log_filename));

        if(wasLogSent && f.exists() && f.length() != 0){
            f.delete();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(heuristicsReceiver);
        //stopService(measurementService);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(heuristicsReceiver);
        //stopService(measurementService);
        //cleanUp();
    }

    /* changes the text of the activityText TextView to reflect changes in activity */
    public void changeText(TextView tv, int value){
        tv.setText(getString(R.string.text_activity_display_short,
                Functions.getActivityStringInSpanish(this, value)));
    }
}
