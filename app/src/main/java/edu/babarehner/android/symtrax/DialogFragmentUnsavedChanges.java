package edu.babarehner.android.symtrax;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;



public class DialogFragmentUnsavedChanges extends DialogFragment {

    public DialogClickListener callBack;

    public DialogFragmentUnsavedChanges() { } // constructor

/*  Deprecated Activity- Use Context
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            callBack = (DialogClickListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogClickListener");
        }
    }
*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activity a;
        // if(context instanceof Activity){
        //     a = (Activity) context;

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            callBack = (DialogClickListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement DialogClickListener");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setMessage(R.string.unsaved_changes_dialog_msg);
        b.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // callBack.onDiscardClick(DialogFragmentUnsavedChanges.this);
                callBack.onDiscardClick();
            }
        });
        b.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        return b.create();
    }


    interface DialogClickListener {
        // the two buttons on DialogFragementUnsavedChangesDialog
        void onDiscardClick();
        // void onCancelClick();
    }

}



