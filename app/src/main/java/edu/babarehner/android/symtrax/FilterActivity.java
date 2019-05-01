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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
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

        setTitle("Set Filters");

        // have to drop off the 'None' selection in the first[0] position
        String[] FilterEmotions = new String[Konstants.EMOTIONS.length-1];
        for (int i = 1; i< Konstants.EMOTIONS.length; i++)
            FilterEmotions[i-1] = Konstants.EMOTIONS[i];


        // Damn better remember this complicated conversion
        ArrayList<String> arrayEmotions = new ArrayList(Arrays.asList(FilterEmotions));
        FilterEmotionArrayAdapter adapter = new FilterEmotionArrayAdapter(this, arrayEmotions);

        ListView listView = findViewById(R.id.left_list_view_emotion);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id){

                // Set the checkbox when clicking anywhere in the item row
                CheckBox cb = v.findViewById(R.id.list_item_filter_emotion_checkbox);
                if (cb.isChecked()){
                    cb.setChecked(false);
                } else {
                    cb.setChecked(true);
                }

                TextView tv = v.findViewById(R.id.list_item_filter_emotions);
                String s = tv.getText().toString();
                Toast.makeText(getApplicationContext(), "Position: " + pos + "  " + s, Toast.LENGTH_LONG).show();
            }
        });



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

    // Options menu automatically called from onCreate I believe
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_activity, menu);
        return true;
    }

    // Select from the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        switch (menuItem) {
            case R.id.action_save_filters:
                //saveRecord();
                finish();       // exit activity
                return true;
            case R.id.action_delete_filters:
                // Alert Dialog for deleting one record;
                //showDeleteConfirmationDialogFragment();
                // deleteRecord();
                return true;
            // this is the <- button on the toolbar
            case android.R.id.home:
                // record has not changed
                //if (!mEditSymptomChanged) {
                // NavUtils.navigateUpFromSameTask(EditSymptomActivity.this);
                //    return true;
                //}

                // show user they have unsaved changes
                //mHomeChecked = true;
                //showUnsavedChangesDialogFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
