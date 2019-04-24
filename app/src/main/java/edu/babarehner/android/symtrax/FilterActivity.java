package edu.babarehner.android.symtrax;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOM_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema._IDS;


public class FilterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final int FILTER_LOADER = 4;
    private Uri mCurrentSymptomUri;
    FilterSymptomCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        setTitle("Filters");

        // Damn better remember his complicated conversion
        ArrayList<String> arrayEmotions = new ArrayList(Arrays.asList(Konstants.EMOTIONS));
        FilterEmotionArrayAdapter adapter = new FilterEmotionArrayAdapter(this, arrayEmotions);

        ListView listView = findViewById(R.id.left_list_view_emotion);
        listView.setAdapter(adapter);


        ListView symptomListView = findViewById(R.id.right_list_view_symptom);

        mCursorAdapter = new FilterSymptomCursorAdapter(this, null);
        symptomListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(FILTER_LOADER, null, this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String symptomSortOrder = SymTraxContract.SymptomTableSchema.C_SYMPTOM + " ASC";
        mCurrentSymptomUri = SYMPTOM_URI;
        String[] projectionSymptoms = {_IDS, SymTraxContract.SymptomTableSchema.C_SYMPTOM};
        return new CursorLoader(this, mCurrentSymptomUri, projectionSymptoms, null,
                null, symptomSortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor c) {

        // update(@link EquipTypeCursorAdapter) with this new cursor containing update Equpment Type data
        mCursorAdapter.swapCursor(c);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        // Callback called when the data needs to be deleted- use null
        mCursorAdapter.swapCursor(null);

    }
}
