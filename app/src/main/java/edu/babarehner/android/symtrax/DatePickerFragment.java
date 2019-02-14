package edu.babarehner.android.symtrax;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);

        return dpd;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        EditText et = getActivity().findViewById(R.id.et_date);
        et.setText(month + "/" + day + "/" + year);
    }
}
