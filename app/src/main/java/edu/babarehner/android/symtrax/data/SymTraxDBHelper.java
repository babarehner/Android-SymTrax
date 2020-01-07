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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.babarehner.android.symtrax.data.SymTraxContract;


 public class SymTraxDBHelper extends SQLiteOpenHelper {

     // To allow for changes in DB versioning and keeping user data
     private static final int DB_VERSION = 2;

     private static final String DB_NAME = "sym_trax.db";

     SymTraxDBHelper(Context context) {
         super(context, DB_NAME, null, DB_VERSION);
     }


     @Override
     public void onCreate(SQLiteDatabase sqLiteDatabase) {

         final String SQL_CREATE_SYM_TRAX_TABLE_TABLE = "CREATE TABLE " +
                 SymTraxContract.SymTraxTableSchema.SYM_TRAX_TABLE_NAME + " (" +
                 SymTraxContract.SymTraxTableSchema._IDST + " INTEGER PRIMARY KEY, " +
                 SymTraxContract.SymTraxTableSchema.C_DATE + " DATE_NOT_NULL, " +
                 SymTraxContract.SymTraxTableSchema.C_TIME + " TEXT, " +
                 SymTraxContract.SymTraxTableSchema.C_SYMPTOM + " TEXT, " +
                 SymTraxContract.SymTraxTableSchema.C_SEVERITY + " INTEGER, " +
                 SymTraxContract.SymTraxTableSchema.C_TRIGGER + " TEXT, " +
                 SymTraxContract.SymTraxTableSchema.C_EMOTION + " TEXT, " +
                 SymTraxContract.SymTraxTableSchema.C_EMOTION2 + " TEXT, " +
                 SymTraxContract.SymTraxTableSchema.C_OBSERVATION + " TEXT, " +
                 SymTraxContract.SymTraxTableSchema.C_OUTCOME + " TEXT );";

         sqLiteDatabase.execSQL(SQL_CREATE_SYM_TRAX_TABLE_TABLE);

         final String SQL_CREATE_SYMPTOMS_TABLE = "CREATE TABLE " +
                 SymTraxContract.SymptomTableSchema.SYMPTOMS_TABLE_NAME + " (" +
                 SymTraxContract.SymptomTableSchema._IDS + " INTEGER PRIMARY KEY, " +
                 SymTraxContract.SymptomTableSchema.C_SYMPTOM + " TEXT );";

         sqLiteDatabase.execSQL(SQL_CREATE_SYMPTOMS_TABLE);

         String[] symptoms = {"Crying", "Feel Faint", "Irregular Heartbeat", "Tense Muscles"};


         for (String each : symptoms) {
             sqLiteDatabase.execSQL("INSERT INTO " + SymTraxContract.SymptomTableSchema.SYMPTOMS_TABLE_NAME
                     + " ( " + SymTraxContract.SymptomTableSchema.C_SYMPTOM + " ) "
                     + " VALUES( "
                     + "'" + each + "'" + ");");
         }

     }


     @Override
     public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
         // Note that this only fires if you change the version number for your database.
         // It does NOT depend on the version number for your application.
         // Currently the next line wipes out all user data and starts with a fresh DB Table
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SymTraxContract.SymTraxTableSchema.SYM_TRAX_TABLE_NAME);
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SymTraxContract.SymptomTableSchema.SYMPTOMS_TABLE_NAME);
         onCreate(sqLiteDatabase);
     }

 }