package com.example.world_bank_v4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


/*classe con cui interagioamo con il database: select, insert, delete, etc.*/
public class DbManager {

    private DbHelper dbhelper;				                /*riferimento all’helper*/


    public DbManager(Context ctx)
    {
        dbhelper = new DbHelper(ctx);       /*istanzia l’helper: creazione e popolazione iniziale
                                            del database*/
    }


    public void addRow(RecordTabella recordTabella)
    {
        SQLiteDatabase db = dbhelper.getWritableDatabase();	/*ottiene il riferimento al database*/
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            /*il primo argomento è il nome della colonna dove inserire il secondo argomento come
            valore*/
            values.put(DbHelper.COLUMN_ID_PAESE, recordTabella.getIdPaese());
            values.put(DbHelper.COLUMN_ID_INDICATORE, recordTabella.getIdIndicatore());
            values.put(DbHelper.COLUMN_NOME_PAESE, recordTabella.getNomePaese());
            values.put(DbHelper.COLUMN_NOME_INDICATORE, recordTabella.getNomeIndicatore());
            values.put(DbHelper.COLUMN_SOURCE_ID, recordTabella.getSourceId());
            values.put(DbHelper.COLUMN_LAST_UPDATED, recordTabella.getLastUpdated());

            /*inserisce i valori per tutti gli anni*/
            for(int i = 0; i<recordTabella.getColonne_anni().size(); i++){
                Float value = recordTabella.getColonne_anni().get(i).getvalue();
                values.put(DbHelper.COLUMN_DATE + (DbHelper.ANNO_INIZIO + i), value.toString());
            }

            db.insert(DbHelper.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        }
        catch(Exception e){
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }


    public void deleteRow(long id)
    {
        SQLiteDatabase db = dbhelper.getWritableDatabase();	/*ottiene il riferimento al database*/
        db.beginTransaction();
        try{
            db.delete(DbHelper.TABLE_NAME, DbHelper.COLUMN_ID + "=" + id,
                    null);
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }





    public boolean delete(long id)
    {
        SQLiteDatabase db = dbhelper.getWritableDatabase(); /*ottiene il riferimento al database
                                                            in scrittura*/
        db.beginTransaction();

        try
        {
            if (db.delete(DbHelper.TABLE_NAME, DbHelper.COLUMN_ID + "=?",
                    new String[]{Long.toString(id)})>0)
                return true;
            return false;
        }
        catch (SQLiteException sqle){
            Log.e("DB ERROR", sqle.toString());
            sqle.printStackTrace();
        }
        finally {
            db.setTransactionSuccessful();

            db.endTransaction();
        }

        return true;
    }



    /*ritorna tutte le righe della tabella DbHelper.TABLE_NAME*/
    public Cursor query()
    {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db = dbhelper.getReadableDatabase();	/*ottiene il riferimento al database
                                                                in lettura*/
            crs=db.query(DbHelper.TABLE_NAME, null, null, null,
                    null, null, null, null);
        }
        catch(SQLiteException sqle)
        {   return null;
        }

        return crs;
    }


    /*ritorna la righa della con il dato id*/
    public Cursor query(long id)
    {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db = dbhelper.getReadableDatabase();	/*ottiene il riferimento al database
                                                                in lettura*/
            crs=db.query(DbHelper.TABLE_NAME, null, DbHelper.COLUMN_ID +" = " +id,
                    null, null, null,  null, null);
        }
        catch(SQLiteException sqle)
        {   return null;
        }

        return crs;
    }


    /*rilascia il puntatore all'oggetto, chiudendolo se l'ultima referenziazione è rilasciata*/
    public void close(){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if(db.isOpen())
            db.close();
        else {
            Log.d(Costanti.NOME_APP, "Database non aperto");}
    }


}