package edu.babarehner.android.symtrax;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
            case (R.id.action_settings):
                return true;
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
                bckupDB.backupDB( cntxt );
                return true;
            case (R.id.action_save_db_to_csv):
                bckupDB.writeCSVfile(cntxt);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
