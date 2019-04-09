package edu.babarehner.android.symtrax;

import java.text.DateFormat;
import java.util.Date;



/**
 * Used to store some utility functions
 */
public class DateUtility {

    // adds a leading 0 to a time string value
    public static String pad(int c){
        if (c >= 10) {
            return String.valueOf(c);
        }
        else {
            return "0" + String.valueOf(c);
        }
    }


    public static String convert24to12(int hours){

        String ampm;
        if (hours > 12) {
            // hours -= 12;
            ampm = "PM";
        } else if (hours == 0) {
            // hours += 12;
            ampm = "AM";
        } else if (hours == 12)
            ampm = "PM";
        else
            ampm = "AM";

        return ampm;
    }

    // formats long 1/1/1970 format to string date format
    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }

}


