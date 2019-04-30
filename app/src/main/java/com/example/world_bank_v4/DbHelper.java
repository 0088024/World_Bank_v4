package com.example.world_bank_v4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/*classe "ottimizzata" per crezione, apertura e aggiornamento versione database*/
public class DbHelper extends SQLiteOpenHelper {


    /*COSTANTI*/
    public static final String TABLE_NAME = "indicatori_per_paese";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_VALUE = "value";


    private static String DB_PATH = "";
    private static String DB_NAME ="WorlBank.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context){
        super(context, DB_NAME, null, DATABASE_VERSION);
    }


    /*SQLite è typeless con una sola eccezione: il tipo di dato INTEGER PRIMARY KEY, che pretende,
    sempre, un tipo di dato signed integer a 32-bit. Tentando di inserire un tipo di dato diverso,
    si riceverà un messaggio di errore.*/
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String newTableQueryString  = "CREATE TABLE "+ TABLE_NAME + " (" + COLUMN_ID +
                " Integer PRIMARY KEY autoincrement not null," + COLUMN_DATE + " text not null, " +
                COLUMN_VALUE + " REAL );";
        db.execSQL(newTableQueryString );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}

