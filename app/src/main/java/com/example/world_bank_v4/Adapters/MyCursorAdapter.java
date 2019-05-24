package com.example.world_bank_v4.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.world_bank_v4.Activities.CaricaDatiActivity;
import com.example.world_bank_v4.Controller.DbHelper;
import com.example.world_bank_v4.R;


public class MyCursorAdapter extends CursorAdapter {

    private CaricaDatiActivity caricaDatiActivity;


    public MyCursorAdapter(Context context, Cursor c, int flags, CaricaDatiActivity caricaDatiActivity) {
        super(context, c, flags);
        this.caricaDatiActivity = caricaDatiActivity;
    }



    @Override
    public View newView(Context ctx, Cursor arg1, ViewGroup arg2)
    {
        View v = caricaDatiActivity.getLayoutInflater().inflate(R.layout.riga_layout_database, null);
        return v;
    }



    @Override
    public void bindView(View v, Context arg1, Cursor crs)
    {
        /*String oggetto=crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_SUBJECT));
        String data=crs.getString(crs.getColumnIndex(DatabaseStrings.FIELD_DATE));*/

        String nomePaese = crs.getString(3);
        String nomeIndicatore = crs.getString(4);

        TextView txt = v.findViewById(R.id.textViewPaese);
        txt.setText(nomePaese);
        txt = v.findViewById(R.id.textViewNazione);
        txt.setText(nomeIndicatore);
        ImageButton buttonDelete = v.findViewById(R.id.imageButtonDelete);
        buttonDelete.setOnClickListener(caricaDatiActivity);
        ImageButton buttonDati = v.findViewById(R.id.imageButtonDati);
        buttonDati.setOnClickListener(caricaDatiActivity);

    }



    /*Fornisce l’id del record in base alla posizione e viene usato x completare le condizioni di
    selezione richieste x la cancellazione.*/
    @Override
    public long getItemId(int position)
    {
        Cursor crs= getCursor();
        crs.moveToPosition(position);
        return crs.getLong(crs.getColumnIndex(DbHelper.COLUMN_ID));
    }
}
