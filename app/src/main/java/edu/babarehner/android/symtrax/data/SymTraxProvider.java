 /*
 * Created by Mike Rehner on 2/11/19.
 * Copyright (C) 2019 Mike Rehner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.babarehner.android.symtrax.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.security.Provider;

import static edu.babarehner.android.symtrax.data.SymTraxContract.PATH_SYMPTOMS_TABLE_NAME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.PATH_SYM_TRAX_TABLE_NAME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SYM_TRAX_AUTHORITY;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.SYM_TRAX_ITEM_TYPE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.SYM_TRAX_LIST_TYPE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema.SYM_TRAX_TABLE_NAME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymTraxTableSchema._IDST;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOMS_TABLE_NAME;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOM_ITEM_TYPE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema.SYMPTOM_LIST_TYPE;
import static edu.babarehner.android.symtrax.data.SymTraxContract.SymptomTableSchema._IDS;


 public class SymTraxProvider extends ContentProvider {

    public static final String LOG_TAG = SymTraxProvider.class.getSimpleName();

     private static final int SYMTRAXES = 100;
     private static final int SYMTRAX_ID = 101;
     private static final int SYMPTOMS = 200;
     private static final int SYMPTOMS_ID = 201;

     private SymTraxDBHelper mDBHelper;

     private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

     static {
         sUriMatcher.addURI(SYM_TRAX_AUTHORITY, PATH_SYM_TRAX_TABLE_NAME, SYMTRAXES);
         sUriMatcher.addURI(SYM_TRAX_AUTHORITY, PATH_SYM_TRAX_TABLE_NAME + "/#",SYMTRAX_ID);
         sUriMatcher.addURI(SYM_TRAX_AUTHORITY, PATH_SYMPTOMS_TABLE_NAME, SYMPTOMS);
         sUriMatcher.addURI(SYM_TRAX_AUTHORITY, PATH_SYMPTOMS_TABLE_NAME + "/#", SYMPTOMS_ID );
     }


     @Override
     public boolean onCreate() {
         mDBHelper = new SymTraxDBHelper(getContext());
         return true;
     }


     @Nullable
     @Override
     public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                         @Nullable String[] selectionArgs, @Nullable String sortOrder) {
         // Create or open a database to write to it
         SQLiteDatabase db = mDBHelper.getReadableDatabase();

         Cursor c;

         int match = sUriMatcher.match(uri);
         switch (match) {
             case SYMTRAXES:
                 c = db.query(SYM_TRAX_TABLE_NAME, projection, selection, selectionArgs, null,
                         null, sortOrder);
                 break;
             case SYMTRAX_ID:
                 selection = _IDST + "=?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 c = db.query(SYM_TRAX_TABLE_NAME, projection, selection, selectionArgs,
                         null, null, sortOrder);
                 break;
             case SYMPTOMS:
                 c = db.query(SYMPTOMS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                 break;
             case SYMPTOMS_ID:
                 selection = _IDS + "=?";
                 selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                 c = db.query(SYMPTOMS_TABLE_NAME, projection, selection, selectionArgs,
                         null, null, sortOrder);
                 break;
             default:
                 throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
         }

         // notify if the data at this URI changes, Then we need to update the cursor listener
         // attached is automatically notified with uri
         c.setNotificationUri(getContext().getContentResolver(), uri);

         return c;
     }


     @Nullable
     @Override
     public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
         final int match = sUriMatcher.match((uri));
         switch (match) {
             case SYMTRAXES :
                 return insertSymTraxRecord(uri, values);
             case SYMPTOMS:
                 return insertSymptomRecord(uri, values);
             default:
                 throw new IllegalArgumentException("Insertion is not supported for: " + uri);
         }
     }


     // Insert a record into the SymTrax table with the given content values. Return the new content uri
     // for that specific row in the database
     public Uri insertSymTraxRecord(Uri uri, ContentValues values) {

         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         long id = db.insert(SYM_TRAX_TABLE_NAME, null, values);
         Log.v(LOG_TAG, "Record not entered");
         if (id == -1) {
             Log.e(LOG_TAG, "Failed to insert row for " + uri);
             return null;
         }

         // notify all listeners that the data has changed for the TSTUFF table
         getContext().getContentResolver().notifyChange(uri, null);
         // return the new Uri with the ID of the newly inserted row appended to the db
         return ContentUris.withAppendedId(uri, id);
     }


     // Insert a record into the Symptoms table with the given content values. Return the new content uri
     // for that specific row in the database
     public Uri insertSymptomRecord(Uri uri, ContentValues values) {

         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         long id = db.insert(SYMPTOMS_TABLE_NAME, null, values);
         Log.v(LOG_TAG, "Record not entered");
         if (id == -1) {
             Log.e(LOG_TAG, "Failed to insert row for " + uri);
             return null;
         }

         // notify all listeners that the data has changed for the TSTUFF table
         getContext().getContentResolver().notifyChange(uri, null);
         // return the new Uri with the ID of the newly inserted row appended to the db
         return ContentUris.withAppendedId(uri, id);
     }


     @Override
     public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
         final int match = sUriMatcher.match(uri);
         switch (match) {
             case SYMTRAXES:
                 return updateSymTraxRecords(uri, values, selection, selectionArgs);
             case SYMTRAX_ID:
                 selection = _IDST + "=?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 return updateSymTraxRecords(uri, values, selection, selectionArgs);
             case SYMPTOMS:
                 return updateSymptomsRecords(uri, values, selection, selectionArgs);
             case SYMPTOMS_ID:
                 selection = _IDS +"=?";
                 selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                 return updateSymptomsRecords(uri, values, selection, selectionArgs);
             default:
                 throw new IllegalArgumentException("Update is not supported for: " + uri);
         }
     }


     private int updateSymTraxRecords(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
         // if there are no values quit
         if (values.size() == 0) {
             return 0;
         }

         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         int rows_updated = db.update(SYM_TRAX_TABLE_NAME, values, selection, selectionArgs);
         if (rows_updated != 0) {
             getContext().getContentResolver().notifyChange(uri, null);
         }
         return rows_updated;
     }


     private int updateSymptomsRecords(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
         // if there are no values quit
         if (values.size() == 0) {
             return 0;
         }

         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         int rows_updated = db.update(SYMPTOMS_TABLE_NAME, values, selection, selectionArgs);
         if (rows_updated != 0) {
             getContext().getContentResolver().notifyChange(uri, null);
         }
         return rows_updated;
     }


     @Override
     public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
         int rowsDeleted;
         SQLiteDatabase db = mDBHelper.getWritableDatabase();
         final int match = sUriMatcher.match(uri);

         switch (match) {
             case SYMTRAX_ID:
                 selection = _IDST + "=?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 rowsDeleted = db.delete(SYM_TRAX_TABLE_NAME, selection, selectionArgs);
                 break;
             case SYMPTOMS_ID:
                 selection = _IDS + "=?";
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 rowsDeleted = db.delete(SYMPTOMS_TABLE_NAME, selection, selectionArgs);
                 break;
             default:
                 throw new IllegalArgumentException("Deletion is not supported for: " + uri);
         }

         if (rowsDeleted != 0) {
             // Notify all listeners that the db has changed
             getContext().getContentResolver().notifyChange(uri, null);
         }

         return rowsDeleted;
     }


     @Nullable
     @Override
     public String getType(@NonNull Uri uri) {
         final int match = sUriMatcher.match(uri);
         switch (match) {
             case SYMTRAXES:
                 return SYM_TRAX_LIST_TYPE;
             case SYMTRAX_ID:
                 return SYM_TRAX_ITEM_TYPE;
             case SYMPTOMS:
                 return SYMPTOM_LIST_TYPE;
             case SYMPTOMS_ID:
                 return SYMPTOM_ITEM_TYPE;
             default:
                 throw new IllegalStateException("Unknown Uri: " + uri + "with match: " + match);
         }
     }



 }
