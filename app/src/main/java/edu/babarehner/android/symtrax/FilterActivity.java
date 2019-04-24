package edu.babarehner.android.symtrax;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.LoaderManager;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FilterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


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


    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
