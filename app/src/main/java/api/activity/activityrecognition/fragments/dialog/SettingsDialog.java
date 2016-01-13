package api.activity.activityrecognition.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import api.activity.activityrecognition.services.DetectionService;
import api.activity.activityrecognition.utils.Constants;

/**
 * Created by brahim on 06-01-16.
 * Dialog used to change the time interval between activity measurements.
 */
public class SettingsDialog extends DialogFragment {

    private static ArrayList<Integer> intervals;
    private static Context creatorContext;

    public static SettingsDialog newInstance(int currentlySelected, Context context) {
        SettingsDialog sd = new SettingsDialog();

        Bundle args = new Bundle();
        intervals = Constants.MEASUREMENT_INTERVALS_MILLIS;;
        args.putInt("currentlySelected", currentlySelected);
        creatorContext = context;
        sd.setArguments(args);

        return sd;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = this.getArguments();
        //final ArrayList<Integer> intervals = args.getIntegerArrayList("intervals");
        int currentlySelected = args.getInt("currentlySelected");
        String measurementUnit = Constants.SECOND_SINGULAR_STRING;

        CharSequence[] items = new CharSequence[intervals.size()];

        for(int i = 0; i < intervals.size(); i++){

            long value = intervals.get(i);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(value);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(value) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(value));
            double retValue = 0;

            if(minutes >= 1){
                retValue = minutes + ((double)seconds / 6) / 10;

                measurementUnit = retValue > 1 ?
                        Constants.MINUTE_PLURAL_STRING
                        : Constants.MINUTE_SINGULAR_STRING;
            }
            else{
                retValue = seconds;

                measurementUnit = retValue > 1 ?
                        Constants.SECOND_PLURAL_STRING
                        : Constants.SECOND_SINGULAR_STRING;
            }

            NumberFormat nf = new DecimalFormat("##.#");
            nf.format(retValue);
            items[i] = nf.format(retValue) + " " + measurementUnit;
        }

        /*prompts the user to select the time interval used to
         take measurements in the background process*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("")
                .setSingleChoiceItems(items, currentlySelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog)dialog).getListView()
                                .getCheckedItemPosition();
                        long interval = intervals.get(selectedPosition);
                        Intent intent = new Intent(creatorContext, DetectionService.class);
                        intent.putExtra("interval", interval);
                        creatorContext.startService(intent);
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
