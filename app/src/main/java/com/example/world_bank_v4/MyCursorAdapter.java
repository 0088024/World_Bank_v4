package com.example.world_bank_v4;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;




public class MyCursorAdapter extends CursorAdapter {

    private CaricaDati caricaDati;


    public MyCursorAdapter(Context context, Cursor c, int flags, CaricaDati caricaDati) {
        super(context, c, flags);
        this.caricaDati = caricaDati;
    }



    @Override
    public View newView(Context ctx, Cursor arg1, ViewGroup arg2)
    {
        View v = caricaDati.getLayoutInflater().inflate(R.layout.riga_layout_database, null);
        return v;
    }



    @Override
    public void bindView(View v, Context arg1, Cursor crs)
    {
        /*String oggetto=crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_SUBJECT));
        String data=crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_DATE));*/

        String nomePaese = crs.getString(3);
        String nomeIndicatore = crs.getString(4);

        TextView txt = v.findViewById(R.id.txt_Paese);
        txt.setText(nomePaese);
        txt = v.findViewById(R.id.txt_Indicatore);
        txt.setText(nomeIndicatore);

        ImageButton imgbtn=(ImageButton) v.findViewById(R.id.btn_delete);
        /*imgbtn.setOnClickListener(clickListener);*/
    }



    @Override
    public long getItemId(int position)
    {
        Cursor crs= getCursor();
        crs.moveToPosition(position);
        /*return crs.getLong(crs.getColumnIndex(DatabaseStrings.FIELD_ID));*/
        return 0;
    }
}
