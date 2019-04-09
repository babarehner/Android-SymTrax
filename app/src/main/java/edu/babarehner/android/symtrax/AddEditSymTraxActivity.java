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
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_DATE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION2;
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


 public class AddEditSymTraxActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
         DialogFragmentUnsavedChanges.DialogClickListener, DialogFragDeleteConfirmation.DialogDeleteListener {

    private final String LOG_TAG = AddEditSymTraxActivity.class.getSimpleName();

    public static final int LOADER_SYMTRAX = 2;
    public static final int LOADER_SYMPTOMS = 3;

    private Uri mCurrentRecordUri;
    private Uri mCurrentSymptomUri;

    public static final int SHARE_EMAIL = 0;
    public static final int SHARE_TEXT = 1;


    static final int SYMPtOM_LOADER = 2;

    // load up the Severity Spinner
    public static final String [] SEVERITY = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    // load up the Emotion Spinner
    public static final String[] EMOTIONS = {"None","Anger (grouchy)", "Disgust", "Envy", "Fear (Anxiety)",
            "Happiness", "Jealousy", "Love", "Sadness", "Shame", "Guilt (sorry)"};

    private Button mPickDate, mPickTime;
    private EditText mEditDate, mEditTime;
    private Spinner mSpinSymptom;
    private Spinner mSpinSeverity;
    private EditText mEditTrigger;
    private Spinner mSpinEmotion;
    private Spinner mSpinEmotion2;
    private EditText mEditObservation;
    private EditText mEditOutcome;

    // see if I can change this to private??
    public SimpleCursorAdapter mSpinSymptomAdapter;
    private String mSpinSymptomVal; // holds the value of the spinner symptom
    private String[] mSpinVal = {"","", ""}; // initialization for Serverity, & both Emotion Spinner Values

    private boolean mRecordChanged = false;
    private boolean mHomeChecked;

     // Touch listener to check if changes made to a record
     private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event){
             mRecordChanged = true;
             return false;
         }
     };

     private ShareActionProvider mShareActionProvider;

     private long mMS = 0; //Linux Time in milliseconds
     private String mDateDB;


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
        mSpinEmotion2 = findViewById(R.id.sp_Emotion2);
        mEditObservation = findViewById(R.id.et_observation);
        mEditOutcome = findViewById(R.id.et_outcome);

        // Set up Touch Listener on all the input views to see if user touch the view
        mEditDate.setOnTouchListener(mTouchListener);
        mEditTime.setOnTouchListener(mTouchListener);
        mSpinSymptom.setOnTouchListener(mTouchListener);
        mSpinSeverity.setOnTouchListener(mTouchListener);
        mEditTrigger.setOnTouchListener(mTouchListener);
        mSpinEmotion.setOnTouchListener(mTouchListener);
        mSpinEmotion2.setOnTouchListener(mTouchListener);
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
        // c.close(); gives warning if I don't close it and crashes if I do ??

        mSpinSymptomAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                c,
                new String[]{SymTraxContract.SymptomTableSchema.C_SYMPTOM },
                new int[] {android.R.id.text1},
                0);

        mSpinSymptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinSymptom.setAdapter(mSpinSymptomAdapter);
        mSpinSymptom.setSelection(0, false);
        // intialize mSpinSymptomVal to first item in spinner in case spinner not touched by user
        CursorWrapper w = (CursorWrapper) mSpinSymptom.getItemAtPosition(0);
        mSpinSymptomVal = String.valueOf(w.getString(1));

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
        mSpinEmotion2 = getSpinnerVal(R.id.sp_Emotion2, EMOTIONS, 2);

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
                        C_EMOTION2,
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

                    // use the index to pull the data out
                     mMS = c.getLong(dateColIndex);
                     mDateDB = DateUtility.formatDate(mMS); // hold on to mMS in case other fields are edited!!!
                     // String date = c.getString(dateColIndex);
                     String time = c.getString(timeColIndex);
                     String symptom = c.getString(symptomColIndex);
                     int severity = c.getInt(severityColIndex);
                     String trigger = c.getString(c.getColumnIndex(C_TRIGGER));
                     String emotion = c.getString(c.getColumnIndex(C_EMOTION));
                     String emotion2 = c.getString(c.getColumnIndex(C_EMOTION2));
                     String observation = c.getString(c.getColumnIndex(C_OBSERVATION));
                     String outcome = c.getString(c.getColumnIndex(C_OUTCOME));

                     // CursorWrapper required when working with CursorLoader & SQLite DB
                     // CursorWrapper not required for raw queries
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

                     pos = 0;
                     for (int i=0; i < EMOTIONS.length; i++)
                         //if (EMOTIONS[i].equals( mSpinEmotion.getItemAtPosition(i))){
                         if (emotion.equals( mSpinEmotion.getItemAtPosition(i))){
                             pos = i;
                             break;
                         }
                     mSpinEmotion.setSelection(pos);

                     pos = 0;
                     for (int i=0; i < EMOTIONS.length; i++)
                         //if (EMOTIONS[i].equals( mSpinEmotion.getItemAtPosition(i))){
                         if (emotion2.equals( mSpinEmotion2.getItemAtPosition(i))){
                             pos = i;
                             break;
                         }
                     mSpinEmotion2.setSelection(pos);


                     mEditDate.setText(mDateDB);
                     mEditTime.setText(time);
                     mSpinSeverity.setSelection(severity);
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
                     mShareActionProvider.setShareIntent(shareData(SHARE_EMAIL, false));
                 }
                 // Intent.createChooser(i, " Create Chooser");
                 Log.v(LOG_TAG, "in action share EMail after String Builder");
                 return true;
             case R.id.action_share_text:
                 if (mShareActionProvider != null){
                     mShareActionProvider.setShareIntent(shareData(SHARE_TEXT, false));
                 }
                 return true;
             case R.id.action_delete:
                 // Alert Dialog for deleting one record
                 // showDeleteConfirmationDialog();
                 showDeleteConfirmationDialogFrag();
                 return true;
             // this is the <- button on the header
             case android.R.id.home:
                 // record has not changed
                 if (!mRecordChanged) {
                     NavUtils.navigateUpFromSameTask(AddEditSymTraxActivity.this);
                     return true;
                 }
                 mHomeChecked = true;
                 showUnsavedChangesDialogFragment();
                 return true;
         }
         return super.onOptionsItemSelected(item);
     }


     // Override the activity's normal back button. If book has changed create a
     // discard click listener that closed current activity.
     @Override
     public void onBackPressed() {
         if (!mRecordChanged) {
             super.onBackPressed();
             return;
         }
         //otherwise if there are unsaved changes setup a dialog to warn the  user
         //handles the user confirming that changes should be made
         mHomeChecked = false;
         showUnsavedChangesDialogFragment();
     }


     private void saveRecord() {

         // read from input fields. Add date & time to get correct sort order for date + time
         String strDateTime = mEditDate.getText().toString() + " " + mEditTime.getText().toString();

         // convert string date to Linux date
         SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US);
         // long ms = 0
         try {
             Date d = f.parse(strDateTime);
             mMS = d.getTime(); // date-time in ms since 1/1/1970
         } catch (ParseException e) {
             e.printStackTrace();
         }

         // read from EditText and Spinner input fields
         // String date = mEditDate.getText().toString();
         String time = mEditTime.getText().toString();
         String symptom  = mSpinSymptomVal;
         String severity = mSpinVal[0];
         String trigger = mEditTrigger.getText().toString().trim();
         String emotion = mSpinVal[1];
         String emotion2 = mSpinVal[2];
         String observation = mEditObservation.getText().toString().trim();
         String outcome = mEditOutcome.getText().toString().trim();

         // if the date field is left blank do nothing
         if (mCurrentRecordUri == null & TextUtils.isEmpty(strDateTime)) {
             Toast.makeText(this, getString(R.string.missing_date), Toast.LENGTH_SHORT).show();
             return;
         }

         ContentValues values = new ContentValues();
         values.put(C_DATE, mMS);
         values.put(C_TIME, time);
         values.put(SymTraxContract.SymTraxTableSchema.C_SYMPTOM, symptom);
         values.put(C_SEVERITY, severity);
         values.put(C_TRIGGER, trigger);
         values.put(C_EMOTION, emotion);
         values.put(C_EMOTION2, emotion2);
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
     public void deleteRecord(){
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


     // Load spinners with values from an array and set up Listener
    private Spinner getSpinnerVal(int resourceId, final String[] cs, final int i){
        mSpinVal[i] = "";
        Spinner sp = findViewById(resourceId);
        ArrayAdapter<String> csAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cs);
        // Specify the layout to use when the list of choices appear
        csAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(csAdapter); // apply the adapter to the spinner

        //CursorWrapper w = (CursorWrapper) mSpinSymptom.getItemAtPosition(0);
        //mSpinSymptomVal = String.valueOf(w.getString(1));
        // initialize mSpinSymptomVal to first item in spinner in case spinner not touched by user
        mSpinVal[i] =  (String) sp.getItemAtPosition(0);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                Log.v("RecordActivity", selection);
                mSpinVal[i] = selection;  //store the selection for future reference
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


    private void showUnsavedChangesDialogFragment(){
        DialogFragment dfus = new DialogFragmentUnsavedChanges();
        dfus.show(getSupportFragmentManager(), "Unsaved Changes Dialog Fragment");
    }

    public void onDiscardClick(){
        //user click discard. Navigate up to parent activity
        if (mHomeChecked){  // at Home Checkmark
            NavUtils.navigateUpFromSameTask(AddEditSymTraxActivity.this);
        }
        if (!mHomeChecked) { // at BackPressed
            finish();
        }
    }


     private void showDeleteConfirmationDialogFrag() {
         DialogFragment df = new DialogFragDeleteConfirmation();
         df.show(getSupportFragmentManager(), "Delete Symptom");
     }

     public void onDeleteClick(){
        deleteRecord();
     }



     // get thu current values shown on screen
     public StringBuilder buildShareString(){

         StringBuilder sb = new StringBuilder();

         mEditDate = findViewById(R.id.et_date);
         mEditTime =  findViewById(R.id.et_time);
         // currently getting value already stored- not from spinner???
         // mSpinSymptom = findViewById(R.id.sp_symptom);
         mSpinSeverity = getSpinnerVal(R.id.sp_severity, SEVERITY, 0);
         mEditTrigger = findViewById(R.id.et_trigger);
         mSpinEmotion = getSpinnerVal(R.id.sp_Emotion, EMOTIONS, 1);
         mEditObservation = findViewById(R.id.et_observation);
         mEditOutcome = findViewById(R.id.et_outcome);

         sb.append(mDateDB).append("   ")
                 .append(mEditTime.getText()).append("\n")
                 .append("Symptom: ").append(mSpinSymptomVal).append("\n")
                 .append("Severity: ").append(mSpinVal[0]).append("\n")
                 .append("Trigger: ").append(mEditTrigger.getText().toString()).append("\n")
                 .append("Emotion: ").append(mSpinVal[1]).append("\n")
                 .append("Emotion 2: ").append(mSpinVal[1]).append("\n")
                 .append("Observation: ").append(mEditObservation.getText().toString()).append("\n")
                 .append("Outcome: ").append(mEditOutcome.getText().toString()).append("\n");

         Log.v(LOG_TAG, "String Builder " + sb);

         return sb;
     }




     private Intent shareData(int shareType, boolean dbRecords){

         StringBuilder sb = buildShareString();
         ShareDataHelper shareFragment = new ShareDataHelper();
         Intent intent;
         if (shareType == SHARE_TEXT) {
             intent = shareFragment.shareText(mShareActionProvider, sb);
         }else{
             intent = shareFragment.shareEMail(mShareActionProvider, sb);
         }
         return intent;
     }



}






