package api.activity.activityrecognition;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import api.activity.activityrecognition.services.DetectionService;
import api.activity.activityrecognition.services.UserInputIntentService;
import api.activity.activityrecognition.utils.Constants;
import api.activity.activityrecognition.utils.Functions;

public class MainActivity extends AppCompatActivity {

    private int selectedInterval;
    private String logName;
    private File logFile;
    private UpdateReceiver updateReceiver;
    private Intent measurementService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedInterval = -1;

        /* opens the settings dialog */
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
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
        final TextView activityText = (TextView) findViewById(R.id.currentActivityTextView);
        activityText.setEnabled(false);
        activityText.setFocusable(false);
        activityText.setVisibility(View.GONE);

        /* starts the detection service */
        measurementService = new Intent(this, DetectionService.class);
        startService(measurementService);

        /* closes the application, killing its background processes */
        Button exitButton = (Button) findViewById(R.id.exitAppButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* textfield for custom activity input */
        EditText customActivityText = (EditText) findViewById(R.id.customActivityText);
        customActivityText.setEnabled(false);
        customActivityText.setFocusable(false);

        /* default text for activity textview */
        changeText(4);

        /* radiobutton listener for custom activity */
        RadioButton otherRadioButton = (RadioButton) findViewById(R.id.otherRadioButton);
        otherRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText customActivityText = (EditText) findViewById(R.id.customActivityText);
                customActivityText.setEnabled(true);
                customActivityText.setFocusable(true);
                /*this part has a bug.
                it's solved by calling the function below */
                customActivityText.setFocusableInTouchMode(true);
            }
        });

        /* radiogroup listener for fixed activities */
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.activityRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(group.getCheckedRadioButtonId() != R.id.otherRadioButton){
                    EditText customActivityText = (EditText) findViewById(R.id.customActivityText);
                    customActivityText.setEnabled(false);
                    customActivityText.setFocusable(false);
                }
            }
        });

        /* send the activity log file to an email */
        Button sendByEmailButton = (Button) findViewById(R.id.sendByEmailButton);
        sendByEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        /* saves the user input regarding current activity */
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            private String activity;
            private String logText;

            @Override
            public void onClick(View v) {
                EditText customActivityText = (EditText) findViewById(R.id.customActivityText);
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.activityRadioGroup);
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

        logName = getString(R.string.activity_log_filename);
        logFile = new File(getFilesDir(), logName);

        try {
            if(logFile.exists() && logFile.isFile()){
                logFile.delete();
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* updates the UI whenever an activity change occurs */
        updateReceiver = new UpdateReceiver(this);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(updateReceiver, new IntentFilter(Constants.BROADCAST_ACTIVITY_UPDATE));
    }

    /* broadcast receiver used to receive notifications about activity changes detected */
    private static class UpdateReceiver extends BroadcastReceiver {

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
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        stopService(measurementService);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        stopService(measurementService);
    }

    /* changes the text of the activityText TextView to reflect changes in activity */
    public void changeText(int activityType){
        TextView activityText = (TextView) findViewById(R.id.currentActivityTextView);
        activityText.setText(getString(R.string.text_activity_display,
                Functions.getActivityStringInSpanish(this, activityType)));
    }
}
