package edu.babarehner.android.symtrax.data;//
// Created by Mike Rehner on 2/11/19.
// Apache LIcense 2.0


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class SymTraxContract {

    // To prevent someone from accidentally instantiating thecontract class
    private SymTraxContract() { }

    //TODO Determine if the following 4 need to be private or are Public as I believe????
    // use package name for convenience for the Content Authority
    private static final String SYM_TRAX_AUTHORITY = "edu.babarehner.android.symtrax";

    // Use SYM_TRAX_AUTHORITY to create the base of all Uri's which apps use
    // to contact the content provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" +
            SYM_TRAX_AUTHORITY);
    private static final String PATH_SYM_TRAX_TABLE_NAME = "TSymTrax";
    private static final String PATH_SYMPTOMS_TABLE_NAME ="TSymptoms";


    // Inner class that defines SymTrax table and columns
    public static final class SymTraxTableDesign implements BaseColumns {

        // MIME type of the (@link #CONTENT_URI for a stuff database table
        public static final String SYM_TRAX_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + SYM_TRAX_AUTHORITY + "/" + PATH_SYM_TRAX_TABLE_NAME;
        // MIME type of the (@link #CONTENT_URI for a single record
        public static final String SYM_TRAX_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + SYM_TRAX_AUTHORITY + "/" + PATH_SYM_TRAX_TABLE_NAME;
        // Content URI to access the table data in the provider
        public static final Uri SYM_TRAX_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_SYM_TRAX_TABLE_NAME);

        public static final String MACHINE_TABLE_NAME = "TSymTrax";

        // the globals and the columns
        public static final String _IDST = BaseColumns._ID;
        public static final String CDATE = "CDate";
        public static final String CTIME = "CTime";
        public static final String C_SYMPTOM = "CSymptom";
        public static final String C_SEVERITY = "CSeverity";  //should I make this an integer
        public static final String C_TRIGGER = "CTrigger";
        public static final String C_EMOTION = "CEmotion";
        public static final String C_OBSERVATION = "CObservation"; // or thoughts
        public static final String C_Outcome= "COutcome";

    }


    public static final class SymptomTableDesign implements BaseColumns {

        // MIME type of the (@link #CONTENT_URI for a stuff database table
        public static final String SYMPTOM_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + SYM_TRAX_AUTHORITY + "/" + PATH_SYMPTOMS_TABLE_NAME;
        // MIME type of the (@link #CONTENT_URI for a single record
        public static final String SYM_TRAX_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + SYM_TRAX_AUTHORITY + "/" + PATH_SYM_TRAX_TABLE_NAME;
        // Content URI to access the table data in the provider
        public static final Uri EQUIP_TYPE_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_SYM_TRAX_TABLE_NAME);

        public static final String SYMPtOMs_TABLE_NAME = "TSymptoms";

        // the globals and the columns
        public static final String _IDS = BaseColumns._ID;
        public static final String C_SYMPtOM = "CSymptom";
    }
}
