package com.example.world_bank_v4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/*classe "ottimizzata" per crezione, apertura e aggiornamento versione database*/
public class DbHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDataBase;
    private Context mContext;

    /*COSTANTI*/
    public static final String TABLE_NAME = "prova_indicatore";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_VALUE = "value";


    private static String DB_PATH = "";
    private static String DB_NAME ="WorlBank.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context){
        super(context, DB_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String newTableQueryString  ="CREATE TABLE "+ TABLE_NAME + "(" + COLUMN_DATE +
                " Integer PRIMARY KEY not null," + " COLUMN_VALUE_" + " REAL);";
        db.execSQL(newTableQueryString );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}

