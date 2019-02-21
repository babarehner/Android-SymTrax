package edu.babarehner.android.symtrax;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;


public class DialogFragDeleteConfirmation extends DialogFragment {

    public DialogFragDeleteConfirmation() {}

    public DialogDeleteListener callBack;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setMessage(R.string.delete_dialog_msg);
        b.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.onDeleteClick();
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



   @Override
    public void onAttach(Context context) {
       super.onAttach(context);

       // Activity a;
       // if(context instanceof Activity){
       //     a = (Activity) context;

       // Verify that the host activity implements the callback interface
       try {
           // Instantiate the NoticeDialogListener so we can send events to the host
           callBack = (DialogDeleteListener) context;
       } catch (ClassCastException e) {
           // The activity doesn't implement the interface, throw exception
           throw new ClassCastException(context.toString()
                   + " must implement DialogClickListener");
       }
   }

   public interface DialogDeleteListener {
        void onDeleteClick();
   }
}
