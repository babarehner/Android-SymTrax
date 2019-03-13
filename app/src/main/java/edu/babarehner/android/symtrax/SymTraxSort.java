package edu.babarehner.android.symtrax;

import android.view.View;
import android.widget.Button;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_DATE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_SYMPTOM;


// Class controls global variable sort order to allow for sorting of symptom, emotions & dates
class SymTraxSort {

    private static final String DEFAULT_SORT = C_DATE + " DESC";
    // sortOrder used in SymTraxActivity
    static String SORT_ORDER = DEFAULT_SORT;
    private static boolean symptomAsc = true;
    private static boolean emotionAsc = true;
    private static boolean dateAsc = true;


    static void setSortOrder(View v){
        // grab the button in order to flip the text
        Button b = v.findViewById(v.getId());
        switch (v.getId()){
            case R.id.button1:
                if (symptomAsc){
                    SORT_ORDER = C_SYMPTOM + " ASC";
                    b.setText(R.string.symptom_desc);  // flip the text
                }
                else {
                    SORT_ORDER = C_SYMPTOM + " DESC";
                    b.setText(R.string.symptom_asc);   // flip the text
                }
                symptomAsc = !symptomAsc;  // flip the boolean
                break;
            case R.id.button2:
                if (emotionAsc){
                    SORT_ORDER = C_EMOTION + " ASC";
                    b.setText(R.string.emotion_desc);
                }
                else {
                    SORT_ORDER = C_EMOTION + " DESC";
                    b.setText(R.string.emotion_asc);
                }
                emotionAsc = !emotionAsc;
                break;
            case R.id.button3:{
                if (dateAsc){
                    SORT_ORDER = C_DATE + " ASC";
                    b.setText(R.string.date_desc);
                }
                else {
                    SORT_ORDER = C_DATE + " DESC";
                    b.setText(R.string.date_asc);
                }
                dateAsc = !dateAsc;
                break;
            }
        }

    }
}
