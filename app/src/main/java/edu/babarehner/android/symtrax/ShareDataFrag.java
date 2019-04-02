package edu.babarehner.android.symtrax;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;


public class ShareDataFrag extends Fragment {

    public final String LOG_TAG = ShareDataFrag.class.getSimpleName();

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

}

