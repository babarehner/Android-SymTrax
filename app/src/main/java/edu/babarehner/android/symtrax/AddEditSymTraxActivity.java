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
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_DATE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_OBSERVATION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_OUTCOME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_SEVERITY;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_SYMPTOM;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_TIME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_TRIGGER;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema._IDST;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOM_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema._IDS;


 public class AddEditSymTraxActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = AddEditSymTraxActivity.class.getSimpleName();

    public static final int LOADER_SYMTRAX = 0;
    public static final int LOADER_SYMPTOMS = 1;

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
    private Spinner spSymptom, spSeverity;
    private EditText mEditTrigger;
    private Spinner spEmotion;
    private EditText mEditObservation;
    private EditText mEditOutcome;

    // see if I can change this to private??
    public SimpleCursorAdapter mSymptomSpinAdapter;
    private String mSymptomSpinVal = "";

    private boolean mRecordChanged = false;

     // Touch listener to check if changes made to a record
     private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event){
             mRecordChanged = true;
             return false;
         }
     };

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
        } else {
            setTitle(getString(R.string.add_edit_sym_trac_activity_title_edit_record));
            getLoaderManager().initLoader(LOADER_SYMTRAX, null, AddEditSymTraxActivity.this);
        }

        // grab the views
        mPickDate = findViewById(R.id.pick_date);
        mPickTime = findViewById(R.id.pick_time);
        mEditDate = findViewById(R.id.et_date);
        mEditTime = findViewById(R.id.et_time);
        spSymptom = findViewById(R.id.sp_symptom);
        spSeverity = findViewById(R.id.sp_severity);
        mEditTrigger = findViewById(R.id.et_trigger);
        spEmotion = findViewById(R.id.sp_Emotion);
        mEditObservation = findViewById(R.id.et_observation);
        mEditOutcome = findViewById(R.id.et_outcome);

        // Set up Touch Listener on all the input views to see if user touch the view
        mEditDate.setOnTouchListener(mTouchListener);
        mEditTime.setOnTouchListener(mTouchListener);
        spSymptom.setOnTouchListener(mTouchListener);
        spSeverity.setOnTouchListener(mTouchListener);
        mEditTrigger.setOnTouchListener(mTouchListener);
        spEmotion.setOnTouchListener(mTouchListener);
        mEditObservation.setOnTouchListener(mTouchListener);
        mEditOutcome.setOnTouchListener(mTouchListener);

        // Load the symptom spinner
        spSymptom = findViewById(R.id.sp_symptom);
        // Get a cursor to hold the data to load the spinner
        Cursor c = getContentResolver().query(
                SYMPTOM_URI,
                null,
                null,
                null,
                C_SYMPTOM + " ASC");
        // load the spin adpater with the data
        mSymptomSpinAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                c,
                new String[]{SymTraxContract.SymptomTableSchema.C_SYMPTOM },
                new int[] {android.R.id.text1},
                0);

        mSymptomSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSymptom.setAdapter(mSymptomSpinAdapter);
        spSymptom.setSelection(0, false);

        spSymptom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // CursorWrapper required when working with CursorLoader & SQLite DB
                CursorWrapper cw = (CursorWrapper) parent.getItemAtPosition(pos);
                mSymptomSpinVal = String.valueOf(cw.getString(1));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //    mSpinVal = "";
            }
        });

        // load the rest of the spinners
        spSeverity = getSpinnerVal(R.id.sp_severity, SEVERITY);
        spEmotion = getSpinnerVal(R.id.sp_Emotion, EMOTIONS);

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
                String[] projectionSymptoms = {_IDS, C_SYMPTOM};
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
                     CursorWrapper cw;
                     int pos = 0;
                     for (int i = 0; i < spSymptom.getCount(); i++){
                         cw = (CursorWrapper) spSymptom.getItemAtPosition(i);
                         if (String.valueOf(cw.getString(1)).equals
                                 (symptom)){
                             pos = i;
                             break;
                         }
                     }

                     spSymptom.setSelection(pos);

                     mEditDate.setText(date);
                     mEditTime.setText(time);
                     mEditTrigger.setText(trigger);
                     mEditObservation.setText(observation);
                     mEditOutcome.setText(outcome);
                 }
                 break;
             case LOADER_SYMPTOMS:
                 mSymptomSpinAdapter.swapCursor(c);
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
                 mSymptomSpinAdapter.swapCursor(null);
                 break;
         }
     }


    // Load spinners with values from and array and set up Listener
    private Spinner getSpinnerVal(int resourceId, final CharSequence[] cs){
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
                // spin_val[i] = selection;
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
