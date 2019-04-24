package edu.babarehner.android.symtrax;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOM_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema._IDS;


public class SymptomActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SYMPTOM_LOADER = 3;
    private Uri mCurrentSymptomUri;
    SymptomCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        setTitle("Symptoms");

        // Create a floating action button Need to add
        // compile 'com.android.support:design:26.1.0' to build gradle module
        FloatingActionButton fab = findViewById(R.id.symptom_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SymptomActivity.this, EditSymptomActivity.class);
                startActivity(intent);
            }
        });

        ListView symptomListView = findViewById(R.id.list_item_symptoms);
        // display the empty view
        View emptyView = findViewById(R.id.equipment_type_empty_view);
        symptomListView.setEmptyView(emptyView);

        mCursorAdapter = new SymptomCursorAdapter(this, null);
        symptomListView.setAdapter(mCursorAdapter);

        symptomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent intent = new Intent(SymptomActivity.this, EditSymptomActivity.class);
                Uri currentEquipTypeUri = ContentUris.withAppendedId(
                        SYMPTOM_URI, id);
                intent.setData(currentEquipTypeUri);
                startActivity(intent);

            }
        });


        getLoaderManager().initLoader(SYMPTOM_LOADER, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String symptomSortOrder = SymTraxContract.SymptomTableSchema.C_SYMPTOM + " ASC";
        mCurrentSymptomUri = SYMPTOM_URI;
        String[] projectionSymptoms = {_IDS, SymTraxContract.SymptomTableSchema.C_SYMPTOM};
        return new CursorLoader(this, mCurrentSymptomUri, projectionSymptoms, null,
                null, symptomSortOrder);


    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        // update(@link EquipTypeCursorAdapter) with this new cursor containing update Equpment Type data
        mCursorAdapter.swapCursor(c);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted- use null
        mCursorAdapter.swapCursor(null);
    }
}



