 /*
  * Created by Mike Rehner on 2/12/19.
  * Copyright (C) 2019 Mike Rehner
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package edu.babarehner.android.symtrax;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_DATE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_OBSERVATION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_OUTCOME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_SEVERITY;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_SYMPTOM;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_TIME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_TRIGGER;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.SYM_TRAX_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema._IDST;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOM_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema._IDS;


 public class AddEditSymTraxActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = AddEditSymTraxActivity.class.getSimpleName();

    public static final int LOADER_SYMTRAX = 2;
    public static final int LOADER_SYMPTOMS = 3;

    private Uri mCurrentRecordUri;
    private Uri mCurrentSymptomUri;

    static final int SYM_TRAX_LOADER = 1;
    static final int SYMPtOM_LOADER = 2;

    // load up the Severity Spinner
    public static final CharSequence[] SEVERITY = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    // load up the Emotion Soinner
    public static final CharSequence[] EMOTIONS = {"None","Anger", "Disgust", "Envy", "Fear (Anxiety)",
            "Happiness", "Jealousy", "Love", "Sadness", "Shame", "Guilt"};

    private Button mPickDate, mPickTime;
    private EditText mEditDate, mEditTime;
    private Spinner mSpinSymptom;
    private Spinner mSpinSeverity;
    private EditText mEditTrigger;
    private Spinner mSpinEmotion;
    private EditText mEditObservation;
    private EditText mEditOutcome;

    // see if I can change this to private??
    public SimpleCursorAdapter mSpinSymptomAdapter;
    private String mSpinSymptomVal; // holds the value of the spinner symptom
    private String[] mSpinVal = {"",""}; // initialization for Serverity, Emotion Spinner Values

    private boolean mRecordChanged = false;

     // Touch listener to check if changes made to a record
     private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event){
             mRecordChanged = true;
             return false;
         }
     };

     private ShareActionProvider mShareActionProvider;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_symtrax);

        // get intent and get data from intent
        Intent intent = getIntent();
        mCurrentRecordUri = intent.getData();

        // intitialize this loader first to load spinner-
        getLoaderManager().initLoader(LOADER_SYMPTOMS, null, AddEditSymTraxActivity.this);

        // if the intent does not contain a single item URI FAB has been cicked
        if (mCurrentRecordUri == null) {
            // set pager header to add record
            setTitle(getString(R.string.add_edit_sym_trac_activity_title_add_record));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.add_edit_sym_trac_activity_title_edit_record));
            getLoaderManager().initLoader(LOADER_SYMTRAX, null, AddEditSymTraxActivity.this);
        }

        // grab the views
        mPickDate = findViewById(R.id.pick_date);
        mPickTime = findViewById(R.id.pick_time);
        mEditDate = findViewById(R.id.et_date);
        mEditTime = findViewById(R.id.et_time);
        mSpinSymptom = findViewById(R.id.sp_symptom);
        mSpinSeverity = findViewById(R.id.sp_severity);
        mEditTrigger = findViewById(R.id.et_trigger);
        mSpinEmotion = findViewById(R.id.sp_Emotion);
        mEditObservation = findViewById(R.id.et_observation);
        mEditOutcome = findViewById(R.id.et_outcome);

        // Set up Touch Listener on all the input views to see if user touch the view
        mEditDate.setOnTouchListener(mTouchListener);
        mEditTime.setOnTouchListener(mTouchListener);
        mSpinSymptom.setOnTouchListener(mTouchListener);
        mSpinSeverity.setOnTouchListener(mTouchListener);
        mEditTrigger.setOnTouchListener(mTouchListener);
        mSpinEmotion.setOnTouchListener(mTouchListener);
        mEditObservation.setOnTouchListener(mTouchListener);
        mEditOutcome.setOnTouchListener(mTouchListener);

        // Load the symptom spinner
        mSpinSymptom = findViewById(R.id.sp_symptom);

        // Feels like I have to put the following lines in to get correct initialization of
        // mSpinSymptomAdapter- threading problem.
        Cursor c = getContentResolver().query(
                SYMPTOM_URI,
                null,
                null,
                null,
                SymTraxContract.SymptomTableSchema.C_SYMPTOM + " ASC");
        c.close();

        // Having real problem with spinner, above line stops it in one
        // program but not the other
        mSpinSymptomAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                null,
                new String[]{SymTraxContract.SymptomTableSchema.C_SYMPTOM },
                new int[] {android.R.id.text1},
                0);

        mSpinSymptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinSymptom.setAdapter(mSpinSymptomAdapter);
        mSpinSymptom.setSelection(0, false);

        mSpinSymptom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // CursorWrapper required when working with CursorLoader & SQLite DB
                CursorWrapper cw = (CursorWrapper) parent.getItemAtPosition(pos);
                mSpinSymptomVal = String.valueOf(cw.getString(1));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // CursorWrapper cw = (CursorWrapper) parent.getItemAtPosition(0);
                // mSpinSymptomVal = String.valueOf(cw.getString(1));
            }
        });

        // load the rest of the spinners
        mSpinSeverity = getSpinnerVal(R.id.sp_severity, SEVERITY, 0);
        mSpinEmotion = getSpinnerVal(R.id.sp_Emotion, EMOTIONS, 1);

        //handle the time and date button clicks
        getDate();
        getTime();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle){
        Loader<Cursor> loader = null; // returns null if not either case

        switch (loaderID){
            case LOADER_SYMTRAX:
                String[] projectionSymTrax = {_IDST,
                        C_DATE,
                        C_TIME,
                        C_SYMPTOM,
                        C_SEVERITY,
                        C_TRIGGER,
                        C_EMOTION,
                        C_OBSERVATION,
                        C_OUTCOME};
                // new loader for new thread
                loader = new CursorLoader(this, mCurrentRecordUri, projectionSymTrax, null,
                        null, null);
                break;
            case LOADER_SYMPTOMS:
                String symptomSortOrder = SymTraxContract.SymptomTableSchema.C_SYMPTOM + " ASC";
                mCurrentSymptomUri = SYMPTOM_URI;
                String[] projectionSymptoms = {_IDS, SymTraxContract.SymptomTableSchema.C_SYMPTOM};
                loader = new CursorLoader(this, mCurrentSymptomUri, projectionSymptoms, null,
                        null, symptomSortOrder);
                break;
        }
        return loader;
    }


     @Override
     public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
         switch (loader.getId()) {
             case LOADER_SYMTRAX:
                 // move to the only row in the cursor
                 if (c.moveToFirst()){
                     int dateColIndex = c.getColumnIndex(C_DATE);
                     int timeColIndex = c.getColumnIndex(C_TIME);
                     int symptomColIndex = c.getColumnIndex(C_SYMPTOM);
                     int severityColIndex = c.getColumnIndex(C_SEVERITY);

                     String date = c.getString(dateColIndex);
                     String time = c.getString(timeColIndex);
                     String symptom = c.getString(symptomColIndex);
                     String severity = c.getString(severityColIndex);
                     String trigger = c.getString(c.getColumnIndex(C_TRIGGER));
                     String emotion = c.getString(c.getColumnIndex(C_EMOTION));
                     String observation = c.getString(c.getColumnIndex(C_OBSERVATION));
                     String outcome = c.getString(c.getColumnIndex(C_OUTCOME));

                     // CursorWrapper required when working with CursorLoader & SQLite DB
                     // CursorWrapper not required for raw queries
                     // CursorWrapper cw;
                     int pos = 0;
                     for (int i = 0; i < mSpinSymptom.getCount(); i++){
                         CursorWrapper cw = (CursorWrapper) mSpinSymptom.getItemAtPosition(i);
                         if (String.valueOf(cw.getString(1)).equals
                                 (symptom)){
                             pos = i;
                             break;
                         }
                     }

                     mSpinSymptom.setSelection(pos);

                     mEditDate.setText(date);
                     mEditTime.setText(time);
                     mEditTrigger.setText(trigger);
                     mEditObservation.setText(observation);
                     mEditOutcome.setText(outcome);
                 }
                 break;
             case LOADER_SYMPTOMS:
                 mSpinSymptomAdapter.swapCursor(c);
                 break;
         }
     }


     @Override
     public void onLoaderReset(Loader<Cursor> loader) {
         // If invalid Loader clear data from input field
         switch (loader.getId()) {
             case LOADER_SYMTRAX:
                 mEditDate.setText("");
                 mEditTime.setText("");
                 mEditTrigger.setText("");
                 mEditObservation.setText("");
                 mEditOutcome.setText("");

                 break;
             case LOADER_SYMPTOMS:
                 mSpinSymptomAdapter.swapCursor(null);
                 break;
         }
     }


     @Override   // set up the menu the first time
     public boolean onCreateOptionsMenu(Menu m) {
         getMenuInflater().inflate(R.menu.menu_add_edit_symtrax_activity, m);

         // relate mShareActionProvider to share e-mail menu item
         // initialize mShareActionProvider
         mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider
                 (m.findItem(R.id.action_share_email));

         return true;
     }


     @Override   // hide delete/share menu items when adding a new exercise
     public boolean onPrepareOptionsMenu(Menu m) {
         super.onPrepareOptionsMenu(m);
         // if this is add an exercise, hide "delete" menu item

         if (mCurrentRecordUri == null) {
             MenuItem deleteItem = m.findItem(R.id.action_delete);
             deleteItem.setVisible(false);
             MenuItem shareEMailItem = m.findItem(R.id.action_share_email);
             shareEMailItem.setVisible(false);
             MenuItem shareTextItem = m.findItem(R.id.action_share_text);
             shareTextItem.setVisible(false);
         }
         return true;
     }


     @Override        // Select from the options menu
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case R.id.action_save:
                 saveRecord();
                 finish();       // exit activity
                 return true;
             case R.id.action_share_email:
                 if (mShareActionProvider != null) {
                     // returns an intent
                     //TODO mShareActionProvider.setShareIntent(shareData(SHARE_EMAIL));
                 }
                 // Intent.createChooser(i, " Create Chooser");
                 Log.v(LOG_TAG, "in action share EMail after String Builder");
                 return true;
             case R.id.action_share_text:
                 if (mShareActionProvider != null){
                     // TODO mShareActionProvider.setShareIntent(shareData(SHARE_TEXT));
                 }
                 return true;
             case R.id.action_delete:
                 // Alert Dialog for deleting one record
                 showDeleteConfirmationDialog();
                 return true;
             // this is the <- button on the header
             case android.R.id.home:
                 // record has not changed
                 if (!mRecordChanged) {
                     NavUtils.navigateUpFromSameTask(AddEditSymTraxActivity.this);
                     return true;
                 }
                 // set up dialog to warn user about unsaved changes
                 DialogInterface.OnClickListener discardButtonClickListener =
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int i) {
                                 //user click discard. Navigate up to parent activity
                                 NavUtils.navigateUpFromSameTask(AddEditSymTraxActivity.this);
                             }
                         };
                 // show user they have unsaved changes
                 //TODO showUnsavedChangesDialog(discardButtonClickListener);
                 return true;
         }
         return super.onOptionsItemSelected(item);
     }


     private void saveRecord() {

         // read from EditText and Spinner input fields
         String date = mEditDate.getText().toString();
         String time = mEditTime.getText().toString();
         String symptom  = mSpinSymptomVal;
         String severity = mSpinVal[0];
         String trigger = mEditTrigger.getText().toString().trim();
         String emotion = mSpinVal[1];
         String observation = mEditObservation.getText().toString().trim();
         String outcome = mEditOutcome.getText().toString().trim();



         ContentValues values = new ContentValues();
         values.put(C_DATE, date);
         values.put(C_TIME, time);
         values.put(SymTraxContract.SymTraxTableSchema.C_SYMPTOM, symptom);
         values.put(C_SEVERITY, severity);
         values.put(C_TRIGGER, trigger);
         values.put(C_EMOTION, emotion);
         values.put(C_OBSERVATION, observation);
         values.put(C_OUTCOME, outcome);

         if (mCurrentRecordUri == null) {
             // a new machine
             // ***********
             Log.v(LOG_TAG, "in saveRecord " + SYM_TRAX_URI.toString() + "\n" + values);

             Uri newUri = getContentResolver().insert(SYM_TRAX_URI, values);
             // ************

             if (newUri == null) {
                 Toast.makeText(this, getString(R.string.insert_record_failed),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.insert_record_succeeded),
                         Toast.LENGTH_SHORT).show();
             }
         } else {
             // existing record so update with content URI and pass in ContentValues
             int rowsAffected = getContentResolver().update(mCurrentRecordUri, values, null, null);
             if (rowsAffected == 0) {
                 // TODO Check db- Text Not Null does not seem to be working or entering todo from PartsRunner
                 // "" does not mean NOT Null- there must be an error message closer to the db!!!
                 Toast.makeText(this, getString(R.string.edit_update_record_failed),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.edit_update_record_success),
                         Toast.LENGTH_SHORT).show();
             }
         }
     }


     // delete Record from DB
     private void deleteRecord(){
         if (mCurrentRecordUri != null) {
             int rowsDeleted = getContentResolver().delete(mCurrentRecordUri, null, null);
             if (rowsDeleted == 0) {
                 Toast.makeText(this, getString(R.string.delete_record_failure),
                         Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, getString(R.string.delete_record_success),
                         Toast.LENGTH_SHORT).show();
             }
         }
         finish();
     }


     private void showDeleteConfirmationDialog() {
         // Create an AlertDialog.Builder, set message and click
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


     // Load spinners with values from an array and set up Listener
    private Spinner getSpinnerVal(int resourceId, final CharSequence[] cs, final int i){
        Spinner sp = findViewById(resourceId);
        ArrayAdapter<CharSequence> csAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cs);
        // Specify the layout to use when the list of choices appear
        csAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(csAdapter); // apply the adapter to the spinner
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                Log.v("RecordActivity", selection);
                mSpinVal[i] = selection;  //tore the selection for future reference
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // selection = cs[0].toString();
            }
        });
        return sp;
    }


    private void getDate(){
        mPickDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (mCurrentRecordUri !=null){
                    mRecordChanged = true;
                }
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
    }


    private void getTime(){
        mPickTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (mCurrentRecordUri !=null){
                    mRecordChanged = true;
                }
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getSupportFragmentManager(), "time picker");
            }
        });
    }





}
