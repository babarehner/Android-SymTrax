package edu.babarehner.android.symtrax;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_DATE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_EMOTION2;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_OBSERVATION;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_OUTCOME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_SEVERITY;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_SYMPTOM;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_TIME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.C_TRIGGER;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.SYM_TRAX_URI;


public class ShareDataHelper {

    private final String LOG_TAG = ShareDataHelper.class.getSimpleName();

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;



    public Intent shareEMail(ShareActionProvider sap, StringBuilder sb){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        if (sap != null){
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "SymTrax info");
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            intent.setData(Uri.parse("mailto: "));
        } else {
            Log.v(LOG_TAG, "Share Action Provider (E-Mail) is most likely null");
        }
        return intent;
    }


    public Intent shareText(ShareActionProvider sap, StringBuilder sb){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String s = sb.toString();
        if (sap != null){
            intent.putExtra("sms_body", s);
            intent.setData(Uri.parse("smsto:"));
        } else {
            Log.v(LOG_TAG, "Share Action Provider (Text) is most likely null");
        }
        return intent;
    }




    public StringBuilder buildDBString(Context context){
        StringBuilder sb = new StringBuilder();
        Cursor c = context.getContentResolver().query(SYM_TRAX_URI,null, null, null, null);
        if (c!= null) {
            while (c.moveToNext()) {
                // convert ms long time to text
                long d = c.getLong(c.getColumnIndexOrThrow(C_DATE));
                Date dateTime = new Date(d);
                SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String date = f.format(dateTime);

                sb.append("Date: ")
                .append(date)
                .append("   Time: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_TIME)))
                .append("\nSymptom: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_SYMPTOM)))
                .append("\nSeverity: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_SEVERITY)))
                .append("\nTrigger: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_TRIGGER)))
                .append("\nEmotion: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_EMOTION)))
                .append("\nEmotion 2: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_EMOTION2)))
                .append("\nObservation: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_OBSERVATION)))
                .append("\nOutcome: ")
                .append(c.getString(c.getColumnIndexOrThrow(C_OUTCOME)))
                .append("\n\n");
            }
            c.close();
        }
        return sb;
    }


    private StringBuilder buildCSVfromDB(Context context){
        StringBuilder csv = new StringBuilder();

        Cursor c = context.getContentResolver().query(SYM_TRAX_URI,null, null, null, null);
        if (c!= null) {
            // add the headers
            csv.append(C_DATE).append(", ").append(C_TIME).append(", ").append(C_SYMPTOM)
                    .append(", ").append(C_SEVERITY).append(", ").append(C_TRIGGER).append(", ")
                    .append(C_EMOTION).append(", ").append(C_EMOTION2).append(", ").append(C_OBSERVATION).append(", ")
                    .append(C_OUTCOME).append("\n");
            while (c.moveToNext()) {
                csv.append(c.getString(c.getColumnIndexOrThrow(C_DATE))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_TIME))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_SYMPTOM))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_SEVERITY))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_TRIGGER))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_EMOTION))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_EMOTION2))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_OBSERVATION))).append(", ")
                        .append(c.getString(c.getColumnIndexOrThrow(C_OUTCOME))).append(", ")
                        .append("\n");
                // TODO better way is to write one line at a time!!!!
            }
            c.close();
        }
        return csv;
    }


    public void writeCSVfile(Context context){

       StringBuilder csv = buildCSVfromDB(context);
       String csvString = csv.toString();


        try {
            File sd = createNewFolder(context);

            if (sd.canWrite()) {
                File file = new File(sd, "symt_trax.csv");
                FileOutputStream fileOutput = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
                outputStreamWriter.write(csvString);
                outputStreamWriter.flush();
                fileOutput.getFD().sync();
                outputStreamWriter.close();
                Toast.makeText(context, "sum_trax csv saved to SymTrax folder", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void backupDB(Context context) {

        try {
            // File sd = Environment.getExternalStorageDirectory();
            File sd = createNewFolder(context);
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/edu.babarehner.android.symtrax/databases/sym_trax.db";
                String backupDBPath = "sym_trax_backup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                Toast.makeText(context, "Database backed up to SymTrax folder as sym_trax_backup.db", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private File createNewFolder(Context context){
        String folder = "SymTrax";
        File f = new File(Environment.getExternalStorageDirectory(), folder);
        if (!f.exists()){
            f.mkdirs();
            Toast.makeText(context, "Folder " + folder + " created", Toast.LENGTH_SHORT).show();
        }
        return f;
    }


    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}

