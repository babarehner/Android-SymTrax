package edu.babarehner.android.symtrax;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.C_SYMPTOM;


public class SymptomCursorAdapter extends CursorAdapter {

    public SymptomCursorAdapter(Context context, Cursor c) { super( context, c); }
    /**
     * creates a new blank list item with no data
     * @param context   app context
     * @param c         cursor
     * @param parent    parent to which view is attached to
     * @return          the newly created list item view
     */
    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item_symptoms, parent, false);
    }

    /**
     * Binds data to the empty list item
     * @param v View
     * @param context Context
     * @param c Cursor
     */
    @Override
    public void bindView(View v, Context context, Cursor c) {

        TextView symptomTextView = v.findViewById(R.id.list_item_symptom);
        int symptomColIndex = c.getColumnIndex(C_SYMPTOM);
        String symptom = c.getString(symptomColIndex);
        symptomTextView.setText(symptom);


    }
}
