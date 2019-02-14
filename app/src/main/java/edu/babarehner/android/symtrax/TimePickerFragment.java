package edu.babarehner.android.symtrax;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //TODO handle 12/24 hour time using settings & Decide how to store date and time
    public void onTimeSet(TimePicker view, int hours, int minutes) {
        EditText et = getActivity().findViewById(R.id.et_time);
        et.setText(convert24to12(hours, minutes));
    }


    // adds a leading 0 to a time string value
    public static String pad(int c){
        if (c >= 10) {
            return String.valueOf(c);
        }
        else {
            return "0" + String.valueOf(c);
        }
    }


    public static String convert24to12(int hours, int minutes){
        String ampm;

        if (hours > 12) {
            hours -= 12;
            ampm = "PM";
        } else if (hours == 0) {
            hours += 12;
            ampm = "AM";
        } else if (hours == 12)
            ampm = "PM";
        else
            ampm = "AM";
        StringBuilder sb = new StringBuilder().append(Integer.toString(hours)).
                append(":").append(pad(minutes)).append(" ").append(ampm);

        return sb.toString();
    }
}
