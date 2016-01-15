package api.activity.activityrecognition.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import api.activity.activityrecognition.R;

/**
 * Created by brahim on 15-01-16.
 */
public class WarningDialog extends DialogFragment{

    public static WarningDialog newInstance(String msg) {
        WarningDialog wd = new WarningDialog();

        Bundle args = new Bundle();
        args.putString("message", msg);
        wd.setArguments(args);

        return wd;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = this.getArguments();

        builder.setTitle(R.string.warn_dialog_title_ES);
        builder.setMessage(args.getString("message"));
        builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}
