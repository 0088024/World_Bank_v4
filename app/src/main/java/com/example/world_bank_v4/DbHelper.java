package com.example.world_bank_v4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/*classe "ottimizzata" per crezione, apertura e aggiornamento versione database*/
public class DbHelper extends SQLiteOpenHelper {


    /*COSTANTI*/
    public static final String TABLE_NAME = "indicatori_per_paese";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_PAESE = "id_paese";
    public static final String COLUMN_ID_INDICATORE = "id_indicatore";
    public static final String COLUMN_NOME_PAESE = "nome_paese";
    public static final String COLUMN_NOME_INDICATORE = "nome_indicatore";
    public static final String COLUMN_DATE = "data";
    public static final String COLUMN_SOURCE_ID = "source_id";
    public static final String COLUMN_LAST_UPDATED = "last_updated";



    public static final int ANNO_INIZIO = 1960;
    public static final int ANNO_FINE = 2018;

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

        /*costruisci la stringa contenente l'istruzione SQLite per creare la tabella*/
        StringBuilder istruzioneSql = new StringBuilder();
        istruzioneSql.append("CREATE TABLE "+ TABLE_NAME + " (" + COLUMN_ID +
                " Integer PRIMARY KEY autoincrement not null," + COLUMN_ID_PAESE + " text not null,"
                + COLUMN_ID_INDICATORE + " text not null," + COLUMN_NOME_PAESE + " text not null,"
                + COLUMN_NOME_INDICATORE + " text not null," + COLUMN_SOURCE_ID + " text not null,"
                + COLUMN_LAST_UPDATED + " text not null");

        for(int x = ANNO_INIZIO; x<=ANNO_FINE; x++) {
            istruzioneSql.append("," + COLUMN_DATE + x + " REAL ");
        }
        istruzioneSql.append(");");

        db.execSQL(istruzioneSql.toString() );
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}

