package edu.babarehner.android.symtrax;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
;import java.util.ArrayList;


public class FilterEmotionArrayAdapter extends ArrayAdapter<String> {

    public FilterEmotionArrayAdapter(Activity context, ArrayList<String> EMOtIONS) {
        super(context, 0, EMOtIONS);
    }

    @Override
    public View getView(int pos, View v, ViewGroup parent) {
        //Get the item for this position
        String currentEmotion = getItem(pos);
        // Check if an existing view is being reused
        if (v == null) {
           v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_filter_emotions, parent, false);
        }
        //TODO Add Checkbox & CheckBox Listener:

        // Looking up for data population
        TextView tvEmotion = v.findViewById(R.id.list_item_filter_emotions);
        tvEmotion.setText(currentEmotion);

        return v;
    }
}
