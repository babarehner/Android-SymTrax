package edu.babarehner.android.symtrax;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.AddEditSymTraxActivity.SHARE_TEXT;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_DATE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.SYM_TRAX_URI;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema._IDST;

public class SymTraxActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final int SYMTRAX_LOADER = 0;
    SymTraxCursorAdapter mCursorAdapter;

    private ShareActionProvider mShareActionProvider;
    public static final int SHARE_EMAIL = 0;
    public static final int SHARE_TEXT = 1;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symtrax);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SymTraxActivity.this, AddEditSymTraxActivity.class);
                startActivity(intent);
                // keeping this around to show me snackbar syntax
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        Button buttonSymptom = findViewById(R.id.button1);
        Button buttonEmotion = findViewById(R.id.button2);
        Button buttonDate = findViewById(R.id.button3);
        buttonSymptom.setOnClickListener(this);
        buttonEmotion.setOnClickListener(this);
        buttonDate.setOnClickListener(this);


        ListView symtraxListView = findViewById(R.id.list_symtrax);
        View emptyView = findViewById(R.id.empty_subtitle_text);
        symtraxListView.setEmptyView(emptyView);

        mCursorAdapter = new SymTraxCursorAdapter(this, null);
        symtraxListView.setAdapter(mCursorAdapter);
        symtraxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent intent = new Intent(SymTraxActivity.this, AddEditSymTraxActivity.class);
                Uri currentMainUri = ContentUris.withAppendedId(
                        SYM_TRAX_URI, id);
                intent.setData(currentMainUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(SYMTRAX_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = SymTraxSort.SORT_ORDER;
        String[] projection = {_IDST,
                C_DATE,
                C_EMOTION,
                SymTraxContract.SymTraxTableSchema.C_SYMPTOM};

        return new CursorLoader(this,
                SYM_TRAX_URI,
                projection,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    // Handle multiple buttons clicks
    @Override
    public void onClick(View v){
        // using global var for sortOrder, requires static method and variables
        SymTraxSort.setSortOrder(v);
        getLoaderManager().restartLoader(SYMTRAX_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_symtrax, m);

        //Create divider line in menu groups
        MenuCompat.setGroupDividerEnabled(m, true);

        // relate mShareActionProvider to share e-mail menu item
        // initialize mShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider
                (m.findItem(R.id.action_share_entire_db));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Context cntxt = this;
        ShareDataHelper bckupDB = new ShareDataHelper();
        switch (item.getItemId()) {
            //case (R.id.action_settings):
            //    return true;
            case (R.id.action_symptom_activity):
                Intent intent = new Intent(SymTraxActivity.this, SymptomActivity.class);
                startActivity(intent);
                return true;
            case (R.id.action_share_entire_db):
                StringBuilder sb = bckupDB.buildDBString(cntxt);
                if (mShareActionProvider != null) {
                    // returns an intent
                    mShareActionProvider.setShareIntent(bckupDB.shareEMail(mShareActionProvider, sb));
                }
                return true;
            case (R.id.action_backup_db_to_storage):
                checkWritePermission();
                bckupDB.backupDB( cntxt );
                return true;
            case (R.id.action_save_db_to_csv):
                checkWritePermission();
                bckupDB.writeCSVfile(cntxt);
                return true;
            /* Not implemented at this time
            case (R.id.action_set_filters):
                Intent filterIntent = new Intent(SymTraxActivity.this, FilterActivity.class);
                startActivity(filterIntent);
                return true;

             */
        }
        return super.onOptionsItemSelected(item);
    }


    public void checkWritePermission(){

        // Get public external storage folder ( /storage/emulated/0 ).
        // File externalDir = Environment.getExternalStorageDirectory();

        // Get /storage/emulated/0/Music folder.
        // File docPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(SymTraxActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(SymTraxActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Write permission granted for External Storage. Please return to Backup menu item.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Write external storage permission not granted. Unable to back up item.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
