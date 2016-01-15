package api.activity.activityrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;

import api.activity.activityrecognition.email.AutomaticEmailSender;
import api.activity.activityrecognition.services.DetectionService;
import api.activity.activityrecognition.services.UserInputIntentService;
import api.activity.activityrecognition.utils.Functions;

public class MainActivity extends AppCompatActivity {

    //private int selectedInterval;
    //private UpdateReceiver updateReceiver;
    private boolean wasLogSent;
    private Intent measurementService;
    private Button saveButton;
    private Button sendByEmailButton;
    private Button exitButton;
    private ImageButton settingsButton;
    private TextView activityText;
    private EditText customActivityText;
    private RadioButton otherRadioButton;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wasLogSent = false;

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

        /* BUTTON NOT USED ANYMORE
         * NOT REMOVED JUST IN CASE */
        settingsButton.setEnabled(false);
        settingsButton.setFocusable(false);
        settingsButton.setVisibility(View.GONE);

        /* disabled temporarily for screen space issues */
        activityText = (TextView) findViewById(R.id.currentActivityTextView);
        activityText.setEnabled(false);
        activityText.setFocusable(false);
        activityText.setVisibility(View.GONE);

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

                switch(selected){
                    case R.id.otherRadioButton:
                        activity = customActivityText.getText().toString();
                        customActivityText.setText("");
                        break;
                    case R.id.stillRadioButton:
                        activity = "still";
                        break;
                    case R.id.walkingRadioButton:
                        activity = "walking";
                        break;
                    case R.id.gettingOnVehicleRadioButton:
                        activity = "getting on vehicle";
                        break;
                    case R.id.onVehicleRadioButton:
                        activity = "on vehicle";
                        break;
                    case R.id.gettingOffVehicleRadioButton:
                        activity = "getting off vehicle";
                }

                Intent userIntent = new Intent(MainActivity.this, UserInputIntentService.class);
                logText = String.format(getString(R.string.activity_log_base_text), activity);
                userIntent.putExtra("activity", logText);
                startService(userIntent);
            }
        });

        /* default text for activity textview */
        changeText(4);

        /* starts the detection service */
        measurementService = new Intent(this, DetectionService.class);
        startService(measurementService);

        /* updates the UI whenever an activity change occurs
        * NOT USED FOR NOW*/
        /*updateReceiver = new UpdateReceiver(this);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(updateReceiver, new IntentFilter(Constants.BROADCAST_ACTIVITY_UPDATE));*/
    }

    /* broadcast receiver used to receive notifications about activity changes detected
    * NOT USED FOR NOW*/
    /*private static class UpdateReceiver extends BroadcastReceiver {

        private MainActivity mainActivity;

        public UpdateReceiver(MainActivity activity){
            mainActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int confidence = bundle.getInt("confidence");
            int activityType = bundle.getInt("most_probable");
            mainActivity.changeText(activityType);
        }
    }*/

    /* sends the remaining log file when the app terminates */
    private void cleanUp(){
        File f = new File(getFilesDir() + File.separator + getString(R.string.activity_log_filename));

        if(wasLogSent && f.exists() && f.length() != 0){
            f.delete();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        //stopService(measurementService);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        //stopService(measurementService);
        //cleanUp();
    }

    /* changes the text of the activityText TextView to reflect changes in activity */
    public void changeText(int activityType){
        activityText.setText(getString(R.string.text_activity_display,
                Functions.getActivityStringInSpanish(this, activityType)));
    }
}
