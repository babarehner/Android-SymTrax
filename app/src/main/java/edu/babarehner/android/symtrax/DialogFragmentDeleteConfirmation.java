package edu.babarehner.android.symtrax;

import android.app.Dialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;


public class DialogFragmentDeleteConfirmation extends DialogFragment {

    public DialogFragmentDeleteConfirmation() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setMessage(R.string.delete_dialog_msg);
        b.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddEditSymTraxActivity activity = (AddEditSymTraxActivity) getActivity();
                activity.deleteRecord();
            }
        });
        b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) { dialog.dismiss();}
            }
        });

        // Create the AlertDialog object and return it
        return b.create();
    }
}
