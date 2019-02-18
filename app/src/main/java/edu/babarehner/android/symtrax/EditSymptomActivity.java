package edu.babarehner.android.symtrax;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.SYM_TRAX_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema._IDST;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOM_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema._IDS;
import static edu.babarehner.android.symtrax.data.SymTraxProvider.LOG_TAG;


public class EditSymptomActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private Uri mCurrentSymptomUri;
    static final int EXISTING_SYMPTOM_LOADER = 4;

    private String mSymptom;
    private EditText mEditSymptomText;
    private boolean mEditSymptomChanged;

    // Touch listener to check if changes made to a record
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event){
            mEditSymptomChanged = true;
            v.performClick();
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_symptom);

        Intent intent = getIntent();
        mCurrentSymptomUri = intent.getData();

        if (mCurrentSymptomUri == null) {
            setTitle("Add an Equipment/Item Type");
        } else {
            setTitle("Edit Equipment/Item Type");
            getLoaderManager().initLoader(EXISTING_SYMPTOM_LOADER, null, EditSymptomActivity.this);
        }

        mEditSymptomText = findViewById(R.id.edit_symptoms);
        mEditSymptomText.setOnTouchListener(mTouchListener);

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {_IDS, SymTraxContract.SymptomTableSchema.C_SYMPTOM};

        return new CursorLoader(this, mCurrentSymptomUri, projection, null,
                null, null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        // move to the only row in the cursor
        if (c.moveToFirst()){
            int equpmentTypeColIndex = c.getColumnIndex(SymTraxContract.SymptomTableSchema.C_SYMPTOM);
            // use the index to pull the data out
            mSymptom = c.getString(equpmentTypeColIndex);
            // update the text view
            mEditSymptomText.setText(mSymptom);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // if invalid loader clear data from input field
        mEditSymptomText.setText("");
    }


    // Options menu automatically called from onCreate I believe
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_symptom_activity, menu);
        return true;
    }


    // Select from the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.action_save_symptom:
                saveRecord();
                finish();       // exit activity
                return true;
            case R.id.action_delete_symptom:
                // Alert Dialog for deleting one record;
                showDeleteConfirmationDialog();
                // deleteRecord();
                return true;
            // this is the <- button on the toolbar
            case android.R.id.home:
                // record has not changed
                if (!mEditSymptomChanged) {
                    NavUtils.navigateUpFromSameTask(EditSymptomActivity.this);
                    return true;
                }
                // set up dialog to warn user about unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                //user click discard. Navigate up to parent activity
                                NavUtils.navigateUpFromSameTask(EditSymptomActivity.this);
                            }
                        };
                // show user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // hide delete menu item when adding a new record
    @Override
    public boolean onPrepareOptionsMenu(Menu m) {
        super.onPrepareOptionsMenu(m);
        // if this is add a record, hide "delete" menu item
        if (mCurrentSymptomUri == null) {
            MenuItem deleteItem = m.findItem(R.id.action_delete_symptom);
            deleteItem.setVisible(false);
        }

        return true;
    }


    // Override the activity's normal back button. If record has changed create a
    // discard click listener that closed current activity.
    @Override
    public void onBackPressed() {
        if (!mEditSymptomChanged) {
            super.onBackPressed();
            return;
        }
        //otherwise if there are unsaved changes setup a dialog to warn the  user
        //handles the user confirming that changes should be made
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // user clicked "Discard" button, close the current activity
                        finish();
                    }
                };

        // show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void saveRecord() {
        String prevSymptom = mSymptom;  // from DB called by OnLoadFinished
        String newSymptom =  mEditSymptomText.getText().toString().trim();

        // if the date field is left blank do nothing
        if (mCurrentSymptomUri == null & TextUtils.isEmpty(newSymptom)) {
            Toast.makeText(this, getString(R.string.missing_symptom), Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(SymTraxContract.SymptomTableSchema.C_SYMPTOM, newSymptom);


        if (mCurrentSymptomUri == null) {     // New record
            // a new record
            Log.v(LOG_TAG, "in saveRecord " + SYMPTOM_URI.toString() + "\n" + values);
            Uri newUri = getContentResolver().insert(SYMPTOM_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.symtrax_provider_insert_record_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.symtrax_provider_insert_record_succeeded),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing record so update with content URI and pass in ContentValues
            int rowsAffected = getContentResolver().update(mCurrentSymptomUri, values, null, null);
            if (rowsAffected == 0) {
                // TODO Check db- Text Not Null does not seem to be working or entering
                // "" does not mean NOT Null- there must be an error message closer to the db!!!
                Toast.makeText(this, getString(R.string.edit_update_record_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.edit_update_record_success),
                        Toast.LENGTH_SHORT).show();
            }


            ContentValues valuesTSymTrax = new ContentValues();
            valuesTSymTrax.put(SymTraxContract.SymTraxTableSchema.C_SYMPTOM, newSymptom);

            int rows = getContentResolver().update(SYM_TRAX_URI,
                    valuesTSymTrax, "CSymptom = ?" , new String [] {prevSymptom});

            if (rows == 0) {
                Toast.makeText(this, "Machine_Table Update Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Machine_Table Update Succeeded", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // delete record from db
    private void deleteRecord(){
        if (mCurrentSymptomUri != null) {
            // pull out the string value we are trying to delete
            mEditSymptomText = findViewById(R.id.edit_symptoms);
            String deleteTry = mEditSymptomText.getText().toString();

            // set up the ContentProvider query
            String[] projection = {_IDST, SymTraxContract.SymTraxTableSchema.C_SYMPTOM};
            String selectionClause = SymTraxContract.SymTraxTableSchema.C_SYMPTOM  + " = ? ";
            String[] selectionArgs = {deleteTry};

            Cursor c = getContentResolver().query(
                    SYM_TRAX_URI,
                    projection,
                    selectionClause,
                    selectionArgs,
                    null );

            // if there are no instances of the deleteTry string in the Machines DB
            // Go ahead and try to delete. If deleteTry value is in CMachineType column do not delete (no cascade delete)
            if (!c.moveToFirst()){
                int rowsDeleted = getContentResolver().delete(mCurrentSymptomUri, null, null);
                if (rowsDeleted == 0) {
                    Toast.makeText(this, getString(R.string.delete_record_failure),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.delete_record_success),
                            Toast.LENGTH_SHORT).show();
                }
                c.close();
                finish();   // after deleting field value
            } else {
                c.close();
                showNoCascadeDeleteDialog();
            }
        }
    }


    private void showDeleteConfirmationDialog() {
        // Create and AlertDialog.Builder, set message and click
        // listeners for positive and negative buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked delete so delete
                deleteRecord();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked cancel, dismiss dialog, continue editing
                if (dialog != null) {dialog.dismiss();}
            }
        });
        // Create and show dialog
        AlertDialog alertD = builder.create();
        alertD.show();
    }


    private void showNoCascadeDeleteDialog(){
        final AlertDialog.Builder albldr = new AlertDialog.Builder(this);
        StringBuilder sb = new StringBuilder();
        sb.append("Unable to delete \"").append(mSymptom)
                .append(". In order to delete \"")
                .append(mSymptom)
                .append("\" all of the records in the SymTrax table that have an SYMPTOM TYPE of \"")
                .append(mSymptom)
                .append("\" must be changed to another SYMPTOM TYPE.")
                .append("\n\n OR \n\n")
                .append("You can delete all the SYMPTOMS that have an SYMPTOM TYPE of \"")
                .append(mSymptom)
                .append("\" in the SymTrax table.");

        albldr.setMessage(sb);
        albldr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // click on warning message
                finish();  // go back to previous activity when item not cascade deleted

            }
        });

        AlertDialog alertDialog = albldr.create();
        alertDialog.show();
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        // Create AlertDialogue.Builder amd set message and click listeners
        // for positive and negative buttons in dialogue.
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // user clicked the "keep eiditing" button. Dismiss dialog and keep editing
                if (dialog !=null) { dialog.dismiss();}
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
