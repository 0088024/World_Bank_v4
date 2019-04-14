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


    public void addRow(String rowDate, String rowValue)
    {
        SQLiteDatabase db = dbhelper.getWritableDatabase();	/*ottiene il riferimento al database*/
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(DbHelper.COLUMN_DATE, rowDate);
            values.put(DbHelper.COLUMN_VALUE, rowValue);
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


    public void deleteRow(long rowDate)
    {
        SQLiteDatabase db = dbhelper.getWritableDatabase();	/*ottiene il riferimento al database*/
        db.beginTransaction();
        try{
            db.delete(DbHelper.TABLE_NAME, DbHelper.COLUMN_DATE + "=" + rowDate,
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




    public void save(String date, String value)
    {
        SQLiteDatabase db = dbhelper.getWritableDatabase();	/*ottiene il riferimento al database*/
        ContentValues val = new ContentValues();
        val.put(DbHelper.COLUMN_DATE, date);
        val.put(DbHelper.COLUMN_VALUE, value);
        db.beginTransaction();

        try
        {
            db.insert(DbHelper.TABLE_NAME, null, val);
            db.setTransactionSuccessful();

        }
        catch (SQLiteException sqle){
            /*Gestione delle eccezioni*/
        }
        finally {
            db.endTransaction();
        }

    }


    public boolean delete(long id)
    {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.beginTransaction();

        try
        {
            if (db.delete(DbHelper.TABLE_NAME, DbHelper.COLUMN_DATE + "=?",
                    new String[]{Long.toString(id)})>0)
                return true;
            return false;
        }
        catch (SQLiteException sqle){
            return false;
        }
        finally {
            db.setTransactionSuccessful();

            db.endTransaction();
        }
    }


    public Cursor query()
    {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            crs=db.query(DbHelper.TABLE_NAME, null, null, null,
                    null, null, null, null);
        }
        catch(SQLiteException sqle)
        {   return null;
        }

        return crs;
    }
}

