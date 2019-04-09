package edu.babarehner.android.symtrax;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.babarehner.android.symtrax.data.SymTraxContract;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_DATE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_TIME;


public class SymTraxCursorAdapter extends CursorAdapter {

    public SymTraxCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }


    @Override
    public View newView(Context context, Cursor c, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item_symtrax, parent,
                false);
    }


    @Override
    public void bindView(View v, Context context, Cursor c) {

        TextView dateTextView = v.findViewById(R.id.list_item_date);
        //TextView timeTextView = v.findViewById(R.id.list_item_time);
        TextView symptomTextView = v.findViewById(R.id.list_item_symptom);
        TextView emotionTextView = v.findViewById(R.id.list_item_emotion);

        int dateColIndex = c.getColumnIndex(C_DATE);
        //int timeColIndex = c.getColumnIndex(C_TIME);
        int symptomColIndex = c.getColumnIndex(SymTraxContract.SymTraxTableSchema.C_SYMPTOM);
        int emotionColIndex = c.getColumnIndex(C_EMOTION);

        // get long datetime for db and convert it to string
        long d = c.getLong(dateColIndex);
        Date dateTime = new Date(d);
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String date = f.format(dateTime);
        //String time = c.getString(timeColIndex);
        String symptom = c.getString(symptomColIndex);
        String emotion = c.getString(emotionColIndex);

        dateTextView.setText(date);
        //timeTextView.setText(time);
        symptomTextView.setText(symptom);

        emotionTextView.setText(emotion);
    }
}
